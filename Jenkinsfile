
pipeline {
    agent none
    options {
        checkoutToSubdirectory('gr.grnet.eseal')
        newContainerPerStage()
    }
    environment {
        PROJECT_DIR='gr.grnet.eseal'
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
                echo 'Eseal library Packaging & Testing' 
                sh """
                mvn clean package cobertura:cobertura -Dcobertura.report.format=xml -f ${PROJECT_DIR}/eseal/pom.xml
                """
                junit '**/target/surefire-reports/*.xml'
                cobertura coberturaReportFile: '**/target/site/cobertura/coverage.xml'
            }
            post {
                always {
                    cleanWs()
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