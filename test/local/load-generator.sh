#!/bin/bash

# Configure logging
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1"
}

# Service endpoints (using port-forwarded localhost)
USER_SERVICE="http://localhost:8081/api/users"
ORDER_SERVICE="http://localhost:8082/api/orders"
NOTIFICATION_SERVICE="http://localhost:8083/api/notifications"

# Function to create a user
create_user() {
    local name="Test User $((RANDOM % 1000))"
    local email="test$((RANDOM % 1000))@example.com"
    local payload="{\"name\":\"$name\",\"email\":\"$email\"}"
    
    log "Creating user: $name"
    log "Request URL: $USER_SERVICE/register"
    log "Request Payload: $payload"
    
    response=$(curl -s -X POST -H "Content-Type: application/json" \
        -d "$payload" \
        "$USER_SERVICE/register")
    
    log "Response: $response"
    echo "$response" | grep -o '"id":[0-9]*' | cut -d':' -f2
}

# Function to create an order
create_order() {
    local user_id=$1
    local product_name="Product $((RANDOM % 10 + 1))"
    local quantity=$((RANDOM % 5 + 1))
    local price=$(printf "%.2f" $(echo "$RANDOM/100" | bc -l))
    local payload="{\"userId\":$user_id,\"productName\":\"$product_name\",\"quantity\":$quantity,\"price\":$price}"
    
    log "Creating order for user $user_id"
    log "Request URL: $ORDER_SERVICE"
    log "Request Payload: $payload"
    
    response=$(curl -s -X POST -H "Content-Type: application/json" \
        -d "$payload" \
        "$ORDER_SERVICE")
    
    log "Response: $response"
}

# Function to get orders for a user
get_orders_by_user() {
    local user_id=$1
    log "Getting orders for user $user_id"
    log "Request URL: $ORDER_SERVICE/user/$user_id"
    
    response=$(curl -s -X GET "$ORDER_SERVICE/user/$user_id")
    log "Response: $response"
}

# Function to get weather for a user
get_user_weather() {
    local user_id=$1
    log "Getting weather for user $user_id"
    log "Request URL: $USER_SERVICE/$user_id/weather"
    
    response=$(curl -s -X GET "$USER_SERVICE/$user_id/weather")
    log "Response: $response"
}

# Function to get latest news
get_latest_news() {
    log "Getting latest news"
    log "Request URL: $NOTIFICATION_SERVICE/news"
    
    response=$(curl -s -X GET "$NOTIFICATION_SERVICE/news")
    log "Response: $response"
}

# Main loop
log "Starting load generator..."

while true; do
    # Randomly choose an action
    case $((RANDOM % 5)) in
        0)
            user_id=$(create_user)
            if [ ! -z "$user_id" ]; then
                create_order "$user_id"
                get_orders_by_user "$user_id"
                get_user_weather "$user_id"
            fi
            ;;
        1)
            # Use a random user id for order creation and retrieval
            user_id=$((RANDOM % 10 + 1))
            create_order "$user_id"
            get_orders_by_user "$user_id"
            get_user_weather "$user_id"
            ;;
        2)
            # Just get orders for a random user
            user_id=$((RANDOM % 10 + 1))
            get_orders_by_user "$user_id"
            get_user_weather "$user_id"
            ;;
        3)
            # Get weather for a random user
            user_id=$((RANDOM % 10 + 1))
            get_user_weather "$user_id"
            ;;
        4)
            # Get latest news
            get_latest_news
            ;;
    esac
    
    # Random delay between 5 and 30 seconds
    delay=$((RANDOM % 26 + 5))
    log "Waiting $delay seconds before next request..."
    sleep $delay
done 