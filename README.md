# Distributed Microservices Project

## Project Structure
```
demo-project/
├── user-service/
│   ├── src/main/java/com/demo/userservice/
│   ├── Dockerfile
│   └── pom.xml
├── order-service/
│   ├── src/main/java/com/demo/orderservice/
│   ├── Dockerfile
│   └── pom.xml
├── notification-service/
│   ├── src/main/java/com/demo/notificationservice/
│   ├── Dockerfile
│   └── pom.xml
├── k8s/
│   ├── namespace.yaml
│   ├── user-service.yaml
│   ├── order-service.yaml
│   ├── notification-service.yaml
│   ├── postgres.yaml
│   └── ingress.yaml
├── jenkins/
│   ├── Jenkinsfile
│   └── deploy-pod.yaml
├── docker-compose.yml
└── README.md
```

## Architecture Overview

### Services:
1. **User Service** (Port 8081)
   - Manages user registration and authentication
   - PostgreSQL database for user data
   - Calls external weather API

2. **Order Service** (Port 8082)
   - Handles order creation and management
   - Calls User Service to validate users
   - PostgreSQL database for order data

3. **Notification Service** (Port 8083)
   - Sends notifications for orders
   - Called by Order Service
   - Calls external news API
   - In-memory H2 database for notification logs

### Distributed Tracing Flow:
```
Client Request → User Service → Weather API
                      ↓
                Order Service → User Service (validation)
                      ↓
              Notification Service → News API
```

## Technology Stack
- **Framework**: Spring Boot 2.7.x
- **Tracing**: Spring Cloud Sleuth + Zipkin
- **Database**: PostgreSQL (User & Order), H2 (Notification)
- **Service Discovery**: Kubernetes native
- **Containerization**: Docker
- **Orchestration**: Kubernetes (EKS)
- **CI/CD**: Jenkins

## External APIs Used
- **Weather API**: Open-Meteo (free, no API key required) - https://api.open-meteo.com/v1/forecast
- **News API**: Hacker News API (free, no API key required) - https://hacker-news.firebaseio.com/v0/topstories.json

## Prerequisites
1. AWS Account with EKS cluster
2. Jenkins with Kubernetes plugin
3. Docker Hub account (or AWS ECR)
4. kubectl configured for your EKS cluster

## Setup Instructions

### 1. Local Development
For local testing, use Docker Compose:
```bash
# Build and start all services
docker-compose up --build

# Access services:
# User Service: http://localhost:8081
# Order Service: http://localhost:8082
# Notification Service: http://localhost:8083
# Zipkin UI: http://localhost:9411
```

### 2. EKS Deployment
1. Create EKS cluster
2. Configure kubectl
3. Update image registry in k8s YAML files
4. Deploy using kubectl or Jenkins pipeline

### 3. Jenkins Configuration
- Install Kubernetes plugin
- Configure AWS credentials
- Add GitHub webhook for automatic builds

### 4. Monitoring Setup
Deploy Zipkin for distributed tracing:
```bash
kubectl apply -f https://raw.githubusercontent.com/openzipkin/zipkin/master/docker/examples/docker-compose-zipkin.yml
```

## API Endpoints

### User Service (8081)
- `POST /api/users/register` - Register new user
- `GET /api/users/{id}` - Get user details
- `GET /api/users/{id}/weather` - Get weather for user's location

### Order Service (8082)
- `POST /api/orders` - Create new order
- `GET /api/orders/{id}` - Get order details
- `GET /api/orders/user/{userId}` - Get orders by user

### Notification Service (8083)
- `POST /api/notifications/send` - Send notification
- `GET /api/notifications/{id}` - Get notification status
- `GET /api/notifications/news` - Get latest news

## Tracing Verification
Once deployed, you can verify distributed tracing by:
1. Making a request to create an order
2. Checking Zipkin UI for the complete trace
3. Observing spans across all three services plus external API calls 

## Security Considerations

### Sensitive Information
Before pushing to a public repository:
1. Never commit sensitive information like:
   - Passwords
   - API keys
   - AWS credentials
   - Docker registry credentials
2. Use environment variables or Kubernetes secrets
3. Follow the template in `k8s/secrets.template.yaml`

### Setting up Secrets
1. Create Kubernetes secrets:
```bash
# Create namespace
kubectl create namespace microservices-demo

# Create secrets from template
cp k8s/secrets.template.yaml k8s/secrets.yaml
# Edit secrets.yaml with your values
kubectl apply -f k8s/secrets.yaml
```

2. Update environment variables in Jenkins:
   - Go to Jenkins > Credentials > System > Global credentials
   - Add Docker registry credentials
   - Add AWS credentials 