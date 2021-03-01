
pipeline {
    agent none
    options {
        checkoutToSubdirectory('gr.grnet.eseal')
        newContainerPerStage()
    }
    environment {
        PROJECT_DIR='gr.grnet.eseal'
        GH_USER = 'newgrnetci'
        GH_EMAIL = '<argo@grnet.gr>'
    }
    stages {
        stage('Library Testing & Packaging') {
            agent {
                docker {
                    image 'argo.registry:5000/epel-7-java18'
                    args '-u jenkins:jenkins'
                }
            }
            steps {
                echo 'Eseal Packaging & Testing'
            }
            post {
                always {
                    cleanWs()
                }
            }
        }
        stage ('Deploy Docs') {
            when {
                anyOf {
                    changeset 'website/**'
                }
            }
            agent {
                docker {
                    image 'node:buster'
                }
            }
            steps {
                echo 'Publish gr.grnet.eseal docs...'
                sh '''
                    cd $WORKSPACE/$PROJECT_DIR
                    cd website
                    npm install
                '''
                sshagent (credentials: ['jenkins-master']) {
                    sh '''
                        cd $WORKSPACE/$PROJECT_DIR/website
                        mkdir ~/.ssh && ssh-keyscan -H github.com > ~/.ssh/known_hosts
                        git config --global user.email ${GH_EMAIL}
                        git config --global user.name ${GH_USER}
                        GIT_USER=${GH_USER} USE_SSH=true npm run deploy
                    '''
                }
            }
        }
    }
    post {
        success {
            script{
                if ( env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'devel' ) {
                    slackSend( message: ":rocket: New version for <$BUILD_URL|$PROJECT_DIR>:$BRANCH_NAME Job: $JOB_NAME !")
                }
            }
        }
        failure {
            script{
                if ( env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'devel' ) {
                    slackSend( message: ":rain_cloud: Build Failed for <$BUILD_URL|$PROJECT_DIR>:$BRANCH_NAME Job: $JOB_NAME")
                }
            }
        }
    }
}
