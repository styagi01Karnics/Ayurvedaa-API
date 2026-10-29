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
                    echo "Environment check completed."
                '''
            }

            stage('Maven Test & Package') {
                sh '''
                    set -eu
                    mvn clean test package -DskipTests=false
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
                        payment-service
                    do
                        test -f "./${service}/Dockerfile"
                        echo "Verified: ${service}/Dockerfile"
                    done
                '''
            }

            if (branchName == 'dev') {

                stage('SonarQube Analysis') {
                    withSonarQubeEnv('SonarQube') {
                        sh '''
                            set -eu

                            mvn sonar:sonar \
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

                            printf '%s' "$DOCKER_PASSWORD" | docker login \
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
                            payment-service
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

                            printf '%s' "$DOCKER_PASSWORD" | docker login \
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
                                payment-service
                            do
                                image="${DOCKER_ORG}/${PROJECT_NAME}-${service}:${IMAGE_TAG}"

                                echo "Pushing: $image"

                                docker push "$image"
                            done

                            echo "All Docker images pushed successfully."
                        '''
                    }
                }

                // Keep only the current image tag for each service
                // on the Jenkins/monitoring server.
                stage('Monitoring Server Image Retention') {
                    sh '''
                        set -eu

                        echo "Applying monitoring server image retention..."
                        echo "Policy: keep only the current build tag per service."

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
                            payment-service
                        do
                            repo="${DOCKER_ORG}/${PROJECT_NAME}-${service}"
                            current="${repo}:${IMAGE_TAG}"

                            if ! docker image inspect "$current" >/dev/null 2>&1; then
                                echo "ERROR: Current image not found: $current"
                                exit 1
                            fi

                            image_refs=$(docker image ls \
                                --format '{{.Repository}}:{{.Tag}}' |
                                awk -v prefix="${repo}:" \
                                    'index($0, prefix) == 1')

                            for image in $image_refs; do
                                if [ "$image" = "$current" ]; then
                                    continue
                                fi

                                # Do not remove images used by containers.
                                if docker ps -a \
                                    --filter "ancestor=$image" \
                                    --format '{{.ID}}' | grep -q .; then
                                    echo "Keeping image referenced by a container: $image"
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

                            export SSHPASS="$SSH_PASSWORD"

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

cd "${APP_DIR}"

if [ ! -f .env ]; then
    echo "ERROR: Server .env file not found."
    exit 1
fi

if [ ! -f docker-compose.yml ] && [ ! -f compose.yml ]; then
    echo "ERROR: Docker Compose file not found."
    exit 1
fi

# Change only IMAGE_TAG; preserve the other server settings.
if grep -q '^IMAGE_TAG=' .env; then
    sed -i "s/^IMAGE_TAG=.*/IMAGE_TAG=${IMAGE_TAG}/" .env
else
    printf '\\nIMAGE_TAG=%s\\n' "${IMAGE_TAG}" >> .env
fi

echo "Validating Compose configuration..."
docker compose --env-file .env config --quiet

echo "Pulling updated images..."
docker compose --env-file .env pull

echo "Starting updated services..."
docker compose --env-file .env up -d --remove-orphans

echo "Deployment command completed."
docker compose --env-file .env ps
REMOTE_SCRIPT

                            unset SSHPASS
                        '''
                    }
                }

                stage('Deployment Verification & App Image Retention') {
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

                            sshpass -e ssh \
                                -o StrictHostKeyChecking=no \
                                -o UserKnownHostsFile=/dev/null \
                                "$SSH_USER@$DEPLOY_SERVER" \
                                "IMAGE_TAG='$IMAGE_TAG' APP_DIR='$DEPLOY_PATH' bash -s" <<'REMOTE'
set -eu

cd "$APP_DIR"

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
"

echo "=========================================="
echo "DEPLOYMENT VERIFICATION"
echo "=========================================="

# Verify all 12 services before removing any old image tags.
for service in $SERVICES; do
    container_ids=$(docker compose --env-file .env ps --all -q "$service")

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

echo "All 12 services passed deployment verification."

# App server policy:
# Keep the deployed image plus the two newest other image tags
# for each service. Sort by Docker image creation timestamp.
echo "=========================================="
echo "APPLICATION SERVER IMAGE RETENTION"
echo "Policy: current + previous 2 images per service"
echo "=========================================="

for service in $SERVICES; do
    repo="sunardock/ayurvedaa-api-${service}"
    current="${repo}:${IMAGE_TAG}"

    if ! docker image inspect "$current" >/dev/null 2>&1; then
        echo "ERROR: Deployed image not found locally: $current"
        exit 1
    fi

    image_rows=$(docker image ls \
        --format '{{.CreatedAt}}|{{.Repository}}:{{.Tag}}' |
        awk -F '|' -v prefix="${repo}:" \
            'index($2, prefix) == 1 { print $0 }' |
        sort -r)

    previous_kept=0

    while IFS='|' read -r created image; do
        [ -n "$image" ] || continue

        # Always retain the currently deployed image.
        if [ "$image" = "$current" ]; then
            echo "KEEP current: $image"
            continue
        fi

        # Retain the two newest other tags.
        if [ "$previous_kept" -lt 2 ]; then
            previous_kept=$((previous_kept + 1))
            echo "KEEP previous ${previous_kept}: $image"
            continue
        fi

        # Never remove an image referenced by any container.
        if docker ps -a \
            --filter "ancestor=$image" \
            --format '{{.ID}}' | grep -q .; then
            echo "WARNING: Keeping image referenced by a container: $image"
            continue
        fi

        echo "Removing old image tag: $image"

        docker image rm "$image" ||
            echo "WARNING: Could not remove $image"

    done <<IMAGE_ROWS
$image_rows
IMAGE_ROWS

done

echo "Application server image retention completed."
REMOTE

                            unset SSHPASS
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
