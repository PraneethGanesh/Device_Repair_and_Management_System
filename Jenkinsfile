pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        DOCKERHUB_USER = 'your-dockerhub-username'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Root Project') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test Root Project') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Docker Build & Push - All Services') {
            matrix {
                axes {
                    axis {
                        name 'SERVICE'
                        values 'eureka-server',
                                'api-gateway',
                                'user-service',
                                'vendor-service',
                                'customer-service',
                                'device-service',
                                'order-service',
                                'repair-service',
                                'notification-service',
                                'audit-service'
                    }
                }

                stages {
                    stage('Docker Build') {
                        steps {
                            dir("${SERVICE}") {
                                sh "docker build -t ${DOCKERHUB_USER}/${SERVICE}:latest ."
                            }
                        }
                    }

                    stage('Docker Push') {
                        steps {
                            sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                            sh "docker push ${DOCKERHUB_USER}/${SERVICE}:latest"
                        }
                    }
                }
            }
        }

        stage('Deploy Placeholder') {
            steps {
                echo 'Deploy stage placeholder. Add Docker Compose or Kubernetes deployment later.'
            }
        }
    }

    post {
        always {
            sh 'docker logout || true'
            cleanWs()
        }
        success {
            echo 'All services built, tested, and pushed successfully.'
        }
        failure {
            echo 'Pipeline failed. Check the failed stage logs.'
        }
    }
}