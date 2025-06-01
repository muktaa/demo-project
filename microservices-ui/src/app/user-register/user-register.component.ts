import { Component } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-user-register',
  templateUrl: './user-register.component.html',
  styleUrls: ['./user-register.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule]
})
export class UserRegisterComponent {
  name = '';
  email = '';
  response: any;
  isError = false;

  constructor(private http: HttpClient) {}

  registerUser() {
    const payload = { name: this.name, email: this.email };
    console.log('Sending request to:', `${environment.userServiceUrl}/register`);
    console.log('With payload:', payload);
    
    this.http.post(`${environment.userServiceUrl}/register`, payload)
      .subscribe({
        next: (res) => {
          console.log('Success response:', res);
          this.response = res;
          this.isError = false;
        },
        error: (err: HttpErrorResponse) => {
          console.error('Error response:', err);
          this.response = {
            status: err.status,
            statusText: err.statusText,
            message: err.message,
            error: err.error
          };
          this.isError = true;
        }
      });
  }
}
