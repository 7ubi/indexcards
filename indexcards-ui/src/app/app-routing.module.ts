import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './component/auth/login/login.component';
import { SignupComponent } from './component/auth/signup/signup.component';
import { LoginRequired } from './LoginRequired';
import { AllProjectsComponent } from './component/projects/all-projects/all-projects.component';
import {PageNotFoundComponent} from "./component/common/page-not-found/page-not-found.component";
import {CreateProjectComponent} from "./component/projects/create-project/create-project.component";
import {ProjectComponent} from "./component/projects/project/project.component";

const routes: Routes = [
  {
    path: 'signup',
    component: SignupComponent,
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: '',
    component: AllProjectsComponent,
    canActivate:[LoginRequired]
  },
  {
    path: 'createProject',
    component: CreateProjectComponent,
    canActivate: [LoginRequired]
  },
  {
    path: 'project/:id',
    component: ProjectComponent,
    canActivate: [LoginRequired]
  },
  {
    path: '**',
    pathMatch: 'full',
    component: PageNotFoundComponent
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
