pipeline {

    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven'
    }

    environment {

        IMAGE_NAME = "sunardock/ayurvedaa"

        CONTAINER_NAME = "ayurvedaa"

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
        docker build -t sunardock/ayurvedaa-api-patient-service:${BUILD_NUMBER} ./patient-service
        docker build -t sunardock/ayurvedaa-api-doctor-service:${BUILD_NUMBER} ./doctor-service
        docker build -t sunardock/ayurvedaa-api-appointment-service:${BUILD_NUMBER} ./appointment-service
        docker build -t sunardock/ayurvedaa-api-therapist-service:${BUILD_NUMBER} ./therapist-service
        docker build -t sunardock/ayurvedaa-api-file-upload-service:${BUILD_NUMBER} ./file-upload-service
        docker build -t sunardock/ayurvedaa-api-attendance-service:${BUILD_NUMBER} ./attendance-service
        '''
    }
}


        stage('Docker Push') {
    steps {
        withDockerRegistry([credentialsId: 'dockerhub-creds', url: '']) {
            sh '''
            docker push sunardock/ayurvedaa-api-patient-service:${BUILD_NUMBER}
            docker push sunardock/ayurvedaa-api-doctor-service:${BUILD_NUMBER}
            docker push sunardock/ayurvedaa-api-appointment-service:${BUILD_NUMBER}
            docker push sunardock/ayurvedaa-api-therapist-service:${BUILD_NUMBER}
            docker push sunardock/ayurvedaa-api-file-upload-service:${BUILD_NUMBER}
            docker push sunardock/ayurvedaa-api-attendance-service:${BUILD_NUMBER}
            '''
        }
    }
}
      stage('Deploy') {
    steps {
        sh '''
        # Stop and remove old containers
        docker rm -f ayurvedaa-api-patient-service || true
        docker rm -f ayurvedaa-api-doctor-service || true
        docker rm -f ayurvedaa-api-appointment-service || true
        docker rm -f ayurvedaa-api-therapist-service || true
        docker rm -f ayurvedaa-api-file-upload-service || true
        docker rm -f ayurvedaa-api-attendance-service || true

        # Shared network so Feign can resolve sibling containers by name.
        # Public IP / localhost both fail inside Docker (hairpin / container-local loopback).
        docker network create ayurvedaa-api-net || true

        # Run patient-service
        docker run -d \
        --name ayurvedaa-api-patient-service \
        --network ayurvedaa-api-net \
        -p 8101:8101 \
        sunardock/ayurvedaa-api-patient-service:${BUILD_NUMBER}

        # Run doctor-service
        docker run -d \
        --name ayurvedaa-api-doctor-service \
        --network ayurvedaa-api-net \
        -p 8102:8102 \
        sunardock/ayurvedaa-api-doctor-service:${BUILD_NUMBER}

        # Run appointment-service
        docker run -d \
        --name ayurvedaa-api-appointment-service \
        --network ayurvedaa-api-net \
        -p 8103:8103 \
        -e SERVICES_PATIENT_URL=http://ayurvedaa-api-patient-service:8101 \
        -e SERVICES_DOCTOR_URL=http://ayurvedaa-api-doctor-service:8102 \
        -e SERVICES_THERAPIST_URL=http://ayurvedaa-api-therapist-service:8104 \
        -e SERVICES_FILE_UPLOAD_URL=http://ayurvedaa-api-file-upload-service:8105 \
        sunardock/ayurvedaa-api-appointment-service:${BUILD_NUMBER}

        # Run therapist-service
        docker run -d \
        --name ayurvedaa-api-therapist-service \
        --network ayurvedaa-api-net \
        -p 8104:8104 \
        -e SERVICES_APPOINTMENT_URL=http://ayurvedaa-api-appointment-service:8103 \
        sunardock/ayurvedaa-api-therapist-service:${BUILD_NUMBER}

        # Run file-upload-service
        docker run -d \
        --name ayurvedaa-api-file-upload-service \
        --network ayurvedaa-api-net \
        -p 8105:8105 \
        sunardock/ayurvedaa-api-file-upload-service:${BUILD_NUMBER}

        # Run attendance-service
        docker run -d \
        --name ayurvedaa-api-attendance-service \
        --network ayurvedaa-api-net \
        -p 8106:8106 \
        sunardock/ayurvedaa-api-attendance-service:${BUILD_NUMBER}
        '''
    }
}
    }

}
