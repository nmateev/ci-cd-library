
def call(){

    pipeline {
        agent { label 'generic' }
        stages {
            stage ('Clean workspace') {
                steps {
                    script {
                        if (params.CLEAN_WORKSPACE) {
                            println('Cleaning working directory')
                            cleanWs()
                            sh """
                                  pwd
                                  ls
                               """

                        } else {
                            println('Skipping cleaning of workspace')
                        }
                    }

                }
            }

            stage ('Checkout') {
                steps {
                    git 'https://github.com/nmateev/example.git'
                    sh 'pwd'
                    sh 'ls'
                }
            }

            stage ('Build') {
                steps {
                    sh 'chmod +x ./gradlew'
                    script {

                        if (params.SKIP_TESTS) {
                            sh './gradlew clean build -x test'
                        } else {
                            sh './gradlew clean build'
                        }
                    }
                }
            }

            stage ('Archive') {
                steps {
                    script {
                        try {
                            archiveArtifacts params.ARCHIVE

                        } catch (Exception e) {
                            println('Bad archive configuration')
                            println(e)

                        }
                    }
                }
            }
        }
        post {
            success {
                println("Successful build from branch: ${env.BRANCH_NAME}")
            }
            failure {
                println("Unsuccessful build number: ${env.BUILD_ID} from ${env.BRANCH_NAME}")
            }

        }
    }
}