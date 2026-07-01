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

                    waitForQualityGate abortPipeline: true

                }

            }

        }

      stage('Docker Build') {
    steps {
        sh '''
        docker build -t sunardock/patient-service:${BUILD_NUMBER} ./patient-service
        docker build -t sunardock/doctor-service:${BUILD_NUMBER} ./doctor-service
        docker build -t sunardock/appointment-service:${BUILD_NUMBER} ./appointment-service
        docker build -t sunardock/therapist-service:${BUILD_NUMBER} ./therapist-service
        docker build -t sunardock/file-upload-service:${BUILD_NUMBER} ./file-upload-service
        '''
    }
}

       stage('Docker Push') {
    steps {
        withDockerRegistry(
            credentialsId: 'dockerhub-creds',
            url: 'https://index.docker.io/v1/'
        ) {
            sh '''
            docker push sunardock/patient-service:${BUILD_NUMBER}
            docker push sunardock/doctor-service:${BUILD_NUMBER}
            docker push sunardock/appointment-service:${BUILD_NUMBER}
            docker push sunardock/therapist-service:${BUILD_NUMBER}
            docker push sunardock/file-upload-service:${BUILD_NUMBER}
            '''
        }
    }
}

        stage('Deploy') {
    steps {
        sh '''
        # Stop and remove old containers
        docker rm -f patient-service || true
        docker rm -f doctor-service || true
        docker rm -f appointment-service || true
        docker rm -f therapist-service || true
        docker rm -f file-upload-service || true

        # Run patient-service
        docker run -d \
        --name patient-service \
        -p 8101:8101 \
        sunardock/patient-service:${BUILD_NUMBER}

        # Run doctor-service
        docker run -d \
        --name doctor-service \
        -p 8102:8102 \
        sunardock/doctor-service:${BUILD_NUMBER}

        # Run appointment-service
        docker run -d \
        --name appointment-service \
        -p 8103:8103 \
        sunardock/appointment-service:${BUILD_NUMBER}

        # Run therapist-service
        docker run -d \
        --name therapist-service \
        -p 8104:8104 \
        sunardock/therapist-service:${BUILD_NUMBER}

        # Run file-upload-service
        docker run -d \
        --name file-upload-service \
        -p 8105:8105 \
        sunardock/file-upload-service:${BUILD_NUMBER}
        '''
    }
}

    }

}
