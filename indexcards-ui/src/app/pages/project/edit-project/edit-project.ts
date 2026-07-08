import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject,
  ChangeDetectionStrategy,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
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
import { ProjectResponse } from '../../../app.responses';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';
import { parseIsoDateString, toIsoDateString } from '../../../util/date.util';

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
    MatSuffix,
    MatDatepicker,
    MatDatepickerInput,
    MatDatepickerToggle,
    LoadingSpinner,
  ],
  templateUrl: './edit-project.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './edit-project.css',
})
export class EditProject implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private formBuilder = inject(FormBuilder);
  private httpService = inject(HttpService);
  private snackbarService = inject(SnackbarService);
  private cdr = inject(ChangeDetectorRef);

  editProjectFormGroup: FormGroup;

  userProject?: ProjectResponse;
  id: string | null = '';
  loading = true;

  constructor() {
    this.editProjectFormGroup = this.formBuilder.group({
      name: ['', Validators.required],
      examDate: [null],
    });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.httpService.get<ProjectResponse>(
      `/api/project/${this.id}`,
      (response) => {
        this.userProject = response;
        this.editProjectFormGroup.setValue({
          name: response.name,
          examDate: parseIsoDateString(response.examDate),
        });
        this.loading = false;
        this.cdr.detectChanges();
      },
      () => this.router.navigate(['']),
    );
  }

  editProject(): void {
    const nameUnchanged = this.editProjectFormGroup.get('name')?.value === this.userProject?.name;
    const examDateUnchanged =
      toIsoDateString(this.editProjectFormGroup.get('examDate')?.value ?? null) ===
      (this.userProject?.examDate ?? null);

    if (nameUnchanged && examDateUnchanged) {
      this.snackbarService.showWarnMessage('project.not_changed');
      this.router.navigate(['/']).then();
      return;
    }

    if (!this.editProjectFormGroup.valid) {
      this.snackbarService.showErrorMessage('project.no_name');
      return;
    }

    this.httpService.put<undefined>(
      `/api/project?id=${this.id}`,
      this.getEditProjectRequestParameter(),
      () => {
        this.snackbarService.showSuccessMessage('project.edited');
        this.router.navigate(['/']).then();
      },
    );
  }

  getEditProjectRequestParameter() {
    return {
      name: this.editProjectFormGroup.get('name')?.value,
      examDate: toIsoDateString(this.editProjectFormGroup.get('examDate')?.value ?? null),
    };
  }
}
