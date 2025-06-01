import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { UserRegisterComponent } from './user-register/user-register.component';
import { ApiTesterComponent } from './api-tester/api-tester.component';

@NgModule({
  declarations: [
    AppComponent,
    UserRegisterComponent,
    ApiTesterComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { } 