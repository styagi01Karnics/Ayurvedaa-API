# Kafka setup on server (Ayurvedaa API)

This guide covers running Kafka on the deploy host so **payment → billing** auto-completes invoices after a successful PayU callback.

Local smoke (Windows + Docker Desktop) already verified this path with `KAFKA_BOOTSTRAP_SERVERS=localhost:9092`. On the server, prefer the Docker Compose wiring below.

---

## Why Kafka is required

| Step | Service | What happens |
|------|---------|--------------|
| 1 | PayU | Calls payment-service success/failure URL |
| 2 | **payment-service** | Marks link PAID and publishes `PAYMENT_UPDATED` |
| 3 | Kafka topic `ayurveda.payments` | Carries the event |
| 4 | **billing-service** | Consumes event and records part payment → invoice `COMPLETED` |

If Kafka is down (or `KAFKA_ENABLED=false`), PayU can still succeed but the invoice stays **UNPAID** until someone syncs manually.

**notification-service** also consumes the same topic (optional for invoice completion; keep it wired if you use payment-related notifications).

Auth, patient, and other services do **not** need Kafka.

---

## What the code expects

| Item | Value |
|------|--------|
| Topic | `ayurveda.payments` |
| Producer | payment-service |
| Consumers | billing-service (`group-id: billing-service`), notification-service |
| Env toggle | `KAFKA_ENABLED=true` |
| Bootstrap (Docker) | `kafka:29092` |
| Bootstrap (host process on same machine) | `127.0.0.1:9092` |
| Security | **PLAINTEXT only** in current Spring configs (no SASL/SSL in app YAML) |

Compose already injects Kafka env for billing, payment, and notification:

```yaml
KAFKA_BOOTSTRAP_SERVERS: kafka:29092
KAFKA_ENABLED: "true"
```

See root `docker-compose.yml` (`kafka`, `billing-service`, `payment-service`, `notification-service`).

---

## Prerequisites on the server

1. Docker + Docker Compose installed.
2. Deploy checkout that includes the Kafka `kafka:` service and payment/billing Kafka classes.
3. External Docker network exists (compose marks it `external: true`):

```bash
docker network create ayurvedaa-app-net
```

4. PayU callback URLs must reach the **public** payment-service (e.g. `http://<server-ip>:8112/...` or HTTPS via your reverse proxy). Kafka does not replace that callback.

---

## Install / start Kafka

From the directory that contains `docker-compose.yml` and `.env`:

```bash
# 1) Network (once)
docker network create ayurvedaa-app-net || true

# 2) Start Kafka only
docker compose up -d kafka

# 3) Confirm
docker ps --filter name=ayurvedaa-kafka
docker logs ayurvedaa-kafka --tail 50
```

Expected container name: `ayurvedaa-kafka`.

### Listener layout (repo compose)

| Listener | Address | Used by |
|----------|---------|---------|
| INTERNAL | `kafka:29092` | Other containers on `ayurvedaa-app-net` |
| EXTERNAL | `localhost:9092` | Processes on the **same host** (not remote machines) |
| CONTROLLER | `:9093` | KRaft controller (internal) |

Do **not** point remote apps at `http://<public-ip>:9092` with the current advertised listener (`EXTERNAL://localhost:9092`). Keep producers/consumers either:

- inside Docker using `kafka:29092`, or  
- on the same host using `127.0.0.1:9092`.

---

## Wire payment / billing / notification

### A) Services run via Docker Compose (recommended)

```bash
docker compose up -d kafka payment-service billing-service notification-service
```

Verify env inside containers:

```bash
docker inspect ayurvedaa-api-payment-service --format '{{range .Config.Env}}{{println .}}{{end}}' | grep KAFKA
docker inspect ayurvedaa-api-billing-service --format '{{range .Config.Env}}{{println .}}{{end}}' | grep KAFKA
docker inspect ayurvedaa-api-notification-service --format '{{range .Config.Env}}{{println .}}{{end}}' | grep KAFKA
```

