pipeline {

    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)

        buildDiscarder(
            logRotator(
                numToKeepStr: '20',
                daysToKeepStr: '30'
            )
        )
    }

    environment {

        IMAGE_PREFIX = 'sunardock/ayurvedaa-api'

        DOCKER_CREDENTIALS = 'dockerhub-creds'

        APP_SERVER = '45.195.229.15'
        APP_DIR = '/root/ayurvedaa'

        SSH_CREDENTIALS = 'new-server-ssh'
    }

    stages {

        stage('Initialize') {
            steps {
                script {

                    env.SAFE_BRANCH = (env.BRANCH_NAME ?: 'unknown')
                        .replaceAll(/[^A-Za-z0-9_.-]/, '-')

                    env.IMAGE_TAG = "${env.SAFE_BRANCH}-${env.BUILD_NUMBER}"

                    echo "=============================================="
                    echo "AYURVEDAA MULTIBRANCH PIPELINE"
                    echo "=============================================="
                    echo "Branch       : ${env.BRANCH_NAME}"
                    echo "Safe Branch  : ${env.SAFE_BRANCH}"
                    echo "Build Number : ${env.BUILD_NUMBER}"
                    echo "Image Tag    : ${env.IMAGE_TAG}"
                    echo "=============================================="
                }
            }
        }


        stage('Checkout') {
            steps {

                checkout scm

                sh '''
                    set -e

                    echo "Repository:"
                    git remote get-url origin

                    echo ""
                    echo "Commit:"
                    git rev-parse --short HEAD

                    echo ""
                    echo "Branch:"
                    git branch --show-current || true
                '''
            }
        }


        stage('Environment Check') {
            steps {
                sh '''
                    set -e

                    echo "Checking required commands..."

                    command -v java
                    command -v mvn
                    command -v docker
                    command -v git

                    echo ""
                    echo "Environment check completed."
                '''
            }
        }


        stage('Maven Test & Package') {
            steps {
                sh '''
                    set -e

                    mvn clean test package -DskipTests=false
                '''
            }
        }


        stage('SonarQube Analysis') {
            steps {

                withSonarQubeEnv('SonarQube') {

                    sh '''
                        set -e

                        mvn sonar:sonar
                    '''
                }
            }
        }


        stage('Verify Dockerfiles') {
            steps {

                sh '''
                    set -e

                    test -f patient-service/Dockerfile
                    test -f doctor-service/Dockerfile
                    test -f appointment-service/Dockerfile
                    test -f therapist-service/Dockerfile
                    test -f file-upload-service/Dockerfile
                    test -f attendance-service/Dockerfile
                    test -f activity-log-service/Dockerfile
                    test -f medicine-service/Dockerfile
                    test -f billing-service/Dockerfile
                    test -f notification-service/Dockerfile
                    test -f auth-service/Dockerfile
                    test -f payment-service/Dockerfile

                    echo "All Dockerfiles found."
                '''
            }
        }


        stage('Docker Build') {
            steps {

                sh '''
                    set -e

                    echo "Building Ayurvedaa images..."
                    echo "IMAGE_TAG=${IMAGE_TAG}"

                    docker build \
                        -t ${IMAGE_PREFIX}-patient-service:${IMAGE_TAG} \
                        ./patient-service

                    docker build \
                        -t ${IMAGE_PREFIX}-doctor-service:${IMAGE_TAG} \
                        ./doctor-service

                    docker build \
                        -t ${IMAGE_PREFIX}-appointment-service:${IMAGE_TAG} \
                        ./appointment-service

                    docker build \
                        -t ${IMAGE_PREFIX}-therapist-service:${IMAGE_TAG} \
                        ./therapist-service

                    docker build \
                        -t ${IMAGE_PREFIX}-file-upload-service:${IMAGE_TAG} \
                        ./file-upload-service

                    docker build \
                        -t ${IMAGE_PREFIX}-attendance-service:${IMAGE_TAG} \
                        ./attendance-service

                    docker build \
                        -t ${IMAGE_PREFIX}-activity-log-service:${IMAGE_TAG} \
                        ./activity-log-service

                    docker build \
                        -t ${IMAGE_PREFIX}-medicine-service:${IMAGE_TAG} \
                        ./medicine-service

                    docker build \
                        -t ${IMAGE_PREFIX}-billing-service:${IMAGE_TAG} \
                        ./billing-service

                    docker build \
                        -t ${IMAGE_PREFIX}-notification-service:${IMAGE_TAG} \
                        ./notification-service

                    docker build \
                        -t ${IMAGE_PREFIX}-auth-service:${IMAGE_TAG} \
                        ./auth-service

                    docker build \
                        -t ${IMAGE_PREFIX}-payment-service:${IMAGE_TAG} \
                        ./payment-service

                    echo "Docker build completed."
                '''
            }
        }


        stage('Docker Push') {
            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: "${DOCKER_CREDENTIALS}",
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {

                    sh '''
                        set -e

                        echo "${DOCKER_PASSWORD}" | docker login \
                            --username "${DOCKER_USERNAME}" \
                            --password-stdin

                        docker push ${IMAGE_PREFIX}-patient-service:${IMAGE_TAG}
                        docker push ${IMAGE_PREFIX}-doctor-service:${IMAGE_TAG}
                        docker push ${IMAGE_PREFIX}-appointment-service:${IMAGE_TAG}
                        docker push ${IMAGE_PREFIX}-therapist-service:${IMAGE_TAG}
                        docker push ${IMAGE_PREFIX}-file-upload-service:${IMAGE_TAG}
                        docker push ${IMAGE_PREFIX}-attendance-service:${IMAGE_TAG}
                        docker push ${IMAGE_PREFIX}-activity-log-service:${IMAGE_TAG}
                        docker push ${IMAGE_PREFIX}-medicine-service:${IMAGE_TAG}
                        docker push ${IMAGE_PREFIX}-billing-service:${IMAGE_TAG}
                        docker push ${IMAGE_PREFIX}-notification-service:${IMAGE_TAG}
                        docker push ${IMAGE_PREFIX}-auth-service:${IMAGE_TAG}
                        docker push ${IMAGE_PREFIX}-payment-service:${IMAGE_TAG}

                        docker logout

                        echo "Docker push completed."
                    '''
                }
            }
        }


        stage('Deploy') {

            when {
                anyOf {
                    branch 'fixes-development'
                    branch 'dev-sonarqube-common'
                }
            }

            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: "${SSH_CREDENTIALS}",
                        usernameVariable: 'SSH_USER',
                        passwordVariable: 'SSH_PASSWORD'
                    )
                ]) {

                    sh '''
                        set -e

                        echo "=============================================="
                        echo "AYURVEDAA DEPLOYMENT"
                        echo "=============================================="
                        echo "Branch    : ${BRANCH_NAME}"
                        echo "Image Tag : ${IMAGE_TAG}"
                        echo "Server    : ${APP_SERVER}"
                        echo "=============================================="

                        export SSHPASS="${SSH_PASSWORD}"

                        sshpass -e ssh \
                            -o StrictHostKeyChecking=no \
                            -o UserKnownHostsFile=/dev/null \
                            "${SSH_USER}@${APP_SERVER}" \
                            "IMAGE_TAG='${IMAGE_TAG}' APP_DIR='${APP_DIR}' bash -s" <<'REMOTE_SCRIPT'

set -e

exec 9>/var/lock/ayurvedaa-api-deployment.lock
flock 9

echo "Deployment lock acquired."

cd "${APP_DIR}"

echo ""
echo "Current Ayurvedaa containers:"
docker ps \
    --filter "name=ayurvedaa" \
    --format "table {{.Names}}\t{{.Image}}\t{{.Status}}"

echo ""
echo "Stopping and removing current Ayurvedaa deployment..."

docker compose down --remove-orphans

echo ""
echo "Pulling new images..."
IMAGE_TAG="${IMAGE_TAG}" docker compose pull

echo ""
echo "Starting new Ayurvedaa deployment..."
IMAGE_TAG="${IMAGE_TAG}" docker compose up -d --remove-orphans

echo ""
echo "New Ayurvedaa containers:"
docker ps \
    --filter "name=ayurvedaa" \
    --format "table {{.Names}}\t{{.Image}}\t{{.Status}}"

echo ""
echo "Deployment completed successfully."

REMOTE_SCRIPT

                        unset SSHPASS
                    '''
                }
            }
        }


        stage('Deployment Verification') {

            when {
                anyOf {
                    branch 'fixes-development'
                    branch 'dev-sonarqube-common'
                }
            }

            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: "${SSH_CREDENTIALS}",
                        usernameVariable: 'SSH_USER',
                        passwordVariable: 'SSH_PASSWORD'
                    )
                ]) {

                    sh '''
                        set -e

                        export SSHPASS="${SSH_PASSWORD}"

                        sshpass -e ssh \
                            -o StrictHostKeyChecking=no \
                            -o UserKnownHostsFile=/dev/null \
                            "${SSH_USER}@${APP_SERVER}" \
                            "cd ${APP_DIR} && docker compose ps"

                        unset SSHPASS
                    '''
                }
            }
        }


        stage('Cleanup Old Ayurvedaa Images') {

            when {
                anyOf {
                    branch 'fixes-development'
                    branch 'dev-sonarqube-common'
                }
            }

            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: "${SSH_CREDENTIALS}",
                        usernameVariable: 'SSH_USER',
                        passwordVariable: 'SSH_PASSWORD'
                    )
                ]) {

                    sh '''
                        set -e

                        export SSHPASS="${SSH_PASSWORD}"

                        sshpass -e ssh \
                            -o StrictHostKeyChecking=no \
                            -o UserKnownHostsFile=/dev/null \
                            "${SSH_USER}@${APP_SERVER}" \
                            'bash -s' <<'REMOTE_CLEANUP'

set -e

echo "=============================================="
echo "AYURVEDAA IMAGE CLEANUP"
echo "=============================================="

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

for SERVICE in ${SERVICES}
do

    REPOSITORY="sunardock/ayurvedaa-api-${SERVICE}"

    echo ""
    echo "----------------------------------------------"
    echo "Service: ${SERVICE}"
    echo "Repository: ${REPOSITORY}"
    echo "----------------------------------------------"

    docker image ls "${REPOSITORY}" \
        --format '{{.CreatedAt}}|{{.Repository}}:{{.Tag}}' \
        | sort -r \
        > "/tmp/${SERVICE}-ayurvedaa-images.txt"

    COUNT=0

    while IFS='|' read -r CREATED IMAGE
    do

        if [ -z "${IMAGE}" ]
        then
            continue
        fi

        COUNT=$((COUNT + 1))

        if [ "${COUNT}" -le 3 ]
        then

            echo "KEEP   : ${IMAGE}"

        else

            RUNNING=$(docker ps \
                --filter "ancestor=${IMAGE}" \
                --format '{{.Names}}' \
                | head -n 1)

            if [ -n "${RUNNING}" ]
            then

                echo "SKIP   : ${IMAGE}"
                echo "         Running container: ${RUNNING}"

            else

                echo "REMOVE : ${IMAGE}"

                docker image rm "${IMAGE}" || true

            fi
        fi

    done < "/tmp/${SERVICE}-ayurvedaa-images.txt"

    rm -f "/tmp/${SERVICE}-ayurvedaa-images.txt"

done

echo ""
echo "=============================================="
echo "CLEANUP COMPLETED"
echo "=============================================="

echo ""
echo "Remaining Ayurvedaa images:"

docker images \
    --format '{{.Repository}}:{{.Tag}}\t{{.CreatedAt}}' \
    | grep '^sunardock/ayurvedaa-api-' \
    | sort

echo ""
echo "No other application images were touched."

REMOTE_CLEANUP

                        unset SSHPASS
                    '''
                }
            }
        }
    }


    post {

        success {
            echo ""
            echo "=============================================="
            echo "AYURVEDAA PIPELINE SUCCESS"
            echo "=============================================="
            echo "Branch    : ${env.BRANCH_NAME}"
            echo "Build     : ${env.BUILD_NUMBER}"
            echo "Image Tag : ${env.IMAGE_TAG}"
            echo "=============================================="
        }

        failure {
            echo ""
            echo "=============================================="
            echo "AYURVEDAA PIPELINE FAILED"
            echo "=============================================="
            echo "Branch : ${env.BRANCH_NAME}"
            echo "Build  : ${env.BUILD_NUMBER}"
            echo "=============================================="
        }

        always {
            echo "Pipeline completed for branch: ${env.BRANCH_NAME}"
        }
    }
}
