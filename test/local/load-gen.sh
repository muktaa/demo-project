#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to generate random user data
generate_user_data() {
    local name="User$(date +%s)"
    local email=$(echo "$name" | tr '[:upper:]' '[:lower:]')"@example.com"
    echo "{\"name\":\"$name\",\"email\":\"$email\"}"
}

# Function to generate random order data
generate_order_data() {
    local userId=$1
    local products=("Laptop" "Phone" "Tablet" "Headphones" "Monitor")
    local product=${products[$RANDOM % ${#products[@]}]}
    local quantity=$((RANDOM % 5 + 1))
    local price=$((RANDOM % 1000 + 100))
    echo "{\"userId\":$userId,\"productName\":\"$product\",\"quantity\":$quantity,\"price\":$price}"
}

# Function to register a user
register_user() {
    local userData=$(generate_user_data)
    echo -e "${YELLOW}Registering user: $userData${NC}"
    local response=$(curl -s -X POST -H "Content-Type: application/json" -d "$userData" http://localhost:8081/api/users/register)
    echo "$response" | jq .
    echo "$response" | jq -r '.id'
}

# Function to create an order
create_order() {
    local userId=$1
    local orderData=$(generate_order_data $userId)
    echo -e "${YELLOW}Creating order for user $userId: $orderData${NC}"
    curl -s -X POST -H "Content-Type: application/json" -d "$orderData" http://localhost:8082/api/orders | jq .
}

# Function to get user's orders
get_user_orders() {
    local userId=$1
    echo -e "${YELLOW}Getting orders for user $userId${NC}"
    curl -s http://localhost:8082/api/orders/user/$userId | jq .
}

# Function to get weather for user
get_user_weather() {
    local userId=$1
    echo -e "${YELLOW}Getting weather for user $userId${NC}"
    curl -s http://localhost:8081/api/users/$userId/weather | jq .
}

# Function to get news
get_news() {
    echo -e "${YELLOW}Getting latest news${NC}"
    curl -s http://localhost:8083/api/notifications/news | jq .
}

# Main load generation loop
echo -e "${GREEN}Starting load generation...${NC}"

while true; do
    # Register a new user
    userId=$(register_user)
    
    # Create some orders for the user
    for i in {1..3}; do
        create_order $userId
        sleep 1
    done
    
    # Get user's orders
    get_user_orders $userId
    
    # Get weather for user
    get_user_weather $userId
    
    # Get news
    get_news
    
    echo -e "${GREEN}Load generation cycle complete. Waiting 5 seconds...${NC}"
    sleep 5
done 