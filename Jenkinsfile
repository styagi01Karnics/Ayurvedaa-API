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
        docker build -t sunardock/ayurvedaa-api-patient-service:${BUILD_NUMBER} ./patient-service
        docker build -t sunardock/ayurvedaa-api-doctor-service:${BUILD_NUMBER} ./doctor-service
        docker build -t sunardock/ayurvedaa-api-appointment-service:${BUILD_NUMBER} ./appointment-service
        docker build -t sunardock/ayurvedaa-api-therapist-service:${BUILD_NUMBER} ./therapist-service
        docker build -t sunardock/ayurvedaa-api-file-upload-service:${BUILD_NUMBER} ./file-upload-service
        docker build -t sunardock/ayurvedaa-api-attendance-service:${BUILD_NUMBER} ./attendance-service
        docker build -t sunardock/ayurvedaa-api-auth-service:${BUILD_NUMBER} ./auth-service
        docker build -t sunardock/ayurvedaa-api-activity-log-service:${BUILD_NUMBER} ./activity-log-service
        docker build -t sunardock/ayurvedaa-api-medicine-service:${BUILD_NUMBER} ./medicine-service
        docker build -t sunardock/ayurvedaa-api-billing-service:${BUILD_NUMBER} ./billing-service
        docker build -t sunardock/ayurvedaa-api-notification-service:${BUILD_NUMBER} ./notification-service
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
            docker push sunardock/ayurvedaa-api-auth-service:${BUILD_NUMBER}
            docker push sunardock/ayurvedaa-api-activity-log-service:${BUILD_NUMBER}
            docker push sunardock/ayurvedaa-api-medicine-service:${BUILD_NUMBER}
            docker push sunardock/ayurvedaa-api-billing-service:${BUILD_NUMBER}
            docker push sunardock/ayurvedaa-api-notification-service:${BUILD_NUMBER}
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
        docker rm -f ayurvedaa-api-auth-service || true
        docker rm -f ayurvedaa-api-activity-log-service || true
        docker rm -f ayurvedaa-api-medicine-service || true
        docker rm -f ayurvedaa-api-billing-service || true
        docker rm -f ayurvedaa-api-notification-service || true

       # Run patient-service
        docker run -d \
        --restart unless-stopped \
        --network monitoring \
        --network-alias ayurvedaa-api-patient-service \
        --name ayurvedaa-api-patient-service \
        -p 8101:8101 \
        sunardock/ayurvedaa-api-patient-service:${BUILD_NUMBER}
        
        # Run doctor-service
        docker run -d \
        --restart unless-stopped \
        --network monitoring \
        --network-alias ayurvedaa-api-doctor-service \
        --name ayurvedaa-api-doctor-service \
        -p 8102:8102 \
        sunardock/ayurvedaa-api-doctor-service:${BUILD_NUMBER}
        
        # Run appointment-service
        docker run -d \
        --restart unless-stopped \
        --network monitoring \
        --network-alias ayurvedaa-api-appointment-service \
        --name ayurvedaa-api-appointment-service \
        -p 8103:8103 \
        sunardock/ayurvedaa-api-appointment-service:${BUILD_NUMBER}
        
        # Run therapist-service
        docker run -d \
        --restart unless-stopped \
        --network monitoring \
        --network-alias ayurvedaa-api-therapist-service \
        --name ayurvedaa-api-therapist-service \
        -p 8104:8104 \
        sunardock/ayurvedaa-api-therapist-service:${BUILD_NUMBER}
        
        # Run file-upload-service
        docker run -d \
        --restart unless-stopped \
        --network monitoring \
        --network-alias ayurvedaa-api-file-upload-service \
        --name ayurvedaa-api-file-upload-service \
        -p 8105:8105 \
        sunardock/ayurvedaa-api-file-upload-service:${BUILD_NUMBER}
        
        # Run attendance-service
        docker run -d \
        --restart unless-stopped \
        --network monitoring \
        --network-alias ayurvedaa-api-attendance-service \
        --name ayurvedaa-api-attendance-service \
        -p 8106:8106 \
        sunardock/ayurvedaa-api-attendance-service:${BUILD_NUMBER}
        
        # Run activity-log-service
        docker run -d \
        --restart unless-stopped \
        --network monitoring \
        --network-alias ayurvedaa-api-activity-log-service \
        --name ayurvedaa-api-activity-log-service \
        -p 8107:8107 \
        sunardock/ayurvedaa-api-activity-log-service:${BUILD_NUMBER}
        
        # Run medicine-service
        docker run -d \
        --restart unless-stopped \
        --network monitoring \
        --network-alias ayurvedaa-api-medicine-service \
        --name ayurvedaa-api-medicine-service \
        -p 8108:8108 \
        sunardock/ayurvedaa-api-medicine-service:${BUILD_NUMBER}
        
        # Run billing-service
        docker run -d \
        --restart unless-stopped \
        --network monitoring \
        --network-alias ayurvedaa-api-billing-service \
        --name ayurvedaa-api-billing-service \
        -p 8109:8109 \
        sunardock/ayurvedaa-api-billing-service:${BUILD_NUMBER}
        
        # Run notification-service
        docker run -d \
        --restart unless-stopped \
        --network monitoring \
        --network-alias ayurvedaa-api-notification-service \
        --name ayurvedaa-api-notification-service \
        -p 8110:8110 \
        sunardock/ayurvedaa-api-notification-service:${BUILD_NUMBER}
        
        # Run auth-service
        docker run -d \
        --restart unless-stopped \
        --network monitoring \
        --network-alias ayurvedaa-api-auth-service \
        --name ayurvedaa-api-auth-service \
        -p 8111:8111 \
        sunardock/ayurvedaa-api-auth-service:${BUILD_NUMBER}
        '''
    }
}

       stage('Cleanup Old Docker Images') {
    steps {
        sh '''
        echo "Removing old Docker images (keeping latest 3)..."

        REPOS=$(docker images --format "{{.Repository}}" | grep "^sunardock/ayurvedaa-api-" | sort -u)

        for REPO in $REPOS
        do
            echo "Processing $REPO"

            docker images $REPO \
            --format "{{.Tag}}" \
            | grep -E '^[0-9]+$' \
            | sort -rn \
            | tail -n +4 \
            | while read TAG
            do
                echo "Deleting $REPO:$TAG"
                docker rmi -f $REPO:$TAG || true
            done
        done

        docker image prune -f
        '''
    }
}
    }

}
