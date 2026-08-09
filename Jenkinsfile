pipeline {

    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven'
    }

    environment {
        IMAGE_PREFIX = 'sunardock/ayurvedaa-api'
        APP_SERVER = 'root@45.195.229.15'
        APP_DIR = '/root/ayurvedaa'
        COMPOSE_FILE = 'docker-compose.yml'
    }

    stages {

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

                    echo "Docker image build completed successfully."
                '''
            }
        }


        stage('Docker Push') {
            steps {
                echo '=========================================='
                echo 'Pushing Docker Images'
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

                        echo "Docker images pushed successfully."
                    '''
                }
            }
        }


        stage('Deploy') {
            steps {

                echo '=========================================='
                echo 'Deploying Ayurvedaa Application'
                echo "Build: ${BUILD_NUMBER}"
                echo "Application Server: ${APP_SERVER}"
                echo '=========================================='

                sh '''
                    set -e

                    echo "Checking SSH connection..."

                    ssh -o StrictHostKeyChecking=no \
                        ${APP_SERVER} \
                        "echo 'Connected to application server'"


                    echo "Creating application directory..."

                    ssh -o StrictHostKeyChecking=no \
                        ${APP_SERVER} \
                        "mkdir -p ${APP_DIR}"


                    echo "Preparing Docker Compose file..."

                    rm -f docker-compose-clean.yml

                    sed \
                        -e '1{/^```/d;}' \
                        -e '${/^```$/d;}' \
                        ${COMPOSE_FILE} > docker-compose-clean.yml


                    echo "Copying Docker Compose file..."

                    scp -o StrictHostKeyChecking=no \
                        docker-compose-clean.yml \
                        ${APP_SERVER}:${APP_DIR}/${COMPOSE_FILE}


                    rm -f docker-compose-clean.yml


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
                        echo 'Pulling Docker Images'
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


        stage('Cleanup Old Docker Images') {
            steps {

                echo '=========================================='
                echo 'Cleaning Old Docker Images'
                echo 'Keeping Latest 3 Images Per Service'
                echo '=========================================='

                sh '''
                    set +e

                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} '
                        set +e

                        echo "Starting Docker image cleanup..."

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
                            IMAGE="sunardock/ayurvedaa-api-${SERVICE}"

                            echo ""
                            echo "Processing ${IMAGE}..."

                            docker images "${IMAGE}" \
                                --format "{{.Tag}}" \
                                | grep -E "^[0-9]+$" \
                                | sort -nr \
                                | tail -n +4 \
                                | while read TAG
                            do
                                if [ -n "$TAG" ]; then
                                    echo "Removing ${IMAGE}:${TAG}"
                                    docker rmi "${IMAGE}:${TAG}" || true
                                fi
                            done

                            echo "Completed ${IMAGE}"
                        done


                        echo ""
                        echo "Removing dangling images..."

                        docker image prune -f


                        echo ""
                        echo "Docker cleanup completed."
                    '

                    echo "Docker cleanup completed successfully."
                '''
            }
        }

    }


    post {

        success {
            echo '''
==========================================
Ayurvedaa Deployment SUCCESSFUL
==========================================
'''
        }

        failure {
            echo '''
==========================================
Ayurvedaa Deployment FAILED
==========================================
Please check the Jenkins console log.
==========================================
'''
        }

        always {
            echo "Build ${BUILD_NUMBER} completed."
        }
    }
}
