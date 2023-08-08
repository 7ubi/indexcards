import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ResultResponse} from "../../../app.response";
import {LoginService} from "../../auth/login/login.service";
import {Router} from "@angular/router";
import {MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";

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
    private formBuilder: FormBuilder,
    private translateService: TranslateService
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
              summary: this.translateService.instant('common.success'),
              detail: this.translateService.instant('project.created'),
            });
            this.router.navigate(['/']);
          }
        }, err => {
          const response: ResultResponse = err.error;

          response.errorMessages.forEach(error => {
            this.messageService.add({
              key: 'tr',
              severity: 'error',
              summary: this.translateService.instant('common.error'),
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
      summary: this.translateService.instant('common.error'),
      detail: this.translateService.instant('project.no_name'),
    });
  }
}
