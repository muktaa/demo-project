#!/usr/bin/env python3

import requests
import random
import time
import logging
from datetime import datetime

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Service endpoints
BASE_URL = "http://k8s-last9ote-microser-810bcfc554-925734203.ap-south-1.elb.amazonaws.com"
USER_SERVICE = f"{BASE_URL}/user-service/users"
ORDER_SERVICE = f"{BASE_URL}/order-service/orders"

def create_user():
    """Create a new user with random data."""
    user_data = {
        "name": f"Test User {random.randint(1, 1000)}",
        "email": f"test{random.randint(1, 1000)}@example.com"
    }
    try:
        response = requests.post(USER_SERVICE, json=user_data)
        response.raise_for_status()
        logger.info(f"Created user: {user_data['name']}")
        return response.json().get('id')
    except requests.exceptions.RequestException as e:
        logger.error(f"Error creating user: {e}")
        return None

def create_order(user_id):
    """Create a new order for the given user."""
    order_data = {
        "userId": user_id,
        "productId": random.randint(1, 10),
        "quantity": random.randint(1, 5),
        "totalAmount": round(random.uniform(10.0, 1000.0), 2)
    }
    try:
        response = requests.post(ORDER_SERVICE, json=order_data)
        response.raise_for_status()
        logger.info(f"Created order for user {user_id}: {order_data}")
    except requests.exceptions.RequestException as e:
        logger.error(f"Error creating order: {e}")

def get_users():
    """Get all users."""
    try:
        response = requests.get(USER_SERVICE)
        response.raise_for_status()
        users = response.json()
        logger.info(f"Retrieved {len(users)} users")
    except requests.exceptions.RequestException as e:
        logger.error(f"Error getting users: {e}")

def get_orders():
    """Get all orders."""
    try:
        response = requests.get(ORDER_SERVICE)
        response.raise_for_status()
        orders = response.json()
        logger.info(f"Retrieved {len(orders)} orders")
    except requests.exceptions.RequestException as e:
        logger.error(f"Error getting orders: {e}")

def main():
    """Main function to generate load."""
    logger.info("Starting load generator...")
    
    while True:
        # Randomly choose an action
        action = random.choice(['create_user', 'create_order', 'get_users', 'get_orders'])
        
        if action == 'create_user':
            user_id = create_user()
            if user_id:
                # If user was created successfully, create an order for them
                create_order(user_id)
        elif action == 'create_order':
            # Create an order for a random user ID
            create_order(random.randint(1, 100))
        elif action == 'get_users':
            get_users()
        elif action == 'get_orders':
            get_orders()
        
        # Random delay between 5 and 30 seconds
        delay = random.uniform(5, 30)
        logger.info(f"Waiting {delay:.2f} seconds before next request...")
        time.sleep(delay)

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        logger.info("Load generator stopped by user")
    except Exception as e:
        logger.error(f"Unexpected error: {e}") 