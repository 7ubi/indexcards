import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './component/auth/login/login.component';
import {SignupComponent} from './component/auth/signup/signup.component';
import {LoginRequired} from './login-required';
import {AllProjectsComponent} from './component/projects/all-projects/all-projects.component';
import {PageNotFoundComponent} from "./component/common/page-not-found/page-not-found.component";
import {CreateProjectComponent} from "./component/projects/create-project/create-project.component";
import {ProjectComponent} from "./component/projects/project/project.component";
import {CreateIndexcardComponent} from "./component/indexcard/create-indexcard/create-indexcard.component";
import {IndexcardQuizComponent} from "./component/indexcard/indexcard-quiz/indexcard-quiz.component";
import {IndexcardQuizStatComponent} from "./component/indexcard/indexcard-quiz-stat/indexcard-quiz-stat.component";
import {EditProjectComponent} from "./component/projects/edit-project/edit-project.component";
import {EditIndexcardComponent} from "./component/indexcard/edit-indexcard/edit-indexcard.component";

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
    canActivate: [LoginRequired]
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
    path: 'project/:id/edit',
    component: EditProjectComponent,
    canActivate: [LoginRequired]
  },
  {
    path: 'project/:id/createIndexCard',
    component: CreateIndexcardComponent,
    canActivate: [LoginRequired]
  },
  {
    path: 'project/:id/editIndexCard/:indexCardId',
    component: EditIndexcardComponent,
    canActivate: [LoginRequired]
  },
  {
    path: 'project/:id/quiz',
    component: IndexcardQuizComponent,
    canActivate: [LoginRequired]
  },
  {
    path: 'project/:id/quiz/stat',
    component: IndexcardQuizStatComponent,
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
export class AppRoutingModule {
}
