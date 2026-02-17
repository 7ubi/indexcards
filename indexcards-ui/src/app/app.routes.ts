import {Routes} from '@angular/router';
import {Login} from './pages/auth/login/login';
import {Signup} from './pages/auth/signup/signup';
import {AllProjects} from './pages/project/all-projects/all-projects';
import {LoginRequired} from './service/login/login-required';
import {CreateProject} from './pages/project/create-project/create-project';
import {Project} from './pages/project/project/project';
import {Quiz} from './pages/indexcard/quiz/quiz';

export const routes: Routes = [
  {
    path: 'login',
    component: Login
  },
  {
    path: 'signup',
    component: Signup
  },
  {
    path: '',
    component: AllProjects,
    canActivate: [LoginRequired]
  },
  {
    path: 'project',
    canActivate: [LoginRequired],
    children: [
      {
        path: 'create',
        component: CreateProject
      },
      {
        path: ':id',
        component: Project,
      },
      {
        path: ':id/quiz',
        component: Quiz
      }
    ]
  }
];
