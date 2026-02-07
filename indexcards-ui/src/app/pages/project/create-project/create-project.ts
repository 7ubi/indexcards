import {Component} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterModule} from '@angular/router';
import HttpService from '../../../service/http/http.service';
import {SnackbarService} from '../../../service/snackbar/snackbar.service';
import {TranslatePipe} from '@ngx-translate/core';
import {MatButton} from '@angular/material/button';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';

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
    MatLabel
  ],
  templateUrl: './create-project.html',
  styleUrl: './create-project.css',
})
export class CreateProject {

  createProjectFormGroup: FormGroup;

  constructor(
    private snackbarService: SnackbarService,
    private router: Router,
    private formBuilder: FormBuilder,
    private httpService: HttpService
  ) {
    this.createProjectFormGroup = this.formBuilder.group({
      name: ['', Validators.required]
    });
  }

  createProject(): void {
    if (!this.createProjectFormGroup.valid) {
      this.snackbarService.showErrorMessage('project.no_name');
      return;
    }

    this.httpService.post<undefined>(
      '/api/project/create',
      this.getCreateProjectRequestParameter(),
      _ => {
        this.snackbarService.showSuccessMessage('project.created');
        this.router.navigate(['/']).then();
      });
  }

  getCreateProjectRequestParameter() {
    return {
      name: this.createProjectFormGroup.get('name')?.value
    };
  }
}
