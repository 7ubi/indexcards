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
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { IndexCardResponse } from '../../../app.responses';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';
import { MathjaxDirective } from '../../../directives/mathjax.directive';
import { PasteImageDirective } from '../../../directives/paste-image.directive';
import { MarkdownMathPipe } from '../../../pipes/markdown-math.pipe';

@Component({
  selector: 'app-edit-indexcard',
  imports: [
    TranslatePipe,
    FormsModule,
    RouterModule,
    ReactiveFormsModule,
    MatButton,
    MatFormField,
    MatInput,
    MatLabel,
    LoadingSpinner,
    MathjaxDirective,
    PasteImageDirective,
    MarkdownMathPipe,
  ],
  templateUrl: './edit-indexcard.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './edit-indexcard.css',
})
export class EditIndexcard implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private formBuilder = inject(FormBuilder);
  private httpService = inject(HttpService);
  private snackbarService = inject(SnackbarService);
  private cdr = inject(ChangeDetectorRef);

  indexCardResponse?: IndexCardResponse;

  editIndexCardFormGroup: FormGroup;

  id: string | null = '';
  indexCardId: string | null = '';
  loading = true;

  previewMode = false;

  constructor() {
    this.editIndexCardFormGroup = this.formBuilder.group({
      question: ['', Validators.required],
      answer: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    this.indexCardId = this.route.snapshot.paramMap.get('indexCardId');

    this.httpService.get<IndexCardResponse>(
      `/api/indexCard?id=${this.indexCardId}`,
      (response) => {
        this.indexCardResponse = response;
        this.editIndexCardFormGroup.setValue({
          question: response.question,
          answer: response.answer,
        });
        this.loading = false;
        this.cdr.detectChanges();
      },
      () => this.router.navigate(['/project', this.id]),
    );
  }

  togglePreview(): void {
    this.previewMode = !this.previewMode;
  }

  editIndexCard(): void {
    if (!this.editIndexCardFormGroup.valid) {
      this.snackbarService.showErrorMessage('indexcard.required');
      return;
    }

    this.httpService.put<undefined>(
      `/api/indexCard?id=${this.indexCardId}`,
      this.getRequest(),
      () => {
        this.snackbarService.showSuccessMessage('indexcard.edited');
        this.router.navigate(['/project', this.id]).then();
      },
    );
  }

  private getRequest() {
    return {
      question: this.editIndexCardFormGroup.get('question')?.value,
      answer: this.editIndexCardFormGroup.get('answer')?.value,
    };
  }
}
