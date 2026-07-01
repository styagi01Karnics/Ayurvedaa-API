pipeline {

    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
    }

    environment {

        IMAGE_NAME = "yourdockerhubusername/ayurvedaa"

        CONTAINER_NAME = "ayurvedaa"

    }

    stages {

        stage('Checkout') {

            steps {

                git branch: 'development',
                    url: 'https://github.com/yourusername/Ayurvedaa-API.git',
                    credentialsId: 'github-token'

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

                sh 'mvn test'

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
                    -Dsonar.projectKey=Ayurvedaa \
                    -Dsonar.projectName=Ayurvedaa

                    '''

                }

            }

        }

        stage('Quality Gate') {

            steps {

                timeout(time: 10, unit: 'MINUTES') {

                    waitForQualityGate abortPipeline: true

                }

            }

        }

        stage('Docker Build') {

            steps {

                sh '''

                docker build \
                -t ${IMAGE_NAME}:${BUILD_NUMBER} .

                '''

            }

        }

        stage('Docker Push') {

            steps {

                withDockerRegistry(credentialsId: 'dockerhub') {

                    sh '''

                    docker push ${IMAGE_NAME}:${BUILD_NUMBER}

                    '''

                }

            }

        }

        stage('Deploy') {

            steps {

                sh '''

                docker stop ${CONTAINER_NAME} || true

                docker rm ${CONTAINER_NAME} || true

                docker run -d \
                --name ${CONTAINER_NAME} \
                -p 8100:8080 \
                ${IMAGE_NAME}:${BUILD_NUMBER}

                '''

            }

        }

    }

}
