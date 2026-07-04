import {Routes} from '@angular/router';
import {Login} from './pages/auth/login/login';
import {Signup} from './pages/auth/signup/signup';
import {AllProjects} from './pages/project/all-projects/all-projects';
import {LoginRequired} from './service/login/login-required';
import {CreateProject} from './pages/project/create-project/create-project';
import {Project} from './pages/project/project/project';
import {Quiz} from './pages/indexcard/quiz/quiz';
import {EditProject} from './pages/project/edit-project/edit-project';
import {CreateIndexcard} from './pages/indexcard/create-indexcard/create-indexcard';
import {EditIndexcard} from './pages/indexcard/edit-indexcard/edit-indexcard';
import {QuizStat} from './pages/indexcard/quiz-stat/quiz-stat';
import {PageNotFound} from './pages/page-not-found/page-not-found';

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
        path: ':id/edit',
        component: EditProject
      },
      {
        path: ':id/createIndexCard',
        component: CreateIndexcard
      },
      {
        path: ':id/editIndexCard/:indexCardId',
        component: EditIndexcard
      },
      {
        path: ':id/quiz',
        component: Quiz
      },
      {
        path: ':id/quiz/stat',
        component: QuizStat
      }
    ]
  },
  {
    path: '**',
    component: PageNotFound
  }
];
