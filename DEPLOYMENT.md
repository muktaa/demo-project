# Deployment and Testing Guide

## Quick Start - Local Development

### 1. Clone the repository
```bash
git clone https://github.com/muktaa/demo-project.git
cd demo-project
```

### 2. Start with Docker Compose
```bash
# Build and start all services (this will take a few minutes)
docker-compose up --build

# Or run in background
docker-compose up -d --build
```

### 3. Test the APIs
```bash
# Create a user
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "city": "London"
  }'

# Get user weather (calls external Open-Meteo API)
curl http://localhost:8081/api/users/1/weather

# Create an order (triggers distributed tracing)
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productName": "Laptop",
    "quantity": 1,
    "price": 999.99
  }'

# Get latest news (calls external Hacker News API)
curl http://localhost:8083/api/notifications/news

# Check Zipkin for distributed traces
open http://localhost:9411
```

## EKS Deployment

### 1. Prerequisites
```bash
# Install AWS CLI
aws configure

# Install kubectl
# Install eksctl (optional)

# Create EKS cluster (if not exists)
eksctl create cluster --name demo-cluster --region us-west-2 --nodes 3
```

### 2. Deploy using kubectl
```bash
# Update your Docker registry in k8s files
sed -i 's|your-registry|docker.io/yourusername|g' k8s/*.yaml

# Apply all Kubernetes manifests
kubectl apply -f k8s/

# Check deployment status
kubectl get pods -n microservices-demo
kubectl get services -n microservices-demo
```

### 3. Deploy using Jenkins Pod
```bash
# Create the deployment pod
kubectl apply -f jenkins/deploy-pod.yaml

# Execute deployment script inside the pod
kubectl exec -it microservices-deployer -- /bin/sh

# Inside the pod, run:
git clone https://github.com/muktaa/demo-project.git
cd demo-project
chmod +x jenkins/deploy.sh
./jenkins/deploy.sh
```

## Distributed Tracing Verification

### 1. Generate some traffic
```bash
# Create multiple users and orders to generate traces
for i in {1..5}; do
  curl -X POST http://your-app-url/api/users/register \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"User$i\",\"email\":\"user$i@example.com\",\"city\":\"London\"}"
  
  curl -X POST http://your-app-url/api/orders \
    -H "Content-Type: application/json" \
    -d "{\"userId\":$i,\"productName\":\"Product$i\",\"quantity\":1,\"price\":99.99}"
done
```

### 2. View traces in Zipkin
- Open Zipkin UI (http://zipkin-service-url:9411)
- You should see traces spanning:
  - User Service → Open-Meteo Weather API
  - Order Service → User Service → Notification Service → Hacker News API

### 3. Expected trace flow
```
Client Request
    ↓
Order Service (span 1)
    ↓
User Service (span 2) ──→ Open-Meteo API (span 3)
    ↓
Notification Service (span 4) ──→ Hacker News API (span 5)
```

## Monitoring and Health Checks

### 1. Health endpoints
```bash
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Order Service  
curl http://localhost:8083/actuator/health  # Notification Service
```

### 2. Service logs
```bash
# Docker Compose logs
docker-compose logs -f user-service
docker-compose logs -f order-service
docker-compose logs -f notification-service

# Kubernetes logs
kubectl logs -f deployment/user-service -n microservices-demo
kubectl logs -f deployment/order-service -n microservices-demo
kubectl logs -f deployment/notification-service -n microservices-demo
```

## Troubleshooting

### 1. Services not starting
```bash
# Check container logs
docker-compose logs

# Check Kubernetes events
kubectl get events -n microservices-demo --sort-by=.metadata.creationTimestamp
```

### 2. External API calls failing
- Open-Meteo API: Check if https://api.open-meteo.com/v1/forecast is accessible
- Hacker News API: Check if https://hacker-news.firebaseio.com/v0/topstories.json is accessible

### 3. Database connection issues
```bash
# Check PostgreSQL logs
docker-compose logs postgres

# Test database connection
kubectl exec -it deployment/postgres -n microservices-demo -- psql -U postgres -d microservicesdb -c "SELECT 1;"
```

## Clean up

### Local environment
```bash
docker-compose down -v
docker system prune -a
```

### EKS environment
```bash
kubectl delete namespace microservices-demo
# or
eksctl delete cluster --name demo-cluster
``` 