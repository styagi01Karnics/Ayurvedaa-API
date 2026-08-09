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
                        echo 'Removing Old Ayurvedaa Containers'
                        echo '=========================================='


                        CONTAINERS='
                        ayurvedaa-api-patient-service
                        ayurvedaa-api-doctor-service
                        ayurvedaa-api-appointment-service
                        ayurvedaa-api-therapist-service
                        ayurvedaa-api-file-upload-service
                        ayurvedaa-api-attendance-service
                        ayurvedaa-api-activity-log-service
                        ayurvedaa-api-medicine-service
                        ayurvedaa-api-billing-service
                        ayurvedaa-api-notification-service
                        ayurvedaa-api-auth-service
                        '


                        for CONTAINER in \$CONTAINERS; do

                            if docker ps -a \
                                --format '{{.Names}}' \
                                | grep -qx "\$CONTAINER"; then

                                echo "Removing old container: \$CONTAINER"

                                docker rm -f "\$CONTAINER"

                            else

                                echo "Container not found: \$CONTAINER"

                            fi

                        done


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
        
                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} "
                        set +e
        
                        echo '=========================================='
                        echo 'Docker Image Cleanup'
                        echo '=========================================='
        
                        for SERVICE in \\
                            patient-service \\
                            doctor-service \\
                            appointment-service \\
                            therapist-service \\
                            file-upload-service \\
                            attendance-service \\
                            activity-log-service \\
                            medicine-service \\
                            billing-service \\
                            notification-service \\
                            auth-service
                        do
        
                            echo ''
                            echo 'Processing: ayurvedaa-api-${SERVICE}'
        
                            IMAGE_PREFIX='sunardock/ayurvedaa-api-${SERVICE}'
        
                            echo 'Images before cleanup:'
        
                            docker images \"\\${IMAGE_PREFIX}\" \\
                                --format '{{.Repository}}:{{.Tag}}' \\
                                | sort -V -r
        
                            echo 'Removing old images...'
        
                            docker images \"\\${IMAGE_PREFIX}\" \\
                                --format '{{.Repository}}:{{.Tag}}' \\
                                | grep -E ':[0-9]+$' \\
                                | sort -t: -k2,2nr \\
                                | tail -n +4 \\
                                | xargs -r docker rmi 2>/dev/null || true
        
                            echo 'Cleanup completed for: ayurvedaa-api-${SERVICE}'
        
                        done
        
                        echo ''
                        echo '=========================================='
                        echo 'Removing dangling images'
                        echo '=========================================='
        
                        docker image prune -f
        
                        echo ''
                        echo '=========================================='
                        echo 'Cleanup Completed'
                        echo '=========================================='
        
                        echo 'Remaining Ayurvedaa images:'
        
                        docker images 'sunardock/ayurvedaa-api-*' \\
                            --format '{{.Repository}}:{{.Tag}}' \\
                            | sort -V
        
                    "
        
                    echo "Docker cleanup completed."
                '''
            }
        }

    post {

        success {
            echo '''
==========================================
Ayurvedaa Deployment SUCCESSFUL
==========================================
The application was successfully deployed.
==========================================
'''
        }

        failure {
            echo '''
==========================================
Ayurvedaa Build FAILED
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
