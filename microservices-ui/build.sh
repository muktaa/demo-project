#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Building microservices-ui...${NC}"

# Install dependencies
echo -e "${YELLOW}Installing dependencies...${NC}"
npm install

# Build Angular application
echo -e "${YELLOW}Building Angular application...${NC}"
npm run build

# Build Docker image
echo -e "${YELLOW}Building Docker image...${NC}"
docker build -t microservices-ui:latest .

# build on mac arm64 and run on ec2 x86_64
# Install Docker BuildX if not already installed
docker buildx create --use
docker buildx inspect --bootstrap

# Build for multiple architectures
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t last9mukta/microservices-ui:latest \
  --push .
  
echo -e "${GREEN}Build complete!${NC}"
echo -e "To test the image locally, run: ${YELLOW}docker run -p 80:80 microservices-ui:latest${NC}" 