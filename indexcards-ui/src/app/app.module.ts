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
import {LoginRequired} from "./login-required";
import { PageNotFoundComponent } from './component/common/page-not-found/page-not-found.component';
import { CreateProjectComponent } from './component/projects/create-project/create-project.component';
import { ProjectComponent } from './component/projects/project/project.component';
import { CreateIndexcardComponent } from './component/indexcard/create-indexcard/create-indexcard.component';
import { IndexcardQuizComponent } from './component/indexcard/indexcard-quiz/indexcard-quiz.component';
import {PasswordModule} from "primeng/password";

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    SignupComponent,
    LoginComponent,
    AllProjectsComponent,
    PageNotFoundComponent,
    CreateProjectComponent,
    ProjectComponent,
    CreateIndexcardComponent,
    IndexcardQuizComponent
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
    PasswordModule,
  ],
  providers: [LoginRequired,],
  bootstrap: [AppComponent]
})
export class AppModule { }
