import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './component/common/header/header.component';
import { SignupComponent } from './component/auth/signup/signup.component';
import { LoginComponent } from './component/auth/login/login.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {SimpleNotificationsModule} from "angular2-notifications";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { AllProjectsComponent } from './component/projects/all-projects/all-projects.component';
import {LoginRequired} from "./LoginRequired";
import { PageNotFoundComponent } from './component/common/page-not-found/page-not-found.component';
import { CreateProjectComponent } from './component/projects/create-project/create-project.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    SignupComponent,
    LoginComponent,
    AllProjectsComponent,
    PageNotFoundComponent,
    CreateProjectComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    FontAwesomeModule,
    SimpleNotificationsModule.forRoot({
      position: ["top", "right"],
      timeOut: 5000,
      showProgressBar: true,
      clickToClose: true,
      pauseOnHover: false,
      lastOnBottom: true
    }),
  ],
  providers: [LoginRequired,],
  bootstrap: [AppComponent]
})
export class AppModule { }
