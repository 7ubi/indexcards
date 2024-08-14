import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";
import {HttpService} from "../../../services/http.service";

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.css']
})
export class CreateProjectComponent {

  createProjectFormGroup: FormGroup

  constructor(
    private messageService: MessageService,
    private router: Router,
    private formBuilder: FormBuilder,
    private translateService: TranslateService,
    private httpService: HttpService
  ) {
    this.createProjectFormGroup = this.formBuilder.group({
      name: ['', Validators.required]
    });
  }

  createProject(): void {
    if (!this.createProjectFormGroup.valid) {
      this.throwInvalidForm();
      return;
    }

    this.httpService.post<undefined>(
      '/api/project/create',
      this.getCreateProjectRequestParameter(),
      _ => {
        this.messageService.add({
          key: 'tr',
          severity: 'success',
          summary: this.translateService.instant('common.success'),
          detail: this.translateService.instant('project.created'),
        });
        this.router.navigate(['/']).then();
      });
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
