#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting setup...${NC}"

# Apply Kubernetes configurations
echo -e "${YELLOW}Applying Kubernetes configurations...${NC}"
kubectl apply -f k8s/local
kubectl apply -f microservices-ui/k8s

# Wait for pods to be ready
echo -e "${YELLOW}Waiting for pods to be ready...${NC}"
kubectl wait --for=condition=available --timeout=300s deployment/user-service -n last9-otel-demo
kubectl wait --for=condition=available --timeout=300s deployment/order-service -n last9-otel-demo
kubectl wait --for=condition=available --timeout=300s deployment/notification-service -n last9-otel-demo
kubectl wait --for=condition=available --timeout=300s deployment/microservices-ui -n last9-otel-demo

# Start port forwarding in background
echo -e "${YELLOW}Setting up port forwarding...${NC}"
kubectl port-forward -n last9-otel-demo svc/user-service 8081:8081 &
kubectl port-forward -n last9-otel-demo svc/order-service 8082:8082 &
kubectl port-forward -n last9-otel-demo svc/notification-service 8083:8083 &
kubectl port-forward -n last9-otel-demo svc/microservices-ui 4200:80 &

# Store PIDs for cleanup
echo $! > .port-forward-pids

# Wait for port forwarding to be ready
echo -e "${YELLOW}Waiting for port forwarding to be ready...${NC}"
sleep 5

# Run load generation
# echo -e "${YELLOW}Starting load generation...${NC}"
# ./load-gen.sh

# Function to cleanup port forwarding
# cleanup() {
#     echo -e "${YELLOW}Cleaning up port forwarding...${NC}"
#     if [ -f .port-forward-pids ]; then
#         while read pid; do
#             kill $pid 2>/dev/null
#         done < .port-forward-pids
#         rm .port-forward-pids
#     fi
# }

# Register cleanup function
trap cleanup EXIT

# Keep script running
echo -e "${GREEN}Setup complete!"
 