You should see:

```text
KAFKA_BOOTSTRAP_SERVERS=kafka:29092
KAFKA_ENABLED=true
```

If Kafka was added after services were first created, recreate them so they pick up the network + env:

```bash
docker compose up -d --force-recreate payment-service billing-service notification-service
```

### B) Services run as host JVMs (not in Docker)

Export (or systemd `Environment=`):

```bash
export KAFKA_ENABLED=true
export KAFKA_BOOTSTRAP_SERVERS=127.0.0.1:9092
```

Restart payment-service, billing-service, and (if used) notification-service. Kafka container must still publish `9092` on the host (`ports: "9092:9092"` in compose).

---

## Topic setup

Topic name: **`ayurveda.payments`**.

Apps can auto-create topics (`missing-topics-fatal: false`). Creating explicitly is safer for production:

```bash
docker exec -it ayurvedaa-kafka kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create --if-not-exists \
  --topic ayurveda.payments \
  --partitions 3 \
  --replication-factor 1

docker exec -it ayurvedaa-kafka kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --describe --topic ayurveda.payments
```

List consumer groups (after traffic):

```bash
docker exec -it ayurvedaa-kafka kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --list
```

Expect groups such as `billing-service` (and notification’s group if enabled).

---

## End-to-end verification

1. Create a small ONLINE invoice and payment link (known-good hospital tenant).
2. Complete PayU payment (live or test, per your merchant mode).
3. Check logs:

```bash
docker logs ayurvedaa-api-payment-service 2>&1 | grep -E 'PayU callback|Published PAYMENT_UPDATED'
docker logs ayurvedaa-api-billing-service 2>&1 | grep -E 'PayuInvoicePaymentApplier|Part payment recorded'
```

4. Confirm API state:

- Payment link → `PAID`
- Invoice → `COMPLETED`, left amount `0`, remarks like `PAYU:<txnid>`

Healthy local reference (already proven once Kafka was up):

- payment: `Published PAYMENT_UPDATED to ayurveda.payments for txnid=...`
- billing: `Recorded PayU payment ... on invoice ...`

---

## Failure modes

| Symptom | Likely cause | Fix |
|---------|--------------|-----|
| PayU SUCCESS, invoice still UNPAID | Kafka down / wrong bootstrap / `KAFKA_ENABLED=false` | Start Kafka; fix env; restart payment + billing |
| Payment cannot publish (timeouts) | Broker unreachable from container | Same Docker network; use `kafka:29092` |
| Billing never consumes | Consumer not assigned / old image without Kafka consumer | Redeploy billing image; check group lag |
| Connection refused to public `:9092` from another machine | Advertised listener is `localhost` | Use Docker-internal clients only, or change advertised listeners (advanced) |
| SASL auth errors | Broker requires SASL; apps are PLAINTEXT | Use repo Bitnami PLAINTEXT Kafka, or add SASL props in Spring (not in repo today) |

---

## Security notes

- Current compose Kafka is **PLAINTEXT** and intended for the private Docker network. Do not expose `9092` to the public internet.
- Prefer firewall allowlist: host-local / Docker bridge only.
- Do not commit broker passwords or live PayU secrets into this doc or git.

---

## Quick checklist (copy/paste)

```bash
docker network create ayurvedaa-app-net || true
docker compose up -d kafka
docker compose up -d payment-service billing-service notification-service

docker ps --filter name=ayurvedaa-kafka
docker inspect ayurvedaa-api-payment-service --format '{{range .Config.Env}}{{println .}}{{end}}' | grep KAFKA
docker inspect ayurvedaa-api-billing-service --format '{{range .Config.Env}}{{println .}}{{end}}' | grep KAFKA

docker exec -it ayurvedaa-kafka kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create --if-not-exists \
  --topic ayurveda.payments \
  --partitions 3 --replication-factor 1
```

Then run one ₹10 (or test) PayU payment and confirm billing auto-completes the invoice.
