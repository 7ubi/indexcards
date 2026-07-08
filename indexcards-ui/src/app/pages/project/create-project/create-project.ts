import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import HttpService from '../../../service/http/http.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { TranslatePipe } from '@ngx-translate/core';
import { MatButton } from '@angular/material/button';
import { MatFormField, MatInput, MatLabel, MatSuffix } from '@angular/material/input';
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerToggle,
} from '@angular/material/datepicker';
import { toIsoDateString } from '../../../util/date.util';

@Component({
  selector: 'app-create-project',
  imports: [
    TranslatePipe,
    FormsModule,
    RouterModule,
    ReactiveFormsModule,
    MatButton,
    MatFormField,
    MatInput,
    MatLabel,
    MatSuffix,
    MatDatepicker,
    MatDatepickerInput,
    MatDatepickerToggle,
  ],
  templateUrl: './create-project.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './create-project.css',
})
export class CreateProject {
  private snackbarService = inject(SnackbarService);
  private router = inject(Router);
  private formBuilder = inject(FormBuilder);
  private httpService = inject(HttpService);

  createProjectFormGroup: FormGroup;

  constructor() {
    this.createProjectFormGroup = this.formBuilder.group({
      name: ['', Validators.required],
      examDate: [null],
    });
  }

  createProject(): void {
    if (!this.createProjectFormGroup.valid) {
      this.snackbarService.showErrorMessage('project.no_name');
      return;
    }

    this.httpService.post<undefined>(
      '/api/project',
      this.getCreateProjectRequestParameter(),
      () => {
        this.snackbarService.showSuccessMessage('project.created');
        this.router.navigate(['/']).then();
      },
    );
  }

  getCreateProjectRequestParameter() {
    return {
      name: this.createProjectFormGroup.get('name')?.value,
      examDate: toIsoDateString(this.createProjectFormGroup.get('examDate')?.value ?? null),
    };
  }
}
