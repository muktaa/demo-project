pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'your-registry'  // Update with your Docker registry
        KUBE_NAMESPACE = 'last9-otel-demo'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build and Push User Service') {
            steps {
                dir('user-service') {
                    sh 'mvn clean package -DskipTests'
                    sh 'docker build -t ${DOCKER_REGISTRY}/user-service:${BUILD_NUMBER} .'
                    sh 'docker push ${DOCKER_REGISTRY}/user-service:${BUILD_NUMBER}'
                }
            }
        }
        
        stage('Build and Push Order Service') {
            steps {
                dir('order-service') {
                    sh 'mvn clean package -DskipTests'
                    sh 'docker build -t ${DOCKER_REGISTRY}/order-service:${BUILD_NUMBER} .'
                    sh 'docker push ${DOCKER_REGISTRY}/order-service:${BUILD_NUMBER}'
                }
            }
        }
        
        stage('Build and Push Notification Service') {
            steps {
                dir('notification-service') {
                    sh 'mvn clean package -DskipTests'
                    sh 'docker build -t ${DOCKER_REGISTRY}/notification-service:${BUILD_NUMBER} .'
                    sh 'docker push ${DOCKER_REGISTRY}/notification-service:${BUILD_NUMBER}'
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    // Update image versions in Kubernetes manifests
                    sh '''
                        sed -i 's|image: .*|image: ${DOCKER_REGISTRY}/user-service:${BUILD_NUMBER}|g' k8s/user-service.yaml
                        sed -i 's|image: .*|image: ${DOCKER_REGISTRY}/order-service:${BUILD_NUMBER}|g' k8s/order-service.yaml
                        sed -i 's|image: .*|image: ${DOCKER_REGISTRY}/notification-service:${BUILD_NUMBER}|g' k8s/notification-service.yaml
                    '''
                    
                    // Apply Kubernetes manifests
                    sh '''
                        kubectl apply -f k8s/namespace.yaml
                        kubectl apply -f k8s/postgres.yaml
                        kubectl apply -f k8s/zipkin.yaml
                        kubectl apply -f k8s/user-service.yaml
                        kubectl apply -f k8s/order-service.yaml
                        kubectl apply -f k8s/notification-service.yaml
                        kubectl apply -f k8s/ingress.yaml
                    '''
                }
            }
        }
        
        stage('Verify Deployment') {
            steps {
                script {
                    // Wait for pods to be ready
                    sh '''
                        kubectl rollout status deployment/user-service -n ${KUBE_NAMESPACE}
                        kubectl rollout status deployment/order-service -n ${KUBE_NAMESPACE}
                        kubectl rollout status deployment/notification-service -n ${KUBE_NAMESPACE}
                    '''
                    
                    // Verify services are accessible
                    sh '''
                        kubectl get pods -n ${KUBE_NAMESPACE}
                        kubectl get services -n ${KUBE_NAMESPACE}
                    '''
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Deployment completed successfully!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
} 