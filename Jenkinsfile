pipeline {

    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven'
    }

    environment {

        // ==========================================
        // Docker
        // ==========================================

        IMAGE_PREFIX = 'sunardock/ayurvedaa-api'


        // ==========================================
        // Application Server
        // ==========================================

        APP_SERVER = 'root@45.195.229.15'
        APP_DIR = '/root/ayurvedaa'
        COMPOSE_FILE = 'docker-compose.yml'


        // ==========================================
        // Image Retention
        // ==========================================

        // Application server:
        // Keep latest 3 images per service
        KEEP_APP_IMAGES = '3'

        // Monitoring / Jenkins server:
        // Keep latest 1 image per service
        KEEP_JENKINS_IMAGES = '1'
    }


    stages {


        // ==========================================================
        // CHECKOUT
        // ==========================================================

        stage('Checkout') {

            steps {

                echo '=========================================='
                echo 'Checking out source code'
                echo '=========================================='

                checkout scm

                sh '''
                    set -e

                    echo "Git commit:"
                    git rev-parse --short HEAD

                    echo "Repository checkout completed."
                '''
            }
        }


        // ==========================================================
        // BUILD APPLICATION
        // ==========================================================

        stage('Build Application') {

            steps {

                echo '=========================================='
                echo 'Building Maven Application'
                echo '=========================================='

                sh '''
                    set -e

                    mvn clean package -DskipTests

                    echo "Maven build completed successfully."
                '''
            }
        }


        // ==========================================================
        // SONARQUBE
        // ==========================================================

        stage('SonarQube Analysis') {

            steps {

                echo '=========================================='
                echo 'Running SonarQube Analysis'
                echo '=========================================='

                withSonarQubeEnv('SonarQube') {

                    sh '''
                        set -e

                        mvn sonar:sonar \
                            -Dsonar.projectKey=Ayurvedaa-API \
                            -Dsonar.projectName=Ayurvedaa-API

                        echo "SonarQube analysis completed successfully."
                    '''
                }
            }
        }


        // ==========================================================
        // BUILD DOCKER IMAGES
        // ==========================================================

        stage('Build Docker Images') {

            steps {

                echo '=========================================='
                echo 'Building Docker Images'
                echo "Build Number: ${BUILD_NUMBER}"
                echo '=========================================='


                sh '''
                    set -e


                    docker build \
                        -t ${IMAGE_PREFIX}-patient-service:${BUILD_NUMBER} \
                        ./patient-service


                    docker build \
                        -t ${IMAGE_PREFIX}-doctor-service:${BUILD_NUMBER} \
                        ./doctor-service


                    docker build \
                        -t ${IMAGE_PREFIX}-appointment-service:${BUILD_NUMBER} \
                        ./appointment-service


                    docker build \
                        -t ${IMAGE_PREFIX}-therapist-service:${BUILD_NUMBER} \
                        ./therapist-service


                    docker build \
                        -t ${IMAGE_PREFIX}-file-upload-service:${BUILD_NUMBER} \
                        ./file-upload-service


                    docker build \
                        -t ${IMAGE_PREFIX}-attendance-service:${BUILD_NUMBER} \
                        ./attendance-service


                    docker build \
                        -t ${IMAGE_PREFIX}-activity-log-service:${BUILD_NUMBER} \
                        ./activity-log-service


                    docker build \
                        -t ${IMAGE_PREFIX}-medicine-service:${BUILD_NUMBER} \
                        ./medicine-service


                    docker build \
                        -t ${IMAGE_PREFIX}-billing-service:${BUILD_NUMBER} \
                        ./billing-service


                    docker build \
                        -t ${IMAGE_PREFIX}-notification-service:${BUILD_NUMBER} \
                        ./notification-service


                    docker build \
                        -t ${IMAGE_PREFIX}-auth-service:${BUILD_NUMBER} \
                        ./auth-service


                    echo ""
                    echo "=========================================="
                    echo "Docker images created"
                    echo "=========================================="


                    docker images "${IMAGE_PREFIX}-*" \
                        --format "table {{.Repository}}:{{.Tag}}\t{{.Size}}"
                '''
            }
        }


        // ==========================================================
        // DOCKER HUB PUSH
        // ==========================================================

        stage('Docker Push') {

            steps {

                echo '=========================================='
                echo 'Pushing Docker Images to Docker Hub'
                echo "Build Number: ${BUILD_NUMBER}"
                echo '=========================================='


                withDockerRegistry(
                    credentialsId: 'dockerhub-creds',
                    url: 'https://index.docker.io/v1/'
                ) {

                    sh '''
                        set -e


                        docker push ${IMAGE_PREFIX}-patient-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-doctor-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-appointment-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-therapist-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-file-upload-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-attendance-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-activity-log-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-medicine-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-billing-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-notification-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-auth-service:${BUILD_NUMBER}


                        echo ""
                        echo "Docker images pushed successfully to Docker Hub."
                    '''
                }
            }
        }


        // ==========================================================
        // DEPLOY APPLICATION
        // ==========================================================

        stage('Deploy') {

            steps {

                echo '=========================================='
                echo 'Deploying Ayurvedaa Application'
                echo "Build: ${BUILD_NUMBER}"
                echo "Application Server: ${APP_SERVER}"
                echo '=========================================='


                sh '''
                    set -e


                    // --------------------------------------------------
                    // Check SSH connection
                    // --------------------------------------------------

                    echo "Checking SSH connection..."


                    ssh -o StrictHostKeyChecking=no \
                        ${APP_SERVER} \
                        "echo 'Connected to application server'"


                    // --------------------------------------------------
                    // Create application directory
                    // --------------------------------------------------

                    echo "Creating application directory..."


                    ssh -o StrictHostKeyChecking=no \
                        ${APP_SERVER} \
                        "mkdir -p ${APP_DIR}"


                    // --------------------------------------------------
                    // Prepare Docker Compose file
                    // --------------------------------------------------

                    echo "Preparing Docker Compose file..."


                    rm -f docker-compose-clean.yml


                    sed \
                        -e '1{/^```/d;}' \
                        -e '${/^```$/d;}' \
                        ${COMPOSE_FILE} > docker-compose-clean.yml


                    // --------------------------------------------------
                    // Copy Docker Compose file
                    // --------------------------------------------------

                    echo "Copying Docker Compose file..."


                    scp -o StrictHostKeyChecking=no \
                        docker-compose-clean.yml \
                        ${APP_SERVER}:${APP_DIR}/${COMPOSE_FILE}


                    rm -f docker-compose-clean.yml


                    // --------------------------------------------------
                    // Validate application server
                    // --------------------------------------------------

                    echo "Validating application server configuration..."


                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} "

                        set -e

                        cd ${APP_DIR}


                        echo '=========================================='
                        echo 'Checking Docker Compose file'
                        echo '=========================================='


                        ls -lh ${COMPOSE_FILE}


                        echo '=========================================='
                        echo 'Checking .env file'
                        echo '=========================================='


                        if [ ! -f .env ]; then

                            echo 'ERROR: ${APP_DIR}/.env does not exist.'

                            exit 1

                        fi


                        chmod 600 .env


                        echo '.env file exists.'


                        stat -c '%a %n' .env


                        echo '=========================================='
                        echo 'Validating Docker Compose'
                        echo '=========================================='


                        IMAGE_TAG=${BUILD_NUMBER} \
                        docker compose --env-file .env config --quiet


                        echo 'Docker Compose validation successful.'
                    "


                    // --------------------------------------------------
                    // Deploy application
                    // --------------------------------------------------

                    echo "Starting deployment..."


                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} "

                        set -e


                        cd ${APP_DIR}


                        echo '=========================================='
                        echo 'Stopping Old Ayurvedaa Containers'
                        echo '=========================================='


                        IMAGE_TAG=${BUILD_NUMBER} \
                        docker compose --env-file .env down --remove-orphans


                        echo '=========================================='
                        echo 'Pulling Current Docker Images'
                        echo "Build: ${BUILD_NUMBER}"
                        echo '=========================================='


                        IMAGE_TAG=${BUILD_NUMBER} \
                        docker compose --env-file .env pull


                        echo '=========================================='
                        echo 'Starting New Services'
                        echo "Build: ${BUILD_NUMBER}"
                        echo '=========================================='


                        IMAGE_TAG=${BUILD_NUMBER} \
                        docker compose --env-file .env up -d --remove-orphans


                        echo '=========================================='
                        echo 'Deployment Completed'
                        echo '=========================================='


                        echo 'Running Containers:'


                        IMAGE_TAG=${BUILD_NUMBER} \
                        docker compose --env-file .env ps
                    "


                    echo "Ayurvedaa deployment completed successfully."
                '''
            }
        }


        // ==========================================================
        // APPLICATION SERVER CLEANUP
        // KEEP LATEST 3
        // ==========================================================

        stage('Cleanup Application Server Images') {

            steps {

                echo '=========================================='
                echo 'Application Server Docker Image Cleanup'
                echo 'Keeping Latest 3 Images Per Service'
                echo '=========================================='


                sh '''
                    set -e


                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} \
                        "APP_DIR='${APP_DIR}' KEEP_IMAGES='${KEEP_APP_IMAGES}' bash -s" <<'REMOTE_SCRIPT'


set -e


cd "${APP_DIR}"


echo "=========================================="
echo "Application Server Image Cleanup"
echo "=========================================="

echo "Server: $(hostname)"

echo "Keeping latest ${KEEP_IMAGES} images per service"


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
"


for SERVICE in ${SERVICES}
do

    IMAGE="sunardock/ayurvedaa-api-${SERVICE}"


    echo ""
    echo "=========================================="
    echo "Processing: ${IMAGE}"
    echo "=========================================="


    echo ""
    echo "Images currently present:"


    docker images "${IMAGE}" \
        --format '{{.Repository}}:{{.Tag}}' \
        | grep -E ':[0-9]+$' \
        | sort -t: -k2,2nr || true


    echo ""
    echo "Images that will be kept:"


    docker images "${IMAGE}" \
        --format '{{.Tag}}' \
        | grep -E '^[0-9]+$' \
        | sort -nr \
        | head -n "${KEEP_IMAGES}" \
        | while read TAG
    do

        if [ -n "${TAG}" ]; then

            echo "  KEEP: ${IMAGE}:${TAG}"

        fi

    done


    echo ""
    echo "Images that will be deleted:"


    docker images "${IMAGE}" \
        --format '{{.Tag}}' \
        | grep -E '^[0-9]+$' \
        | sort -nr \
        | tail -n +$((KEEP_IMAGES + 1)) \
        | while read TAG
    do

        if [ -n "${TAG}" ]; then

            echo "  DELETE: ${IMAGE}:${TAG}"


            docker rmi "${IMAGE}:${TAG}" || \
                echo "  WARNING: Could not remove ${IMAGE}:${TAG}"

        fi

    done

done


echo ""
echo "=========================================="
echo "Removing Dangling Images"
echo "=========================================="


docker image prune -f


echo ""
echo "=========================================="
echo "Final Application Server Images"
echo "=========================================="


docker images \
    --format 'table {{.Repository}}:{{.Tag}}\t{{.Size}}\t{{.CreatedSince}}' \
    | grep 'sunardock/ayurvedaa-api-' || true


echo ""
echo "=========================================="
echo "Application Server Cleanup Completed"
echo "=========================================="


REMOTE_SCRIPT
                '''
            }
        }


        // ==========================================================
        // JENKINS / MONITORING SERVER CLEANUP
        // KEEP LATEST 1
        // ==========================================================

        stage('Cleanup Jenkins Docker Images') {

            steps {

                echo '=========================================='
                echo 'Jenkins / Monitoring Server Docker Cleanup'
                echo 'Keeping Latest 1 Image Per Service'
                echo '=========================================='


                sh '''

                    set +e


                    echo "=========================================="
                    echo "Jenkins Docker Image Cleanup"
                    echo "=========================================="


                    echo "Server: $(hostname)"

                    echo "Current Build: ${BUILD_NUMBER}"

                    echo "Keeping latest ${KEEP_JENKINS_IMAGES} image per service"


                    for SERVICE in \
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
                        auth-service
                    do

                        IMAGE="${IMAGE_PREFIX}-${SERVICE}"


                        echo ""
                        echo "=========================================="
                        echo "Processing: ${IMAGE}"
                        echo "=========================================="


                        echo ""
                        echo "Images currently present:"


                        docker images "${IMAGE}" \
                            --format '{{.Repository}}:{{.Tag}}' \
                            | grep -E ':[0-9]+$' \
                            | sort -t: -k2,2nr || true


                        echo ""
                        echo "Latest image that will be kept:"


                        docker images "${IMAGE}" \
                            --format '{{.Tag}}' \
                            | grep -E '^[0-9]+$' \
                            | sort -nr \
                            | head -n "${KEEP_JENKINS_IMAGES}" \
                            | while read TAG
                        do

                            if [ -n "${TAG}" ]; then

                                echo "  KEEP: ${IMAGE}:${TAG}"

                            fi

                        done


                        echo ""
                        echo "Older images that will be deleted:"


                        docker images "${IMAGE}" \
                            --format '{{.Tag}}' \
                            | grep -E '^[0-9]+$' \
                            | sort -nr \
                            | tail -n +$((KEEP_JENKINS_IMAGES + 1)) \
                            | while read TAG
                        do

                            if [ -n "${TAG}" ]; then

                                echo "  DELETE: ${IMAGE}:${TAG}"


                                docker rmi "${IMAGE}:${TAG}" || \
                                    echo "  WARNING: Could not remove ${IMAGE}:${TAG}"

                            fi

                        done

                    done


                    echo ""
                    echo "=========================================="
                    echo "Removing Dangling Images"
                    echo "=========================================="


                    docker image prune -f


                    echo ""
                    echo "=========================================="
                    echo "Final Jenkins Images"
                    echo "=========================================="


                    docker images \
                        --format 'table {{.Repository}}:{{.Tag}}\t{{.Size}}\t{{.CreatedSince}}' \
                        | grep 'sunardock/ayurvedaa-api-' || true


                    echo ""
                    echo "Jenkins / Monitoring Docker cleanup completed."

                '''
            }
        }

    }


    // ==========================================================
    // POST ACTIONS
    // ==========================================================

    post {

        success {

            echo '''
==========================================
AYURVEDAA DEPLOYMENT SUCCESSFUL
==========================================

Docker Hub:
  All pushed images retained

Application Server:
  Latest 3 images per service retained

Monitoring / Jenkins Server:
  Latest 1 image per service retained

Deployment completed successfully.
==========================================
'''
        }


        failure {

            echo '''
==========================================
AYURVEDAA DEPLOYMENT FAILED
==========================================

Please check the Jenkins console log.

Image cleanup is performed only after
the deployment stage succeeds.

==========================================
'''
        }


        always {

            echo "Build ${BUILD_NUMBER} completed."

        }

    }
}
