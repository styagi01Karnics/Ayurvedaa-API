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

                    docker build -t ${IMAGE_PREFIX}-patient-service:${BUILD_NUMBER} ./patient-service
                    docker build -t ${IMAGE_PREFIX}-doctor-service:${BUILD_NUMBER} ./doctor-service
                    docker build -t ${IMAGE_PREFIX}-appointment-service:${BUILD_NUMBER} ./appointment-service
                    docker build -t ${IMAGE_PREFIX}-therapist-service:${BUILD_NUMBER} ./therapist-service
                    docker build -t ${IMAGE_PREFIX}-file-upload-service:${BUILD_NUMBER} ./file-upload-service
                    docker build -t ${IMAGE_PREFIX}-attendance-service:${BUILD_NUMBER} ./attendance-service
                    docker build -t ${IMAGE_PREFIX}-activity-log-service:${BUILD_NUMBER} ./activity-log-service
                    docker build -t ${IMAGE_PREFIX}-medicine-service:${BUILD_NUMBER} ./medicine-service
                    docker build -t ${IMAGE_PREFIX}-billing-service:${BUILD_NUMBER} ./billing-service
                    docker build -t ${IMAGE_PREFIX}-notification-service:${BUILD_NUMBER} ./notification-service
                    docker build -t ${IMAGE_PREFIX}-auth-service:${BUILD_NUMBER} ./auth-service

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
                    credentialsId: 'dockerhub-credentials',
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
                echo "Server: ${APP_SERVER}"
                echo '=========================================='

                sh '''
                    set -e

                    echo "Checking SSH connection..."

                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} \
                        "echo 'Connected to application server'"

                    echo "Creating application directory..."

                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} \
                        "mkdir -p ${APP_DIR}"

                    echo "Preparing Docker Compose file..."

                    # Remove accidental Markdown code fences if they exist
                    # in docker-compose.yml from the repository.
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

                    echo "Checking Docker Compose file..."

                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} '
                        set -e

                        cd /root/ayurvedaa

                        echo "Compose file:"
                        ls -lh docker-compose.yml

                        echo "Checking environment file..."

                        if [ ! -f .env ]; then
                            echo "ERROR: /root/ayurvedaa/.env does not exist."
                            exit 1
                        fi

                        chmod 600 .env

                        echo ".env file exists."
                        echo "Environment file permissions:"
                        stat -c "%a %n" .env

                        echo "Validating Docker Compose configuration..."

                        # IMPORTANT:
                        # --quiet prevents environment values such as
                        # DB passwords from being printed.
                        docker compose --env-file .env config --quiet

                        echo "Docker Compose validation successful."
                    '

                    echo "Starting deployment..."

                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} '
                        set -e

                        cd /root/ayurvedaa

                        echo "Pulling latest images..."

                        IMAGE_TAG=${BUILD_NUMBER} \
                        docker compose --env-file .env pull

                        echo "Starting services..."

                        IMAGE_TAG=${BUILD_NUMBER} \
                        docker compose --env-file .env up -d

                        echo "Deployment completed."

                        echo "Running containers:"

                        docker compose ps
                    '

                    echo "Ayurvedaa deployment completed successfully."
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                echo '=========================================='
                echo 'Verifying Deployment'
                echo '=========================================='

                sh '''
                    set -e

                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} '
                        set -e

                        cd /root/ayurvedaa

                        echo "Container status:"

                        docker compose ps

                        echo ""
                        echo "Checking running containers..."

                        RUNNING=$(docker compose ps --status running --services | wc -l)

                        if [ "$RUNNING" -lt 11 ]; then
                            echo "ERROR: Expected 11 Ayurvedaa services, but only $RUNNING are running."
                            exit 1
                        fi

                        echo "All expected Ayurvedaa services are running."
                    '
                '''
            }
        }

        stage('Cleanup Old Docker Images') {
            steps {
                echo '=========================================='
                echo 'Cleaning Old Docker Images'
                echo '=========================================='

                sh '''
                    set +e

                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} '
                        docker image prune -f
                    '

                    echo "Docker cleanup completed."
                '''
            }
        }
    }

    post {

        success {
            echo '''
==========================================
AYURVEDAA DEPLOYMENT SUCCESSFUL
==========================================
'''
        }

        failure {
            echo '''
==========================================
AYURVEDAA DEPLOYMENT FAILED
==========================================
'''
        }

        always {
            echo "Build ${BUILD_NUMBER} completed."
        }
    }
}
