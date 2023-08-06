import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {MessageService} from "primeng/api";
import {LoginService} from "../../auth/login/login.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ResultResponse, UserProjectResponse} from "../../../app.response";

@Component({
  selector: 'app-edit-project',
  templateUrl: './edit-project.component.html',
  styleUrls: ['./edit-project.component.css']
})
export class EditProjectComponent implements OnInit {

  editProjectFormGroup: FormGroup;

  userProject: UserProjectResponse | undefined;
  id: string | null = '';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private messageService: MessageService,
    private loginService: LoginService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.editProjectFormGroup = this.formBuilder.group({
      name: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.http.get<UserProjectResponse>('/api/project/project?id=' + this.id,
      { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => {
          this.userProject = response;

          this.editProjectFormGroup.setValue({
            name: this.userProject.projectResponse.name
          });
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
          this.router.navigate(['']);
        }
      );
    }

  editProject() {
    this.http.put<ResultResponse>(`/api/project/edit?id=${(this.id)}`, this.getEditProjectRequestParameter(),
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

  getEditProjectRequestParameter() {
    return {
      name: this.editProjectFormGroup.get('name')?.value
    };
  }
}
