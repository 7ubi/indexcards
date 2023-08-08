import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient, HttpClientModule} from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './component/common/header/header.component';
import { SignupComponent } from './component/auth/signup/signup.component';
import { LoginComponent } from './component/auth/login/login.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { AllProjectsComponent } from './component/projects/all-projects/all-projects.component';
import {LoginRequired} from "./login-required";
import { PageNotFoundComponent } from './component/common/page-not-found/page-not-found.component';
import { CreateProjectComponent } from './component/projects/create-project/create-project.component';
import { ProjectComponent } from './component/projects/project/project.component';
import { CreateIndexcardComponent } from './component/indexcard/create-indexcard/create-indexcard.component';
import { IndexcardQuizComponent } from './component/indexcard/indexcard-quiz/indexcard-quiz.component';
import {PasswordModule} from "primeng/password";
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {ToastModule} from "primeng/toast";
import {ConfirmationService, MessageService} from "primeng/api";
import {TooltipModule} from "primeng/tooltip";
import {InputTextareaModule} from "primeng/inputtextarea";
import { FooterComponent } from './component/common/footer/footer.component';
import { AssessmentButtonComponent } from './component/indexcard/assessment-button/assessment-button.component';
import { IndexcardQuizStatComponent } from './component/indexcard/indexcard-quiz-stat/indexcard-quiz-stat.component';
import {ChartModule} from "primeng/chart";
import {SidebarModule} from "primeng/sidebar";
import {MenubarModule} from "primeng/menubar";
import {ConfirmPopupModule} from "primeng/confirmpopup";
import { EditProjectComponent } from './component/projects/edit-project/edit-project.component';
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";

// AoT requires an exported function for factories
export function HttpLoaderFactory(httpClient: HttpClient) {
  return new TranslateHttpLoader(httpClient);
}

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
    IndexcardQuizComponent,
    FooterComponent,
    AssessmentButtonComponent,
    IndexcardQuizStatComponent,
    EditProjectComponent
  ],
  imports: [
    FormsModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    PasswordModule,
    ButtonModule,
    InputTextModule,
    ReactiveFormsModule,
    ToastModule,
    TooltipModule,
    InputTextareaModule,
    ChartModule,
    SidebarModule,
    MenubarModule,
    ConfirmPopupModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    })
  ],
  providers: [LoginRequired, MessageService, ConfirmationService],
  bootstrap: [AppComponent],
  exports: [FormsModule, ReactiveFormsModule, TranslateModule]
})
export class AppModule { }
