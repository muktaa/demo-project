# Distributed Microservices Project with OpenTelemetry

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
├── microservices-ui/
│   ├── src/
│   ├── Dockerfile
│   └── package.json
├── k8s/
│   ├── local/
│   │   ├── namespace.yaml
│   │   ├── user-service.yaml
│   │   ├── order-service.yaml
│   │   ├── notification-service.yaml
│   │   ├── postgres.yaml
│   │   ├── postgres-init.yaml
│   │   ├── otel-collector.yaml
│   │   └── ingress.yaml
│   └── jenkins/
│       ├── namespace.yaml
│       ├── user-service.yaml
│       ├── order-service.yaml
│       ├── notification-service.yaml
│       ├── postgres.yaml
│       ├── postgres-init.yaml
│       ├── otel-collector.yaml
│       └── ingress.yaml
├── jenkins/
│   ├── Jenkinsfile
│   └── deploy-pod.yaml
├── test/
│   └── local/
│       └── load-gen.sh
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

4. **Microservices UI** (Port 80)
   - Angular-based frontend
   - Interacts with all backend services
   - Real-time updates and notifications

### Distributed Tracing Flow:
```
Client Request → Microservices UI → User Service → Weather API
                      ↓
                Order Service → User Service (validation)
                      ↓
              Notification Service → News API
```

## Technology Stack
- **Framework**: Spring Boot 2.7.x
- **Observability**: OpenTelemetry
- **Database**: PostgreSQL (User & Order), H2 (Notification)
- **Service Discovery**: Kubernetes native
- **Containerization**: Docker
- **Orchestration**: Kubernetes (EKS)
- **CI/CD**: Jenkins
- **Frontend**: Angular

## External APIs Used
- **Weather API**: Open-Meteo (free, no API key required) - https://api.open-meteo.com/v1/forecast
- **News API**: Hacker News API (free, no API key required) - https://hacker-news.firebaseio.com/v0/topstories.json

## Prerequisites
1. AWS Account with EKS cluster
2. Jenkins with Kubernetes plugin
3. Docker Hub account
4. kubectl configured for your EKS cluster
5. Last9 OpenTelemetry credentials

## Setup Instructions

### 1. Local Development
For local testing, use Docker Compose:
```bash
# Build and start all services
docker-compose up --build

# Access services:
# Microservices UI: http://localhost
# User Service: http://localhost:8081
# Order Service: http://localhost:8082
# Notification Service: http://localhost:8083
```

### 2. Local Kubernetes Deployment
```bash
# Create namespace
kubectl apply -f k8s/local/namespace.yaml

# Deploy all resources
kubectl apply -f k8s/local --validate=false -n last9-otel-demo

# setup
./test/local/setup.sh

# Generate load (optional)
./test/local/load-gen.sh
```

### 3. Jenkins Deployment
1. Configure Jenkins credentials:
   - Add `last9-auth-header` credential with your Last9 OpenTelemetry auth header
   - Add AWS credentials for EKS access

2. Run the Jenkins pipeline:
   - The pipeline will automatically:
     - Create the namespace
     - Set up OpenTelemetry collector
     - Deploy all services
     - Configure ingress

### 4. Monitoring Setup
The OpenTelemetry collector is automatically configured to:
- Collect traces, metrics, and logs
- Export data to Last9
- Provide local debugging through logging exporter

## API Endpoints

### Microservices UI
- `GET /` - Main application interface
- `GET /api/users` - User management interface
- `GET /api/orders` - Order management interface

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

## Observability Verification
Once deployed, you can verify distributed tracing by:
1. Making requests through the UI or load generator
2. Checking Last9 dashboard for:
   - Service maps
   - Trace details
   - Metrics
   - Logs

## Security Considerations

### Sensitive Information
Before pushing to a public repository:
1. Never commit sensitive information like:
   - Passwords
   - API keys
   - AWS credentials
   - Docker registry credentials
   - OpenTelemetry auth headers
2. Use environment variables or Kubernetes secrets
3. Store credentials in Jenkins credential store

### Setting up Secrets
1. Create Kubernetes secrets:
```bash
# Create namespace
kubectl create namespace last9-otel-demo

# Create OpenTelemetry secret
kubectl create secret generic otel-secret \
  --from-literal=last9-auth-header="your-auth-header" \
  --namespace last9-otel-demo
```

2. Update environment variables in Jenkins:
   - Go to Jenkins > Credentials > System > Global credentials
   - Add Docker registry credentials
   - Add AWS credentials
   - Add Last9 OpenTelemetry auth header 