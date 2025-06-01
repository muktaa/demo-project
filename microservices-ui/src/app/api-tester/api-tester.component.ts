import { Component } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-api-tester',
  templateUrl: './api-tester.component.html',
  styleUrls: ['./api-tester.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule]
})
export class ApiTesterComponent {
  // Order creation
  orderUserId: number | null = null;
  productName = '';
  quantity: number | null = null;
  price: number | null = null;
  orderResponse: any;
  orderError = false;

  // Get orders
  getOrdersUserId: number | null = null;
  ordersResponse: any;
  ordersError = false;

  // Weather
  weatherUserId: number | null = null;
  weatherResponse: any;
  weatherError = false;

  // News
  newsResponse: any;
  newsError = false;

  constructor(private http: HttpClient) {}

  createOrder() {
    const payload = {
      userId: this.orderUserId,
      productName: this.productName,
      quantity: this.quantity,
      price: this.price
    };
    console.log('Creating order:', payload);
    
    this.http.post(environment.orderServiceUrl, payload)
      .subscribe({
        next: (res) => {
          console.log('Order created successfully:', res);
          this.orderResponse = res;
          this.orderError = false;
        },
        error: (err: HttpErrorResponse) => {
          console.error('Error creating order:', err);
          this.orderResponse = {
            status: err.status,
            statusText: err.statusText,
            message: err.message,
            error: err.error
          };
          this.orderError = true;
        }
      });
  }

  getOrdersByUser() {
    if (!this.getOrdersUserId) return;
    console.log('Getting orders for user:', this.getOrdersUserId);
    
    this.http.get(`${environment.orderServiceUrl}/user/${this.getOrdersUserId}`)
      .subscribe({
        next: (res) => {
          console.log('Orders retrieved successfully:', res);
          this.ordersResponse = res;
          this.ordersError = false;
        },
        error: (err: HttpErrorResponse) => {
          console.error('Error getting orders:', err);
          this.ordersResponse = {
            status: err.status,
            statusText: err.statusText,
            message: err.message,
            error: err.error
          };
          this.ordersError = true;
        }
      });
  }

  getWeather() {
    if (!this.weatherUserId) return;
    console.log('Getting weather for user:', this.weatherUserId);
    
    this.http.get(`${environment.userServiceUrl}/${this.weatherUserId}/weather`)
      .subscribe({
        next: (res) => {
          console.log('Weather retrieved successfully:', res);
          this.weatherResponse = res;
          this.weatherError = false;
        },
        error: (err: HttpErrorResponse) => {
          console.error('Error getting weather:', err);
          this.weatherResponse = {
            status: err.status,
            statusText: err.statusText,
            message: err.message,
            error: err.error
          };
          this.weatherError = true;
        }
      });
  }

  getNews() {
    console.log('Getting latest news');
    
    this.http.get(`${environment.notificationServiceUrl}/news`)
      .subscribe({
        next: (res) => {
          console.log('News retrieved successfully:', res);
          this.newsResponse = res;
          this.newsError = false;
        },
        error: (err: HttpErrorResponse) => {
          console.error('Error getting news:', err);
          this.newsResponse = {
            status: err.status,
            statusText: err.statusText,
            message: err.message,
            error: err.error
          };
          this.newsError = true;
        }
      });
  }
}
