import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UserRegisterComponent } from './user-register/user-register.component';
import { ApiTesterComponent } from './api-tester/api-tester.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, UserRegisterComponent, ApiTesterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  standalone: true
})
export class AppComponent {
  title = 'microservices-ui';
}
