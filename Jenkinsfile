pipeline {
    agent any

    tools {
        maven 'Maven-3.9.16'
        jdk 'JDK-21'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('**')
        DOCKERHUB_USER        = '***'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build, Test & Push - All Services') {
            matrix {
                axes {
                    axis {
                        name 'SERVICE'
                        values 'eureka-server', 'api-gateway', 'auth-service', 'vendor-service', \
                               'device-service', 'order-service', 'repair-service', \
                               'notification-service', 'audit-service'
                    }
                }

                stages {

                    stage('Build') {
                        steps {
                            dir("${SERVICE}") {
                                bat 'mvn clean package -DskipTests'
                            }
                        }
                    }

                    stage('Test') {
                        steps {
                            dir("${SERVICE}") {
                                bat 'mvn test'
                            }
                        }
                        post {
                            always {
                                junit testResults: "${SERVICE}/target/surefire-reports/*.xml", allowEmptyResults: true
                            }
                        }
                    }

                    stage('Docker Build & Push') {
                        steps {
                            dir("${SERVICE}") {
                                bat "docker build -t %DOCKERHUB_USER%/${SERVICE}:latest ."
                                bat 'echo %DOCKERHUB_CREDENTIALS_PSW% | docker login -u %DOCKERHUB_CREDENTIALS_USR% --password-stdin'
                                bat "docker push %DOCKERHUB_USER%/${SERVICE}:latest"
                            }
                        }
                    }

                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    echo 'Deploy stage placeholder - wire this in once your k8s manifests are ready.'
                    // Example of what this will look like later:
                    //
                    // def services = ['eureka-server', 'api-gateway', 'auth-service', 'vendor-service',
                    //                  'device-service', 'order-service', 'repair-service',
                    //                  'notification-service', 'audit-service']
                    //
                    // for (svc in services) {
                    //     bat "kubectl set image deployment/${svc} ${svc}=%DOCKERHUB_USER%/${svc}:latest --record"
                    // }
                    //
                    // OR, if applying full manifests per service:
                    // bat "kubectl apply -f k8s/${svc}/deployment.yaml"
                    // bat "kubectl apply -f k8s/${svc}/service.yaml"
                }
            }
        }

    }

    post {
        always {
            bat 'docker logout'
        }
        success {
            echo 'All services built, tested, and pushed successfully.'
        }
        failure {
            echo 'Pipeline failed - check matrix stage logs to see which service failed.'
        }
    }
}