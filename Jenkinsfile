
timestamps {
    node {
        properties([
            buildDiscarder(logRotator(numToKeepStr: '3')),
            disableConcurrentBuilds()
        ])

        env.PROJECT_NAME = 'ayurvedaa-api'
        env.DOCKER_ORG = 'sunardock'
        env.DEPLOY_SERVER = '45.195.229.15'
        env.DEPLOY_PATH = '/root/ayurvedaa'

        def branchName = env.BRANCH_NAME ?: 'unknown'

        def services = [
            'patient-service',
            'doctor-service',
            'appointment-service',
            'therapist-service',
            'file-upload-service',
            'attendance-service',
            'activity-log-service',
            'medicine-service',
            'billing-service',
            'notification-service',
            'auth-service',
            'payment-service',
            'api-gateway'
        ]

        env.SERVICE_LIST = services.join(' ')

        // ==========================================
        // MICROSOFT TEAMS NOTIFICATION HELPER
        // DEV BRANCH ONLY
        // ==========================================

        def sendTeamsNotification = { String status, String title ->

            def notificationFile =
                "teams-notification-${env.BUILD_NUMBER}.json"

            try {

                def payload = [
                    type: 'message',
                    attachments: [[
                        contentType: 'application/vnd.microsoft.card.adaptive',
                        content: [
                            '$schema': 'http://adaptivecards.io/schemas/adaptive-card.json',
                            type: 'AdaptiveCard',
                            version: '1.4',
                            body: [
                                [
                                    type: 'TextBlock',
                                    text: title,
                                    weight: 'Bolder',
                                    size: 'Large',
                                    wrap: true
                                ],
                                [
                                    type: 'TextBlock',
                                    text: "Ayurvedaa API - ${status}",
                                    wrap: true
                                ],
                                [
                                    type: 'FactSet',
                                    facts: [
                                        [title: 'Project', value: 'Ayurvedaa API'],
                                        [title: 'Branch', value: branchName],
                                        [title: 'Build', value: "#${env.BUILD_NUMBER}"],
                                        [title: 'Status', value: status]
                                    ]
                                ]
                            ],
                            actions: [[
                                type: 'Action.OpenUrl',
                                title: 'View Jenkins Build',
                                url: env.BUILD_URL
                            ]]
                        ]
                    ]]
                ]

                writeFile(
                    file: notificationFile,
                    text: groovy.json.JsonOutput.toJson(payload)
                )

                withCredentials([
                    string(
                        credentialsId: 'teams-jenkins-webhook',
                        variable: 'TEAMS_WEBHOOK_URL'
                    )
                ]) {
                
                    withEnv(["NOTIFICATION_FILE=${notificationFile}"]) {
                
                        sh '''
                            set +x
                
                            HTTP_CODE=$(curl -sS \
                                --connect-timeout 10 \
                                --max-time 30 \
                                -o /dev/null \
                                -w "%{http_code}" \
                                -H "Content-Type: application/json" \
                                --data-binary @"$NOTIFICATION_FILE" \
                                "$TEAMS_WEBHOOK_URL")
                
                            echo "Teams HTTP response: $HTTP_CODE"
                
                            case "$HTTP_CODE" in
                                2*)
                                    echo "Teams notification accepted."
                                    ;;
                                *)
                                    echo "ERROR: Teams notification failed."
                                    exit 1
                                    ;;
                            esac
                        '''
                    }
                }

                echo "Teams ${status} notification sent successfully."

            } catch (Exception notificationError) {

                echo "WARNING: Teams notification could not be delivered."
                echo "Notification error: ${notificationError.getMessage()}"

            } finally {

                sh(
                    returnStatus: true,
                    script: "rm -f '${notificationFile}'"
                )
            }
        }

        try {

            // ==========================================
            // MAVEN TOOL CONFIGURATION
            // ==========================================

            def mvnHome = tool 'Maven'
            env.PATH = "${mvnHome}/bin:${env.PATH}"

            // ==========================================
            // CHECKOUT SOURCE
            // ==========================================

            stage('Checkout Source') {

                checkout scm

                env.SAFE_BRANCH = branchName
                    .replaceAll('[^a-zA-Z0-9_.-]', '-')
                    .replaceAll('-+', '-')
                    .replaceAll('^-|-$', '')
                    .toLowerCase()

                if (!env.SAFE_BRANCH) {
                    env.SAFE_BRANCH = 'unknown'
                }

                env.IMAGE_TAG = "${env.SAFE_BRANCH}-${env.BUILD_NUMBER}"

                echo '=========================================='
                echo 'AYURVEDAA API MULTIBRANCH PIPELINE'
                echo "Branch: ${branchName}"
                echo "Image Tag: ${env.IMAGE_TAG}"
                echo '=========================================='
            }

            // ==========================================
            // ENVIRONMENT CHECK
            // ==========================================

            stage('Environment Check') {

                sh '''
                    set -eu

                    command -v java
                    command -v mvn
                    command -v git
                    command -v docker
                    command -v ssh
                    command -v scp

                    echo "Environment check completed."
                '''
            }

            // ==========================================
            // MAVEN TEST & PACKAGE
            // ==========================================

            stage('Maven Test & Package') {

                sh '''
                    set -eu

                    mvn -B clean package -DskipTests=false

                    echo "Maven build and tests completed."
                '''
            }

            // ==========================================
            // VERIFY DOCKERFILES
            // ==========================================

            stage('Verify Dockerfiles') {

                sh '''
                    set -eu

                    for service in $SERVICE_LIST; do

                        test -f "./${service}/Dockerfile"

                        echo "Verified: ${service}/Dockerfile"

                    done

                    test -f docker-compose.yml

                    if ! grep -q '^  api-gateway:' docker-compose.yml; then
                        echo "ERROR: api-gateway missing from docker-compose.yml"
                        exit 1
                    fi

                    echo "Dockerfiles and Compose file verified."
                '''
            }

            // ==========================================
            // DEV BRANCH DEPLOYMENT
            // ==========================================

            if (branchName == 'dev') {

                // ==========================================
                // SONARQUBE ANALYSIS
                // ==========================================

                stage('SonarQube Analysis') {

                    withSonarQubeEnv('SonarQube') {

                        sh '''
                            set -eu

                            mvn -B sonar:sonar \
                                -Dsonar.projectKey=com.ayurveda:ayurvedaa-services \
                                -Dsonar.projectName="Ayurvedaa-backend"

                            echo "SonarQube analysis completed."
                        '''
                    }
                }

                // ==========================================
                // DOCKER LOGIN
                // ==========================================

                stage('Docker Login') {

                    withCredentials([
                        usernamePassword(
                            credentialsId: 'dockerhub-creds',
                            usernameVariable: 'DOCKER_USERNAME',
                            passwordVariable: 'DOCKER_PASSWORD'
                        )
                    ]) {

                        sh '''
                            set -eu

                            trap 'docker logout >/dev/null 2>&1 || true' EXIT

                            login_attempt=1
                            max_login_attempts=4
                            delay=10

                            while [ "$login_attempt" -le "$max_login_attempts" ]; do

                                echo "Docker Hub login attempt $login_attempt/$max_login_attempts"

                                if printf '%s' "$DOCKER_PASSWORD" |
                                    docker login \
                                        --username "$DOCKER_USERNAME" \
                                        --password-stdin; then

                                    echo "Docker Hub login successful."
                                    break
                                fi

                                if [ "$login_attempt" -eq "$max_login_attempts" ]; then
                                    echo "ERROR: Docker Hub login failed."
                                    exit 1
                                fi

                                echo "Login failed. Retrying in $delay seconds..."

                                sleep "$delay"

                                login_attempt=$((login_attempt + 1))
                                delay=$((delay * 2))
                            done
                        '''
                    }
                }

                // ==========================================
                // BUILD DOCKER IMAGES
                // ==========================================

                stage('Build Docker Images') {

                    sh '''
                        set -eu

                        for service in $SERVICE_LIST; do

                            image="${DOCKER_ORG}/${PROJECT_NAME}-${service}:${IMAGE_TAG}"

                            echo "Building: $image"

                            docker build -t "$image" "./${service}"

                        done

                        echo "All Docker images built successfully."
                    '''
                }

                // ==========================================
                // PUSH DOCKER IMAGES
                // PARALLEL PUSH: 3 AT A TIME
                // FOUR RETRIES PER IMAGE
                // ==========================================

                stage('Push Docker Images') {

                    withCredentials([
                        usernamePassword(
                            credentialsId: 'dockerhub-creds',
                            usernameVariable: 'DOCKER_USERNAME',
                            passwordVariable: 'DOCKER_PASSWORD'
                        )
                    ]) {

                        sh '''
                            set -eu

                            trap 'docker logout >/dev/null 2>&1 || true' EXIT

                            login_attempt=1
                            max_login_attempts=4
                            delay=10

                            while [ "$login_attempt" -le "$max_login_attempts" ]; do

                                echo "Docker Hub login attempt $login_attempt/$max_login_attempts"

                                if printf '%s' "$DOCKER_PASSWORD" |
                                    docker login \
                                        --username "$DOCKER_USERNAME" \
                                        --password-stdin; then

                                    echo "Docker Hub login successful."
                                    break
                                fi

                                if [ "$login_attempt" -eq "$max_login_attempts" ]; then
                                    echo "ERROR: Docker Hub login failed."
                                    exit 1
                                fi

                                echo "Retrying login in $delay seconds..."

                                sleep "$delay"

                                login_attempt=$((login_attempt + 1))
                                delay=$((delay * 2))
                            done

                            retry_docker_push() {

                                image="$1"
                                attempt=1
                                max_attempts=4
                                delay=10

                                while [ "$attempt" -le "$max_attempts" ]; do

                                    echo "Docker push attempt $attempt/$max_attempts: $image"

                                    if docker push "$image"; then
                                        echo "SUCCESS: Pushed $image"
                                        return 0
                                    fi

                                    if [ "$attempt" -eq "$max_attempts" ]; then
                                        echo "ERROR: Failed to push $image after $max_attempts attempts."
                                        return 1
                                    fi

                                    echo "Push failed. Retrying in $delay seconds..."

                                    sleep "$delay"

                                    attempt=$((attempt + 1))
                                    delay=$((delay * 2))

                                done

                                return 1
                            }

                            MAX_PARALLEL_PUSHES=3

                            PUSH_PIDS=""
                            PUSH_COUNT=0
                            PUSH_FAILED=0

                            wait_for_push_batch() {

                                batch_failed=0

                                echo "=========================================="
                                echo "Waiting for $PUSH_COUNT parallel image pushes"
                                echo "=========================================="

                                for pid in $PUSH_PIDS; do

                                    if wait "$pid"; then
                                        echo "Push process $pid completed."
                                    else
                                        echo "ERROR: Push process $pid failed."
                                        batch_failed=1
                                    fi

                                done

                                if [ "$batch_failed" -ne 0 ]; then
                                    PUSH_FAILED=1
                                fi

                                PUSH_PIDS=""
                                PUSH_COUNT=0
                            }

                            for service in $SERVICE_LIST; do

                                image="${DOCKER_ORG}/${PROJECT_NAME}-${service}:${IMAGE_TAG}"

                                echo "Starting parallel push: $image"

                                retry_docker_push "$image" &

                                PUSH_PIDS="$PUSH_PIDS $!"
                                PUSH_COUNT=$((PUSH_COUNT + 1))

                                if [ "$PUSH_COUNT" -ge "$MAX_PARALLEL_PUSHES" ]; then
                                    wait_for_push_batch
                                fi

                            done

                            if [ "$PUSH_COUNT" -gt 0 ]; then
                                wait_for_push_batch
                            fi

                            if [ "$PUSH_FAILED" -ne 0 ]; then
                                echo "ERROR: One or more Docker images failed to push."
                                exit 1
                            fi

                            echo "=========================================="
                            echo "ALL 13 DOCKER IMAGES PUSHED SUCCESSFULLY"
                            echo "=========================================="
                        '''
                    }
                }

                // ==========================================
                // MONITORING SERVER IMAGE RETENTION
                // CURRENT IMAGE ONLY PER SERVICE
                // ==========================================

                stage('Monitoring Server Image Retention') {

                    sh '''
                        set -eu

                        echo "=========================================="
                        echo "MONITORING SERVER IMAGE RETENTION"
                        echo "Policy: Current image only"
                        echo "Current tag: ${IMAGE_TAG}"
                        echo "=========================================="

                        for service in $SERVICE_LIST; do

                            repo="${DOCKER_ORG}/${PROJECT_NAME}-${service}"
                            current="${repo}:${IMAGE_TAG}"

                            echo ""
                            echo "Processing: $repo"

                            if ! docker image inspect "$current" >/dev/null 2>&1; then
                                echo "ERROR: Current image not found: $current"
                                exit 1
                            fi

                            echo "KEEP: $current"

                            image_refs=$(
                                docker image ls \
                                    --format '{{.Repository}}:{{.Tag}}' |
                                awk -v prefix="${repo}:" \
                                    'index($0, prefix) == 1'
                            )

                            for image in $image_refs; do

                                if [ "$image" = "$current" ]; then
                                    continue
                                fi

                                if docker ps -a \
                                    --filter "ancestor=$image" \
                                    --format '{{.ID}}' |
                                    grep -q .; then

                                    echo "SKIP: $image (referenced by a container)"
                                    continue
                                fi

                                echo "DELETE: $image"

                                docker image rm "$image" ||
                                    echo "WARNING: Could not remove $image"

                            done

                        done

                        echo "Monitoring server image retention completed."
                    '''
                }

                // ==========================================
                // DEPLOY APPLICATION
                // SSH PRIVATE KEY AUTHENTICATION
                // ==========================================

                stage('Deploy Application') {

                    withCredentials([
                        sshUserPrivateKey(
                            credentialsId: 'new-server-ssh',
                            keyFileVariable: 'SSH_KEY',
                            usernameVariable: 'SSH_USERNAME'
                        )
                    ]) {

                        sh '''
                            set -eu

                            command -v ssh
                            command -v scp

                            test -f docker-compose.yml

                            echo "Transferring Docker Compose file..."

                            scp -i "$SSH_KEY" \
                                -o IdentitiesOnly=yes \
                                -o BatchMode=yes \
                                -o StrictHostKeyChecking=yes \
                                docker-compose.yml \
                                "$SSH_USERNAME@$DEPLOY_SERVER:$DEPLOY_PATH/docker-compose.yml"

                            echo "Compose file transferred successfully."

                            ssh -i "$SSH_KEY" \
                                -o IdentitiesOnly=yes \
                                -o BatchMode=yes \
                                -o StrictHostKeyChecking=yes \
                                "$SSH_USERNAME@$DEPLOY_SERVER" \
                                "IMAGE_TAG='$IMAGE_TAG' APP_DIR='$DEPLOY_PATH' bash -s" <<'REMOTE_SCRIPT'

set -eu

echo "=========================================="
echo "AYURVEDAA API DEPLOYMENT"
echo "Image Tag: ${IMAGE_TAG}"
echo "=========================================="

exec 9>/var/lock/ayurvedaa-api-deployment.lock

flock -n 9 || {
    echo "ERROR: Another deployment is already running."
    exit 1
}

cd "$APP_DIR"

if [ ! -f .env ]; then
    echo "ERROR: Server .env file not found."
    exit 1
fi

COMPOSE_FILE="docker-compose.yml"

if [ ! -f "$COMPOSE_FILE" ]; then
    echo "ERROR: Docker Compose file not found."
    exit 1
fi

if grep -q '^IMAGE_TAG=' .env; then
    sed -i "s/^IMAGE_TAG=.*/IMAGE_TAG=${IMAGE_TAG}/" .env
else
    printf '\\nIMAGE_TAG=%s\\n' "$IMAGE_TAG" >> .env
fi

echo "Validating Compose configuration..."

docker compose --env-file .env -f "$COMPOSE_FILE" config --quiet

if ! docker compose --env-file .env -f "$COMPOSE_FILE" config --services |
    grep -Fxq api-gateway; then

    echo "ERROR: api-gateway is missing from Compose configuration."
    exit 1
fi

echo "API Gateway found."

retry_docker_pull() {

    attempt=1
    max_attempts=4
    delay=10

    while [ "$attempt" -le "$max_attempts" ]; do

        echo "Docker Compose pull attempt $attempt/$max_attempts"

        if docker compose --env-file .env -f "$COMPOSE_FILE" pull; then
            echo "All Docker Compose images pulled successfully."
            return 0
        fi

        if [ "$attempt" -eq "$max_attempts" ]; then
            echo "ERROR: Docker Compose image pull failed."
            return 1
        fi

        echo "Pull failed. Retrying in $delay seconds..."

        sleep "$delay"

        attempt=$((attempt + 1))
        delay=$((delay * 2))

    done
}

echo "Pulling updated images..."

retry_docker_pull

echo "Starting updated services..."

docker compose --env-file .env -f "$COMPOSE_FILE" up -d --remove-orphans

echo "Deployment command completed."

docker compose --env-file .env -f "$COMPOSE_FILE" ps

REMOTE_SCRIPT
                        '''
                    }
                }

                // ==========================================
                // DEPLOYMENT VERIFICATION
                // ==========================================

                stage('Deployment Verification') {

                    withCredentials([
                        sshUserPrivateKey(
                            credentialsId: 'new-server-ssh',
                            keyFileVariable: 'SSH_KEY',
                            usernameVariable: 'SSH_USER'
                        )
                    ]) {

                        sh '''
                            set -eu

                            ssh -i "$SSH_KEY" \
                                -o IdentitiesOnly=yes \
                                -o BatchMode=yes \
                                -o StrictHostKeyChecking=yes \
                                "$SSH_USER@$DEPLOY_SERVER" \
                                "IMAGE_TAG='$IMAGE_TAG' APP_DIR='$DEPLOY_PATH' bash -s" <<'REMOTE'

set -eu

cd "$APP_DIR"

if [ ! -f .env ] || [ ! -f docker-compose.yml ]; then
    echo "ERROR: .env or docker-compose.yml missing."
    exit 1
fi

COMPOSE_FILE="docker-compose.yml"

echo "=========================================="
echo "AYURVEDAA API DEPLOYMENT VERIFICATION"
echo "Image Tag: ${IMAGE_TAG}"
echo "=========================================="

docker compose --env-file .env -f "$COMPOSE_FILE" config --quiet

if ! docker compose --env-file .env -f "$COMPOSE_FILE" config --services |
    grep -Fxq api-gateway; then

    echo "ERROR: api-gateway missing from Compose configuration."
    exit 1
fi

SERVICES="
patient-service
doctor-service
appointment-service
therapist-service
file-upload-service
attendance-service
activity-log-service
medicine-service
billing-service
notification-service
auth-service
payment-service
api-gateway
"

for service in $SERVICES; do

    container_ids=$(
        docker compose --env-file .env -f "$COMPOSE_FILE" \
            ps --all -q "$service"
    )

    if [ -z "$container_ids" ]; then
        echo "ERROR: No container found for $service"
        exit 1
    fi

    set -- $container_ids

    if [ "$#" -ne 1 ]; then
        echo "ERROR: Expected one container for $service, found $#"
        exit 1
    fi

    container_id=$1

    running=$(docker inspect --format '{{.State.Running}}' "$container_id")

    if [ "$running" != "true" ]; then
        echo "ERROR: $service is not running"
        docker logs --tail 50 "$container_id" || true
        exit 1
    fi

    expected_image="sunardock/ayurvedaa-api-${service}:${IMAGE_TAG}"

    actual_image=$(docker inspect --format '{{.Config.Image}}' "$container_id")

    if [ "$actual_image" != "$expected_image" ]; then
        echo "ERROR: Unexpected image for $service"
        echo "Expected: $expected_image"
        echo "Actual: $actual_image"
        exit 1
    fi

    echo "OK: $service is running with ${IMAGE_TAG}"

done

echo "=========================================="
echo "All 13 API services passed verification."
echo "=========================================="

REMOTE
                        '''
                    }
                }

                // ==========================================
                // APP SERVER IMAGE RETENTION
                // CURRENT + 2 NEWEST OTHER IMAGES
                // ==========================================

                stage('App Server Image Retention') {

                    withCredentials([
                        sshUserPrivateKey(
                            credentialsId: 'new-server-ssh',
                            keyFileVariable: 'SSH_KEY',
                            usernameVariable: 'SSH_USER'
                        )
                    ]) {

                        sh '''
                            set -eu

                            ssh -i "$SSH_KEY" \
                                -o IdentitiesOnly=yes \
                                -o BatchMode=yes \
                                -o StrictHostKeyChecking=yes \
                                "$SSH_USER@$DEPLOY_SERVER" \
                                "IMAGE_TAG='$IMAGE_TAG' APP_DIR='$DEPLOY_PATH' bash -s" <<'REMOTE'

set -euo pipefail

cd "$APP_DIR"

SERVICES=(
    patient-service
    doctor-service
    appointment-service
    therapist-service
    file-upload-service
    attendance-service
    activity-log-service
    medicine-service
    billing-service
    notification-service
    auth-service
    payment-service
    api-gateway
)

echo "=========================================="
echo "APPLICATION SERVER IMAGE RETENTION"
echo "Policy: Current + 2 newest other images"
echo "Branch restriction: NONE"
echo "Current tag: ${IMAGE_TAG}"
echo "=========================================="

for service in "${SERVICES[@]}"; do

    repo="sunardock/ayurvedaa-api-${service}"
    current="${repo}:${IMAGE_TAG}"

    if ! docker image inspect "$current" >/dev/null 2>&1; then
        echo "ERROR: Current image not found: $current"
        exit 1
    fi

    echo ""
    echo "Processing repository: $repo"
    echo "KEEP: $current (current deployment)"

    mapfile -t TAGS < <(
        while IFS= read -r tag; do

            [ -n "$tag" ] || continue

            created=$(docker image inspect \
                --format '{{.Created}}' "${repo}:${tag}")

            printf '%s\\t%s\\n' "$created" "$tag"

        done < <(
            docker image ls "$repo" --format '{{.Tag}}' |
                grep -v '^<none>$' || true
        ) |
        LC_ALL=C sort -r -k1,1 |
        cut -f2-
    )

    previous_kept=0

    for tag in "${TAGS[@]}"; do

        image="${repo}:${tag}"

        if [ "$image" = "$current" ]; then
            continue
        fi

        if [ "$previous_kept" -lt 2 ]; then

            previous_kept=$((previous_kept + 1))

            echo "KEEP: $image (recent image ${previous_kept} of 2)"

            continue
        fi

        if docker ps -a \
            --filter "ancestor=$image" \
            --format '{{.ID}}' |
            grep -q .; then

            echo "SKIP: $image (referenced by a container)"
            continue
        fi

        echo "DELETE: $image"

        docker image rm "$image" ||
            echo "WARNING: Could not remove $image"

    done

    echo "Completed: $repo"

done

echo "=========================================="
echo "APPLICATION SERVER IMAGE RETENTION DONE"
echo "=========================================="

REMOTE
                        '''
                    }
                }

            } else {

                // ==========================================
                // NON-DEV BRANCH
                // ==========================================

                echo '=========================================='
                echo 'NON-DEV BRANCH'
                echo "Branch: ${branchName}"
                echo 'Maven build and tests completed.'
                echo 'Dockerfile validation completed.'
                echo 'SonarQube: SKIPPED'
                echo 'Docker build: SKIPPED'
                echo 'Docker push: SKIPPED'
                echo 'Deployment: SKIPPED'
                echo 'Image retention: SKIPPED'
                echo '=========================================='
            }

            // ==========================================
            // PIPELINE SUCCESS
            // ==========================================

            echo '=========================================='
            echo 'AYURVEDAA API PIPELINE SUCCESS'
            echo "Branch: ${branchName}"
            echo "Build: ${env.BUILD_NUMBER}"
            echo "Image Tag: ${env.IMAGE_TAG}"
            echo '=========================================='

            // ==========================================
            // TEAMS RECOVERY NOTIFICATION
            // DEV BRANCH ONLY
            // ==========================================

            if (branchName == 'dev') {

                def previousResult = currentBuild.previousBuild?.result

                if (previousResult == 'FAILURE' ||
                    previousResult == 'UNSTABLE') {

                    sendTeamsNotification(
                        'RECOVERED',
                        'AYURVEDAA API BUILD RECOVERED'
                    )

                } else {

                    echo 'No previous failed or unstable build. Recovery notification skipped.'

                }

            } else {

                echo "Teams recovery notification skipped for branch: ${branchName}"

            }

        } catch (err) {

            // ==========================================
            // PIPELINE FAILURE
            // ==========================================

            echo '=========================================='
            echo 'AYURVEDAA API PIPELINE FAILED'
            echo "Branch: ${branchName}"
            echo 'Check Jenkins Console Output.'
            echo '=========================================='

            currentBuild.result = 'FAILURE'

            // ==========================================
            // TEAMS FAILURE NOTIFICATION
            // DEV BRANCH ONLY
            // ==========================================

            if (branchName == 'dev') {

                sendTeamsNotification(
                    'FAILED',
                    'AYURVEDAA API BUILD FAILED'
                )

            } else {

                echo "Teams failure notification skipped for branch: ${branchName}"

            }

            throw err

        } finally {

            if (branchName == 'dev') {
                sh 'docker logout >/dev/null 2>&1 || true'
            }
        }
    }
}
