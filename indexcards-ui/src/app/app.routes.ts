import { Routes } from '@angular/router';
import {Login} from './pages/auth/login/login';
import {Signup} from './pages/auth/signup/signup';
import {AllProjects} from './pages/project/all-projects/all-projects';

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
    component: AllProjects
  }
];
