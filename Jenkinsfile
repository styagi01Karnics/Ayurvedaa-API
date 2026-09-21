pipeline {

    agent any

    options {
        timestamps()

        /*
         * Prevent two builds of the SAME branch from running together.
         *
         * Deployment itself is additionally protected by flock on the
         * application server so different branch jobs cannot deploy
         * simultaneously.
         */
        disableConcurrentBuilds()

        skipDefaultCheckout(true)

        /*
         * Keep Jenkins build history under control.
         */
        buildDiscarder(
            logRotator(
                numToKeepStr: '20',
                daysToKeepStr: '30'
            )
        )
    }

    environment {

        /*
         * ============================================================
         * DOCKER HUB
         * ============================================================
         */

        IMAGE_PREFIX = 'sunardock/ayurvedaa-api'

        DOCKER_CREDENTIALS = 'dockerhub-creds'


        /*
         * ============================================================
         * APPLICATION SERVER
         * ============================================================
         */

        APP_SERVER = '45.195.229.15'
        APP_DIR = '/root/ayurvedaa'


        /*
         * Jenkins Username/Password credential used to SSH
         * into the application server.
         */
        SSH_CREDENTIALS = 'new-server-ssh'
    }


    stages {


        // ============================================================
        // 1. INITIALIZE
        // ============================================================

        stage('Initialize') {

            steps {

                script {

                    /*
                     * Jenkins Multibranch automatically provides
                     * BRANCH_NAME.
                     *
                     * Examples:
                     *
                     * fixes-development
                     * dev-sonarqube-common
                     */

                    env.SAFE_BRANCH = (env.BRANCH_NAME ?: 'unknown')
                        .replaceAll(/[^A-Za-z0-9_.-]/, '-')


                    /*
                     * Branch-specific image tag.
                     *
                     * Example:
                     *
                     * fixes-development-25
                     * dev-sonarqube-common-12
                     *
                     * IMPORTANT:
                     *
                     * Containers are NOT branch-specific.
                     * Only the image tag is branch-specific.
                     */

                    env.IMAGE_TAG =
                        "${env.SAFE_BRANCH}-${env.BUILD_NUMBER}"


                    echo "================================================"
                    echo "AYURVEDAA MULTIBRANCH PIPELINE"
                    echo "================================================"
                    echo "Branch       : ${env.BRANCH_NAME}"
                    echo "Safe Branch  : ${env.SAFE_BRANCH}"
                    echo "Build Number : ${env.BUILD_NUMBER}"
                    echo "Image Tag    : ${env.IMAGE_TAG}"
                    echo "================================================"
                }
            }
        }


        // ============================================================
        // 2. CHECKOUT
        // ============================================================

        stage('Checkout') {

            steps {

                /*
                 * IMPORTANT:
                 *
                 * Do not specify a branch manually.
                 *
                 * checkout scm automatically checks out the branch
                 * selected by the Multibranch Pipeline.
                 */

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


        // ============================================================
        // 3. ENVIRONMENT CHECK
        // ============================================================

        stage('Environment Check') {

            steps {

                sh '''
                    set -e

                    echo "Checking Jenkins build environment..."

                    command -v java
                    command -v mvn
                    command -v docker
                    command -v git

                    echo ""
                    echo "Environment check completed."
                '''
            }
        }


        // ============================================================
        // 4. MAVEN TEST + PACKAGE
        // ============================================================

        stage('Maven Test & Package') {

            steps {

                sh '''
                    set -e

                    echo "Running Maven clean test package..."

                    mvn clean test package -DskipTests=false

                    echo ""
                    echo "Maven build completed successfully."
                '''
            }
        }


        // ============================================================
        // 5. SONARQUBE
        // ============================================================

        stage('SonarQube Analysis') {

            steps {

                withSonarQubeEnv('sonarqube') {

                    sh '''
                        set -e

                        echo "Running SonarQube analysis..."

                        mvn sonar:sonar

                        echo ""
                        echo "SonarQube analysis completed."
                    '''
                }
            }
        }


        // ============================================================
        // 6. VERIFY DOCKERFILES
        // ============================================================

        stage('Verify Dockerfiles') {

            steps {

                sh '''
                    set -e

                    echo "Checking Ayurvedaa Dockerfiles..."

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

                    echo ""
                    echo "All required Dockerfiles found."
                '''
            }
        }


        // ============================================================
        // 7. DOCKER BUILD
        // ============================================================

        stage('Docker Build') {

            steps {

                sh '''
                    set -e

                    echo "================================================"
                    echo "BUILDING DOCKER IMAGES"
                    echo "================================================"

                    echo "Image tag: ${IMAGE_TAG}"

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

                    echo ""
                    echo "Docker build completed successfully."
                '''
            }
        }


        // ============================================================
        // 8. DOCKER PUSH
        // ============================================================

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

                        echo "Logging into Docker Hub..."

                        echo "${DOCKER_PASSWORD}" | docker login \
                            --username "${DOCKER_USERNAME}" \
                            --password-stdin


                        echo ""
                        echo "Pushing Ayurvedaa images..."


                        docker push \
                            ${IMAGE_PREFIX}-patient-service:${IMAGE_TAG}

                        docker push \
                            ${IMAGE_PREFIX}-doctor-service:${IMAGE_TAG}

                        docker push \
                            ${IMAGE_PREFIX}-appointment-service:${IMAGE_TAG}

                        docker push \
                            ${IMAGE_PREFIX}-therapist-service:${IMAGE_TAG}

                        docker push \
                            ${IMAGE_PREFIX}-file-upload-service:${IMAGE_TAG}

                        docker push \
                            ${IMAGE_PREFIX}-attendance-service:${IMAGE_TAG}

                        docker push \
                            ${IMAGE_PREFIX}-activity-log-service:${IMAGE_TAG}

                        docker push \
                            ${IMAGE_PREFIX}-medicine-service:${IMAGE_TAG}

                        docker push \
                            ${IMAGE_PREFIX}-billing-service:${IMAGE_TAG}

                        docker push \
                            ${IMAGE_PREFIX}-notification-service:${IMAGE_TAG}

                        docker push \
                            ${IMAGE_PREFIX}-auth-service:${IMAGE_TAG}

                        docker push \
                            ${IMAGE_PREFIX}-payment-service:${IMAGE_TAG}


                        docker logout


                        echo ""
                        echo "Docker push completed successfully."
                    '''
                }
            }
        }


        // ============================================================
        // 9. DEPLOY
        // ============================================================

        stage('Deploy') {

            /*
             * Only your currently used branches deploy.
             *
             * Both branches use the SAME deployment.
             *
             * There is NOT one container set for each branch.
             */

            when {

                anyOf {

                    branch 'fixes-development'

                    branch 'dev-sonarqube-common'
                }
            }


            steps {

                /*
                 * Your new-server-ssh credential is Username/Password,
                 * therefore we use usernamePassword instead of sshagent.
                 */

                withCredentials([
                    usernamePassword(
                        credentialsId: "${SSH_CREDENTIALS}",
                        usernameVariable: 'SSH_USER',
                        passwordVariable: 'SSH_PASSWORD'
                    )
                ]) {

                    sh '''
                        set -e

                        echo "================================================"
                        echo "AYURVEDAA DEPLOYMENT"
                        echo "================================================"
                        echo "Branch    : ${BRANCH_NAME}"
                        echo "Image Tag : ${IMAGE_TAG}"
                        echo "Server    : ${APP_SERVER}"
                        echo "================================================"


                        /*
                         * SSHPASS is passed through the environment so
                         * the password is not placed directly in the
                         * SSH command.
                         */

                        export SSHPASS="${SSH_PASSWORD}"


                        sshpass -e ssh \
                            -o StrictHostKeyChecking=no \
                            -o UserKnownHostsFile=/dev/null \
                            ${SSH_USER}@${APP_SERVER} \
                            "IMAGE_TAG='${IMAGE_TAG}' APP_DIR='${APP_DIR}' bash -s" <<'REMOTE_SCRIPT'

                            set -e


                            # ==================================================
                            # GLOBAL DEPLOYMENT LOCK
                            # ==================================================
                            #
                            # This lock is on the application server.
                            #
                            # If fixes-development and
                            # dev-sonarqube-common jobs start together,
                            # only ONE deployment runs at a time.
                            #
                            # This is important because both branches deploy
                            # to the SAME Ayurvedaa containers.
                            # ==================================================

                            exec 9>/var/lock/ayurvedaa-api-deployment.lock

                            flock 9


                            echo ""
                            echo "Deployment lock acquired."


                            cd "${APP_DIR}"


                            # ==================================================
                            # CURRENT DEPLOYMENT
                            # ==================================================

                            echo ""
                            echo "Current Ayurvedaa containers:"
                            docker ps \
                                --filter "name=ayurvedaa" \
                                --format "table {{.Names}}\\t{{.Image}}\\t{{.Status}}"


                            # ==================================================
                            # STOP AND REMOVE OLD CONTAINERS
                            # ==================================================
                            #
                            # This is NOT branch-specific.
                            #
                            # Whatever branch is currently running will be
                            # stopped and removed.
                            #
                            # Then the new branch version will start.
                            # ==================================================

                            echo ""
                            echo "Stopping current Ayurvedaa deployment..."

                            docker compose down --remove-orphans


                            # ==================================================
                            # PULL NEW IMAGES
                            # ==================================================

                            echo ""
                            echo "Pulling images with IMAGE_TAG=${IMAGE_TAG}..."

                            IMAGE_TAG="${IMAGE_TAG}" \
                                docker compose pull


                            # ==================================================
                            # START NEW DEPLOYMENT
                            # ==================================================

                            echo ""
                            echo "Starting new Ayurvedaa deployment..."

                            IMAGE_TAG="${IMAGE_TAG}" \
                                docker compose up -d --remove-orphans


                            # ==================================================
                            # VERIFY CONTAINERS
                            # ==================================================

                            echo ""
                            echo "New Ayurvedaa containers:"

                            docker ps \
                                --filter "name=ayurvedaa" \
                                --format "table {{.Names}}\\t{{.Image}}\\t{{.Status}}"


                            echo ""
                            echo "Deployment completed."


                            # ==================================================
                            # SHOW DEPLOYED IMAGE TAG
                            # ==================================================

                            echo ""
                            echo "Images currently deployed:"

                            docker ps \
                                --filter "name=ayurvedaa" \
                                --format "{{.Names}} -> {{.Image}}"


                            echo ""
                            echo "Deployment lock released when SSH session exits."

REMOTE_SCRIPT

                    unset SSHPASS
                }
            }
        }


        // ============================================================
        // 10. DEPLOYMENT VERIFICATION
        // ============================================================

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
                            ${SSH_USER}@${APP_SERVER} \
                            "cd ${APP_DIR} && docker compose ps"


                        unset SSHPASS
                    '''
                }
            }
        }


        // ============================================================
        // 11. CLEANUP OLD AYURVEDAA IMAGES
        // ============================================================

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
                            ${SSH_USER}@${APP_SERVER} \
                            'bash -s' <<'REMOTE_CLEANUP'

                            set -e


                            echo "================================================"
                            echo "AYURVEDAA DOCKER IMAGE CLEANUP"
                            echo "================================================"

                            /*
                             * IMPORTANT:
                             *
                             * Cleanup is ONLY for these repositories:
                             *
                             * sunardock/ayurvedaa-api-*
                             *
                             * Nothing else is touched.
                             */


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
                                echo "--------------------------------------------"
                                echo "Service: ${SERVICE}"
                                echo "Repository: ${REPOSITORY}"
                                echo "--------------------------------------------"


                                /*
                                 * Get images ordered by Docker creation time.
                                 *
                                 * The newest 3 are retained.
                                 *
                                 * Older images are removed unless currently
                                 * used by a running container.
                                 */


                                docker image ls "${REPOSITORY}" \
                                    --format '{{.CreatedAt}}|{{.Repository}}:{{.Tag}}|{{.ID}}' \
                                    | sort -r \
                                    > /tmp/ayurvedaa-${SERVICE}-images.txt


                                COUNT=0


                                while IFS='|' read -r CREATED IMAGE IMAGE_ID
                                do

                                    if [ -z "${IMAGE}" ]
                                    then
                                        continue
                                    fi


                                    COUNT=$((COUNT + 1))


                                    if [ "${COUNT}" -le 3 ]
                                    then

                                        echo "KEEP : ${IMAGE}"

                                        continue

                                    fi


                                    /*
                                     * Check whether this exact image is
                                     * currently being used by a running
                                     * container.
                                     */

                                    RUNNING_CONTAINER=$(docker ps \
                                        --filter "ancestor=${IMAGE}" \
                                        --format '{{.Names}}' \
                                        | head -n 1)


                                    if [ -n "${RUNNING_CONTAINER}" ]
                                    then

                                        echo "SKIP : ${IMAGE}"
                                        echo "       Currently used by: ${RUNNING_CONTAINER}"

                                    else

                                        echo "REMOVE: ${IMAGE}"

                                        docker image rm "${IMAGE}" || true

                                    fi

                                done < /tmp/ayurvedaa-${SERVICE}-images.txt


                                rm -f /tmp/ayurvedaa-${SERVICE}-images.txt

                            done


                            echo ""
                            echo "================================================"
                            echo "CLEANUP COMPLETED"
                            echo "================================================"


                            echo ""
                            echo "Remaining Ayurvedaa images:"


                            docker images \
                                --format '{{.Repository}}:{{.Tag}}\t{{.CreatedAt}}' \
                                | grep '^sunardock/ayurvedaa-api-' \
                                | sort


                            echo ""
                            echo "Other application images were NOT touched."

REMOTE_CLEANUP


                        unset SSHPASS
                    '''
                }
            }
        }
    }


    // ================================================================
    // POST ACTIONS
    // ================================================================

    post {

        success {

            echo ""
            echo "================================================"
            echo "AYURVEDAA PIPELINE SUCCESS"
            echo "================================================"
            echo "Branch    : ${env.BRANCH_NAME}"
            echo "Build     : ${env.BUILD_NUMBER}"
            echo "Image Tag : ${env.IMAGE_TAG}"
            echo ""
            echo "The shared Ayurvedaa deployment now uses this build."
            echo "================================================"
        }


        failure {

            echo ""
            echo "================================================"
            echo "AYURVEDAA PIPELINE FAILED"
            echo "================================================"
            echo "Branch : ${env.BRANCH_NAME}"
            echo "Build  : ${env.BUILD_NUMBER}"
            echo ""
            echo "Check the Jenkins Console Output."
            echo "================================================"
        }


        always {

            echo ""
            echo "Pipeline completed."
            echo "Branch: ${env.BRANCH_NAME}"
        }
    }
}
