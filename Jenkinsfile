
timestamps {
    node {
        properties([
            buildDiscarder(logRotator(numToKeepStr: '3')),
            disableConcurrentBuilds()
        ])

        def mvnHome = tool 'Maven'

        env.PROJECT_NAME = 'ayurvedaa-api'
        env.DOCKER_ORG = 'sunardock'
        env.DEPLOY_SERVER = '45.195.229.15'
        env.DEPLOY_PATH = '/root/ayurvedaa'
        env.PATH = "${mvnHome}/bin:${env.PATH}"

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

        try {
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

            stage('Environment Check') {
                sh '''
                    set -eu

                    command -v java
                    command -v mvn
                    command -v git
                    command -v docker
                    command -v sshpass

                    echo "Environment check completed."
                '''
            }

            stage('Maven Test & Package') {
                sh '''
                    set -eu

                    mvn -B clean package -DskipTests=false

                    echo "Maven build and tests completed."
                '''
            }

            stage('Verify Dockerfiles') {
                sh '''
                    set -eu

                    for service in \
                        patient-service \
                        doctor-service \
                        appointment-service \
                        therapist-service \
                        file-upload-service \
                        attendance-service \
                        activity-log-service \
                        medicine-service \
                        billing-service \
                        notification-service \
                        auth-service \
                        payment-service \
                        api-gateway
                    do
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

            if (branchName == 'dev') {

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

                            printf '%s' "$DOCKER_PASSWORD" |
                                docker login \
                                    --username "$DOCKER_USERNAME" \
                                    --password-stdin

                            echo "Docker Hub login successful."
                        '''
                    }
                }

                stage('Build Docker Images') {
                    sh '''
                        set -eu

                        for service in \
                            patient-service \
                            doctor-service \
                            appointment-service \
                            therapist-service \
                            file-upload-service \
                            attendance-service \
                            activity-log-service \
                            medicine-service \
                            billing-service \
                            notification-service \
                            auth-service \
                            payment-service \
                            api-gateway
                        do
                            image="${DOCKER_ORG}/${PROJECT_NAME}-${service}:${IMAGE_TAG}"

                            echo "Building: $image"

                            docker build -t "$image" "./${service}"
                        done

                        echo "All Docker images built successfully."
                    '''
                }

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

                            printf '%s' "$DOCKER_PASSWORD" |
                                docker login \
                                    --username "$DOCKER_USERNAME" \
                                    --password-stdin

                            for service in \
                                patient-service \
                                doctor-service \
                                appointment-service \
                                therapist-service \
                                file-upload-service \
                                attendance-service \
                                activity-log-service \
                                medicine-service \
                                billing-service \
                                notification-service \
                                auth-service \
                                payment-service \
                                api-gateway
                            do
                                image="${DOCKER_ORG}/${PROJECT_NAME}-${service}:${IMAGE_TAG}"

                                echo "Pushing: $image"

                                docker push "$image"
                            done

                            echo "All Docker images pushed successfully."
                        '''
                    }
                }

                // MONITORING SERVER IMAGE RETENTION
                // Runs on the Jenkins execution server.
                // Keeps current build tag and protects container-referenced images.

                stage('Monitoring Server Image Retention') {
                    sh '''
                        set -eu

                        echo "Applying monitoring server image retention..."
                        echo "Policy: current build tag per service."

                        for service in \
                            patient-service \
                            doctor-service \
                            appointment-service \
                            therapist-service \
                            file-upload-service \
                            attendance-service \
                            activity-log-service \
                            medicine-service \
                            billing-service \
                            notification-service \
                            auth-service \
                            payment-service \
                            api-gateway
                        do
                            repo="${DOCKER_ORG}/${PROJECT_NAME}-${service}"
                            current="${repo}:${IMAGE_TAG}"

                            if ! docker image inspect "$current" >/dev/null 2>&1; then
                                echo "ERROR: Current image not found: $current"
                                exit 1
                            fi

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
                                    echo "Keeping container-referenced image: $image"
                                    continue
                                fi

                                echo "Removing older local tag: $image"

                                docker image rm "$image" ||
                                    echo "WARNING: Could not remove $image"
                            done
                        done

                        echo "Monitoring server image retention completed."
                    '''
                }

                // DEPLOY TO APPLICATION SERVER
                // First copies updated Compose file from Jenkins workspace.
                // Then deploys using the existing application server .env.

                stage('Deploy Application') {
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'new-server-ssh',
                            usernameVariable: 'SSH_USERNAME',
                            passwordVariable: 'SSH_PASSWORD'
                        )
                    ]) {
                        sh '''
                            set -eu

                            command -v sshpass
                            test -f docker-compose.yml

                            export SSHPASS="$SSH_PASSWORD"
                            trap 'unset SSHPASS' EXIT

                            echo "=========================================="
                            echo "TRANSFERRING DOCKER COMPOSE FILE"
                            echo "Monitoring Server -> Application Server"
                            echo "=========================================="

                            # Copy updated Compose file from Jenkins workspace.
                            sshpass -e scp \
                                -o StrictHostKeyChecking=no \
                                -o UserKnownHostsFile=/dev/null \
                                docker-compose.yml \
                                "$SSH_USERNAME@$DEPLOY_SERVER:$DEPLOY_PATH/docker-compose.yml"

                            echo "Compose file transferred successfully."

                            # Start deployment on application server.
                            sshpass -e ssh \
                                -o StrictHostKeyChecking=no \
                                -o UserKnownHostsFile=/dev/null \
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

# Update only IMAGE_TAG and preserve all other settings.
if grep -q '^IMAGE_TAG=' .env; then
    sed -i "s/^IMAGE_TAG=.*/IMAGE_TAG=${IMAGE_TAG}/" .env
else
    printf '\\nIMAGE_TAG=%s\\n' "$IMAGE_TAG" >> .env
fi

echo "Validating Compose configuration..."

docker compose --env-file .env -f "$COMPOSE_FILE" config --quiet

echo "Checking API Gateway service..."

if ! docker compose --env-file .env -f "$COMPOSE_FILE" config --services |
    grep -Fxq api-gateway; then
    echo "ERROR: api-gateway is missing from Compose configuration."
    exit 1
fi

echo "OK: API Gateway found in Compose configuration."

echo "Pulling updated images..."

docker compose --env-file .env -f "$COMPOSE_FILE" pull

echo "Starting updated services..."

docker compose --env-file .env -f "$COMPOSE_FILE" up -d --remove-orphans

echo "Deployment command completed."

docker compose --env-file .env -f "$COMPOSE_FILE" ps
REMOTE_SCRIPT
                        '''
                    }
                }

                // DEPLOYMENT VERIFICATION
                // Verifies all 13 API services and their image tags.

                stage('Deployment Verification') {
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'new-server-ssh',
                            usernameVariable: 'SSH_USER',
                            passwordVariable: 'SSH_PASSWORD'
                        )
                    ]) {
                        sh '''
                            set -eu

                            command -v sshpass

                            export SSHPASS="$SSH_PASSWORD"
                            trap 'unset SSHPASS' EXIT

                            sshpass -e ssh \
                                -o StrictHostKeyChecking=no \
                                -o UserKnownHostsFile=/dev/null \
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

# Verify every service.
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
        echo "Actual:   $actual_image"
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

                // APPLICATION SERVER IMAGE RETENTION
                // Keep current dev tag + two newest previous dev tags.
                // Protect images referenced by any container.

                stage('App Server Image Retention') {
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'new-server-ssh',
                            usernameVariable: 'SSH_USER',
                            passwordVariable: 'SSH_PASSWORD'
                        )
                    ]) {
                        sh '''
                            set -eu

                            command -v sshpass

                            export SSHPASS="$SSH_PASSWORD"
                            trap 'unset SSHPASS' EXIT

                            sshpass -e ssh \
                                -o StrictHostKeyChecking=no \
                                -o UserKnownHostsFile=/dev/null \
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
echo "Policy: current + two previous dev tags"
echo "Current tag: ${IMAGE_TAG}"
echo "=========================================="

for service in "${SERVICES[@]}"; do

    repo="sunardock/ayurvedaa-api-${service}"
    current="${repo}:${IMAGE_TAG}"

    if ! docker image inspect "$current" >/dev/null 2>&1; then
        echo "ERROR: Deployed image not found locally: $current"
        exit 1
    fi

    echo "Checking: $repo"
    echo "Keeping current image: $current"

    mapfile -t TAGS < <(
        docker image ls "$repo" --format '{{.Tag}}' |
            grep -E '^dev-[0-9]+$' |
            sort -t- -k2,2nr || true
    )

    previous_kept=0

    for tag in "${TAGS[@]}"; do

        image="${repo}:${tag}"

        if [ "$image" = "$current" ]; then
            continue
        fi

        # Keep two newest previous dev tags.
        if [ "$previous_kept" -lt 2 ]; then
            previous_kept=$((previous_kept + 1))
            echo "Keeping previous image ${previous_kept}: $image"
            continue
        fi

        # Protect images used by running or stopped containers.
        if docker ps -a \
            --filter "ancestor=$image" \
            --format '{{.ID}}' |
            grep -q .; then

            echo "Keeping container-referenced image: $image"
            continue
        fi

        echo "Removing old image tag: $image"

        docker image rm "$image" ||
            echo "WARNING: Could not remove $image"
    done
done

echo "Application server image retention completed."
REMOTE
                        '''
                    }
                }

            } else {

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

            echo '=========================================='
            echo 'AYURVEDAA API PIPELINE SUCCESS'
            echo "Branch    : ${branchName}"
            echo "Build     : ${env.BUILD_NUMBER}"
            echo "Image Tag : ${env.IMAGE_TAG}"
            echo '=========================================='

        } catch (err) {

            echo '=========================================='
            echo 'AYURVEDAA API PIPELINE FAILED'
            echo "Branch: ${branchName}"
            echo 'Check Jenkins Console Output.'
            echo '=========================================='

            currentBuild.result = 'FAILURE'
            throw err

        } finally {

            if (branchName == 'dev') {
                sh 'docker logout >/dev/null 2>&1 || true'
            }
        }
    }
}
