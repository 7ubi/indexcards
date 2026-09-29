import { Routes } from '@angular/router';
import { Login } from './pages/auth/login/login';
import { Signup } from './pages/auth/signup/signup';
import { AllProjects } from './pages/project/all-projects/all-projects';
import { LoginRequired } from './service/login/login-required';
import { AdminRequired } from './service/login/admin-required';
import { CreateProject } from './pages/project/create-project/create-project';
import { Project } from './pages/project/project/project';
import { Quiz } from './pages/indexcard/quiz/quiz';
import { EditProject } from './pages/project/edit-project/edit-project';
import { CreateIndexcard } from './pages/indexcard/create-indexcard/create-indexcard';
import { EditIndexcard } from './pages/indexcard/edit-indexcard/edit-indexcard';
import { QuizStat } from './pages/indexcard/quiz-stat/quiz-stat';
import { Practice } from './pages/indexcard/practice/practice';
import { PageNotFound } from './pages/page-not-found/page-not-found';
import { Privacy } from './pages/privacy/privacy';
import { DueQuiz } from './pages/indexcard/due-quiz/due-quiz';

export const routes: Routes = [
  {
    path: 'login',
    component: Login,
  },
  {
    path: 'signup',
    component: Signup,
  },
  {
    path: 'privacy',
    component: Privacy,
  },
  {
    path: '',
    component: AllProjects,
    canActivate: [LoginRequired],
  },
  {
    path: 'due',
    component: DueQuiz,
    canActivate: [LoginRequired],
  },
  {
    path: 'account',
    // Lazy loaded: rarely visited, and keeps its Material components out of the initial bundle.
    loadComponent: () => import('./pages/account/account/account').then((m) => m.Account),
    canActivate: [LoginRequired],
  },
  {
    path: 'admin',
    // Lazy loaded: only admins ever open it.
    loadComponent: () => import('./pages/admin/analytics/analytics').then((m) => m.Analytics),
    canActivate: [LoginRequired, AdminRequired],
  },
  {
    path: 'account/delete',
    redirectTo: 'account',
  },
  {
    path: 'project',
    canActivate: [LoginRequired],
    children: [
      {
        path: 'create',
        component: CreateProject,
      },
      {
        path: ':id',
        component: Project,
      },
      {
        path: ':id/edit',
        component: EditProject,
      },
      {
        path: ':id/createIndexCard',
        component: CreateIndexcard,
      },
      {
        path: ':id/generate',
        // Lazy loaded: optional feature, keeps its Material components out of the initial bundle.
        loadComponent: () =>
          import('./pages/indexcard/generate-indexcards/generate-indexcards').then(
            (m) => m.GenerateIndexcards,
          ),
      },
      {
        path: ':id/editIndexCard/:indexCardId',
        component: EditIndexcard,
      },
      {
        path: ':id/quiz',
        component: Quiz,
      },
      {
        path: ':id/quiz/stat',
        component: QuizStat,
      },
      {
        path: ':id/practice',
        component: Practice,
      },
    ],
  },
  {
    path: '**',
    component: PageNotFound,
  },
];
