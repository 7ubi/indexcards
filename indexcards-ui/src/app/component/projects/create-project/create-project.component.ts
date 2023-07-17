import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ResultResponse} from "../../../app.response";
import {LoginService} from "../../auth/login/login.service";
import {Router} from "@angular/router";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.css']
})
export class CreateProjectComponent {

  createProjectFormGroup: FormGroup

  constructor(
    private http: HttpClient,
    private messageService: MessageService,
    private loginService: LoginService,
    private router: Router,
    private formBuilder: FormBuilder
  ){
    this.createProjectFormGroup = this.formBuilder.group({
      name: ['', Validators.required]
    });
  }

  createProject(): void {
    if(!this.createProjectFormGroup.valid) {
      this.throwInvalidForm();
      return;
    }

    this.http.post<ResultResponse>('/api/project/create', this.getCreateProjectRequestParameter(),
      { headers: this.loginService.getHeaderWithBearer() })
      .subscribe(
        response => {
          if(response.success) {
            this.messageService.add({
              key: 'tr',
              severity: 'success',
              summary: 'SUCCESS',
              detail: 'Project was created!',
            });
            this.router.navigate(['/']);
          }
        }, err => {
          const response: ResultResponse = err.error;

          response.errorMessages.forEach(error => {
            this.messageService.add({
              key: 'tr',
              severity: 'error',
              summary: 'ERROR',
              detail: error.message,
            });
          });
        }
      );
  }

  getCreateProjectRequestParameter() {
    return {
      name: this.createProjectFormGroup.get('name')?.value
    };
  }

  throwInvalidForm() {
    this.messageService.add({
      key: 'tr',
      severity: 'error',
      summary: 'ERROR',
      detail: 'Enter a project name to create a project!',
    });
  }
}
