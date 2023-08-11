import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {MessageService} from "primeng/api";
import {LoginService} from "../../auth/login/login.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ResultResponse, UserProjectResponse} from "../../../app.response";
import {TranslateService} from "@ngx-translate/core";
import {HttpService} from "../../../services/http.service";

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
    private formBuilder: FormBuilder,
    private translateService: TranslateService,
    private httpService: HttpService
  ) {
    this.editProjectFormGroup = this.formBuilder.group({
      name: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.httpService.get<UserProjectResponse>(`/api/project/project?id=${this.id}`, response => {
          this.userProject = response;

          this.editProjectFormGroup.setValue({
            name: this.userProject.projectResponse.name
          });
        }, () => this.router.navigate(['']));
    }

  editProject() {
    if(this.editProjectFormGroup.get('name')?.value === this.userProject?.projectResponse.name) {
      this.messageService.add({
        key: 'tr',
        severity: 'warn',
        summary: this.translateService.instant('common.warning'),
        detail: this.translateService.instant('project.not_changed'),
      });

      this.router.navigate(['/']);
      return;
    }

    this.httpService.put<ResultResponse>(`/api/project/edit?id=${(this.id)}`, this.getEditProjectRequestParameter(),
        response => {
          if(response.success) {
            this.messageService.add({
              key: 'tr',
              severity: 'success',
              summary: this.translateService.instant('common.success'),
              detail: this.translateService.instant('project.edited'),
            });
            this.router.navigate(['/']);
          }
        });
  }

  getEditProjectRequestParameter() {
    return {
      name: this.editProjectFormGroup.get('name')?.value
    };
  }
}
