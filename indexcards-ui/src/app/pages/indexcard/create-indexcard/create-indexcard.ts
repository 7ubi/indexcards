import { Component, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
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
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { MathjaxDirective } from '../../../directives/mathjax.directive';
import { MarkdownMathPipe } from '../../../pipes/markdown-math.pipe';

@Component({
  selector: 'app-create-indexcard',
  imports: [
    TranslatePipe,
    FormsModule,
    RouterModule,
    ReactiveFormsModule,
    MatButton,
    MatFormField,
    MatInput,
    MatLabel,
    MathjaxDirective,
    MarkdownMathPipe,
  ],
  templateUrl: './create-indexcard.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './create-indexcard.css',
})
export class CreateIndexcard implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private formBuilder = inject(FormBuilder);
  private httpService = inject(HttpService);
  private snackbarService = inject(SnackbarService);

  id: string | null = '';

  createIndexCardFormGroup: FormGroup;

  previewMode = false;

  constructor() {
    this.createIndexCardFormGroup = this.formBuilder.group({
      question: ['', Validators.required],
      answer: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
  }

  togglePreview(): void {
    this.previewMode = !this.previewMode;
  }

  createIndexcard(): void {
    if (!this.createIndexCardFormGroup.valid) {
      this.snackbarService.showErrorMessage('indexcard.required');
      return;
    }

    this.httpService.post<undefined>('/api/indexCard', this.createRequest(), () => {
      this.snackbarService.showSuccessMessage('indexcard.created');
      this.router.navigate(['/project', this.id]).then();
    });
  }

  createRequest() {
    return {
      projectId: this.id,
      question: this.createIndexCardFormGroup.get('question')?.value,
      answer: this.createIndexCardFormGroup.get('answer')?.value,
    };
  }
}
