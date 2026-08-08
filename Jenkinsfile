pipeline {

    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven'
    }

    environment {

        IMAGE_PREFIX = "sunardock/ayurvedaa-api"

        // New application server
        APP_SERVER = "root@45.195.229.15"

        // Application directory on new server
        APP_DIR = "/root/ayurvedaa"
    }

    stages {

        stage('Checkout') {

            steps {

                git branch: 'fixes-development',
                    credentialsId: 'github-creds-funride',
                    url: 'https://github.com/styagi01Karnics/Ayurvedaa-API.git'
            }
        }


        stage('Clean Workspace') {

            steps {

                sh 'mvn clean'
            }
        }


        stage('Compile') {

            steps {

                sh 'mvn compile'
            }
        }


        stage('Unit Test') {

            steps {

                sh 'mvn test jacoco:report'
            }
        }


        stage('Package') {

            steps {

                sh 'mvn package -DskipTests'
            }
        }


        stage('SonarQube Analysis') {

            steps {

                withSonarQubeEnv('SonarQube') {

                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=Ayurvedaa-API \
                        -Dsonar.projectName=Ayurvedaa-API
                    '''
                }
            }
        }


        stage('Quality Gate') {

            steps {

                timeout(time: 10, unit: 'MINUTES') {

                    waitForQualityGate abortPipeline: false
                }
            }
        }


        stage('Docker Build') {

            steps {

                sh '''
                    set -e

                    echo "=========================================="
                    echo "Building Docker Images"
                    echo "Build Number: ${BUILD_NUMBER}"
                    echo "=========================================="

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
                        -t ${IMAGE_PREFIX}-auth-service:${BUILD_NUMBER} \
                        ./auth-service

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

                    echo "Docker image build completed successfully."
                '''
            }
        }


        stage('Docker Push') {

            steps {

                withDockerRegistry([
                    credentialsId: 'dockerhub-creds',
                    url: ''
                ]) {

                    sh '''
                        set -e

                        echo "=========================================="
                        echo "Pushing Docker Images"
                        echo "Build Number: ${BUILD_NUMBER}"
                        echo "=========================================="

                        docker push ${IMAGE_PREFIX}-patient-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-doctor-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-appointment-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-therapist-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-file-upload-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-attendance-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-auth-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-activity-log-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-medicine-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-billing-service:${BUILD_NUMBER}

                        docker push ${IMAGE_PREFIX}-notification-service:${BUILD_NUMBER}

                        echo "Docker images pushed successfully."
                    '''
                }
            }
        }


        stage('Deploy') {
        
            steps {
        
                sh '''
                    set -e
        
                    echo "=========================================="
                    echo "Deploying Ayurvedaa Application"
                    echo "Build: ${BUILD_NUMBER}"
                    echo "Server: ${APP_SERVER}"
                    echo "=========================================="
        
                    echo "Checking SSH connection..."
        
                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} "
                        echo 'Connected to application server'
                    "
        
        
                    echo "Creating application directory..."
        
                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} "
                        mkdir -p ${APP_DIR}
                    "
        
        
                    echo "Copying docker-compose.yml to application server..."
        
                    scp -o StrictHostKeyChecking=no \
                        docker-compose.yml \
                        ${APP_SERVER}:${APP_DIR}/docker-compose.yml
        
        
                    echo "Checking docker-compose.yml..."
        
                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} "
                        cd ${APP_DIR}
        
                        echo 'Compose file:'
                        ls -lh docker-compose.yml
        
                        echo 'Validating Compose configuration:'
        
                        docker compose config
                    "
        
        
                    echo "Checking application network..."
        
                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} "
                        docker network inspect ayurvedaa-app-net > /dev/null
                    "
        
        
                    echo "Pulling Docker images..."
        
                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} "
                        cd ${APP_DIR}
        
                        IMAGE_TAG=${BUILD_NUMBER} docker compose pull
                    "
        
        
                    echo "Starting application services..."
        
                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} "
                        cd ${APP_DIR}
        
                        IMAGE_TAG=${BUILD_NUMBER} \
                        docker compose up -d --remove-orphans
                    "
        
        
                    echo "Waiting for services to start..."
        
                    sleep 15
        
        
                    echo "Checking application containers..."
        
                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} "
                        cd ${APP_DIR}
        
                        docker compose ps
                    "
        
        
                    echo "=========================================="
                    echo "Deployment completed successfully"
                    echo "Build: ${BUILD_NUMBER}"
                    echo "=========================================="
                '''
            }
        }


        stage('Verify Deployment') {

            steps {

                sh '''
                    set -e

                    echo "=========================================="
                    echo "Verifying Deployment"
                    echo "=========================================="

                    ssh -o StrictHostKeyChecking=no ${APP_SERVER} "

                        cd ${APP_DIR}

                        echo 'Docker Compose Services:'

                        docker compose ps

                        echo ''

                        echo 'Ayurvedaa Containers:'

                        docker ps \
                            --filter 'name=ayurvedaa-api-' \
                            --format 'table {{.Names}}\\t{{.Status}}\\t{{.Ports}}'

                    "
                '''
            }
        }


        stage('Cleanup Old Docker Images') {

            steps {

                sh '''
                    echo "=========================================="
                    echo "Cleaning Old Docker Images"
                    echo "Keeping Latest 3 Versions"
                    echo "=========================================="

                    REPOS=$(docker images --format "{{.Repository}}" \
                        | grep "^sunardock/ayurvedaa-api-" \
                        | sort -u || true)

                    for REPO in $REPOS
                    do

                        echo "Processing $REPO"

                        docker images "$REPO" \
                            --format "{{.Tag}}" \
                            | grep -E '^[0-9]+$' \
                            | sort -rn \
                            | tail -n +4 \
                            | while read TAG
                            do

                                echo "Deleting $REPO:$TAG"

                                docker rmi -f "$REPO:$TAG" || true

                            done

                    done

                    docker image prune -f || true

                    echo "Docker cleanup completed."
                '''
            }
        }
    }


    post {

        success {

            echo '''
            ==========================================
            AYURVEDAA DEPLOYMENT SUCCESS
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
    }
}
