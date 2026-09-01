# Logging (Ayurvedaa API)

## What is in use today

All Spring Boot services use **Logback** (Spring Boot’s default), not Log4j / Log4j2.

| Item | Status |
|------|--------|
| Framework | **Logback** via `spring-boot-starter-logging` (transitive from `spring-boot-starter-web` / JPA / etc.) |
| API used in code | **SLF4J** (`@Slf4j` / `LoggerFactory`) |
| `log4j*.xml` / `log4j*.properties` | **None** |
| `logback*.xml` | **None** (Boot’s default console config) |
| `spring-boot-starter-log4j2` | **Not present** in any `pom.xml` |
| Explicit Log4j / Log4j2 deps | **None** |

Parent POM: Spring Boot **3.5.3** (`ayurvedaa-services`).

### Per-service inventory

| Module | Framework | Config files | `logging.*` in `application.yml` |
|--------|-----------|--------------|----------------------------------|
| common-service | Logback (library) | — | n/a (not a runnable app) |
| auth-service | Logback | none | yes (+ `org.springframework.security`) |
| patient-service | Logback | none | yes |
| doctor-service | Logback | none | yes |
| therapist-service | Logback | none | yes |
| appointment-service | Logback | none | yes |
| medicine-service | Logback | none | yes |
| billing-service | Logback | none | yes |
| file-upload-service | Logback | none | yes |
| notification-service | Logback | none | yes |
| activity-log-service | Logback | none | yes |
| attendance-service | Logback | none | yes (same pattern) |

Typical `application.yml` block today:

```yaml
logging:
  level:
    org.springframework.web: INFO
    org.hibernate.SQL: DEBUG
```

`auth-service` also sets `org.springframework.security: INFO`.

There is **no broken / partial Log4j2 setup** — Log4j is simply not installed. Application logging is healthy on Logback.

---

## How to change log levels (no framework switch)

Edit each service’s `src/main/resources/application.yml` (or an active profile YAML / env vars). Spring Boot maps `logging.level.*` to the active logging system (Logback today).

### Recommended for local / FE integration (dev)

```yaml
logging:
  level:
    root: INFO
    com.ayurveda: DEBUG          # your app packages
    org.springframework.web: INFO
    org.springframework.security: INFO   # useful for auth-service
    org.hibernate.SQL: DEBUG             # SQL only; noisy
    org.hibernate.orm.jdbc.bind: TRACE   # bind params (optional, very noisy)
```

### Recommended for production-ish / quieter console

```yaml
logging:
  level:
    root: INFO
    com.ayurveda: INFO
    org.springframework.web: WARN
    org.hibernate.SQL: WARN
```

### Override without editing YAML

```bash
# env
LOGGING_LEVEL_COM_AYURVEDA=DEBUG

# or JVM
-Dlogging.level.com.ayurveda=DEBUG
```

Actuator health is already exposed; you can also change levels at runtime via Actuator `loggers` if you enable that endpoint (not required for normal FE/dev work).

---

## If you later switch to Log4j2

Only do this if you explicitly want Log4j2. Today’s Logback setup is correct and sufficient.

1. In each runnable service `pom.xml` (or parent dependencyManagement):

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
  <exclusions>
    <exclusion>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-logging</artifactId>
    </exclusion>
  </exclusions>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

Exclude the default logging starter from **every** Boot starter that brings it in (web, actuator, test, etc.), or exclude once at the parent and add `spring-boot-starter-log4j2`.

2. Put optional config at:

`{service}/src/main/resources/log4j2.xml`  
(or `log4j2-spring.xml` for Spring Boot–aware config)

3. Keep using SLF4J in Java (`@Slf4j`). Do **not** call Log4j APIs directly.

4. `logging.level.*` in `application.yml` continues to work with Log4j2 under Spring Boot.

---

## Note on “activity-log-service”

That module is an **audit/activity API**, not the Java logging framework. Runtime logs still go through Logback like every other service.
