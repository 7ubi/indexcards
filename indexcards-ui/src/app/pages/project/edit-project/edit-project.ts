import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import HttpService from '../../../service/http/http.service';
import {SnackbarService} from '../../../service/snackbar/snackbar.service';
import {TranslatePipe} from '@ngx-translate/core';
import {MatButton} from '@angular/material/button';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {ProjectResponse} from '../../../app.responses';
import {LoadingSpinner} from '../../../component/loading-spinner/loading-spinner';

@Component({
  selector: 'app-edit-project',
  imports: [
    TranslatePipe,
    FormsModule,
    RouterModule,
    ReactiveFormsModule,
    MatButton,
    MatFormField,
    MatInput,
    MatLabel,
    LoadingSpinner
  ],
  templateUrl: './edit-project.html',
  styleUrl: './edit-project.css',
})
export class EditProject implements OnInit {

  editProjectFormGroup: FormGroup;

  userProject?: ProjectResponse;
  id: string | null = '';
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private httpService: HttpService,
    private snackbarService: SnackbarService,
    private cdr: ChangeDetectorRef
  ) {
    this.editProjectFormGroup = this.formBuilder.group({
      name: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.httpService.get<ProjectResponse>(`/api/project/${this.id}`, response => {
      this.userProject = response;
      this.editProjectFormGroup.setValue({name: response.name});
      this.loading = false;
      this.cdr.detectChanges();
    }, () => this.router.navigate(['']));
  }

  editProject(): void {
    if (this.editProjectFormGroup.get('name')?.value === this.userProject?.name) {
      this.snackbarService.showWarnMessage('project.not_changed');
      this.router.navigate(['/']).then();
      return;
    }

    if (!this.editProjectFormGroup.valid) {
      this.snackbarService.showErrorMessage('project.no_name');
      return;
    }

    this.httpService.put<undefined>(`/api/project?id=${this.id}`, this.getEditProjectRequestParameter(),
      () => {
        this.snackbarService.showSuccessMessage('project.edited');
        this.router.navigate(['/']).then();
      });
  }

  getEditProjectRequestParameter() {
    return {
      name: this.editProjectFormGroup.get('name')?.value
    };
  }
}
