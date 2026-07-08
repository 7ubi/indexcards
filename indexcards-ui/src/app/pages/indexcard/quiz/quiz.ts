import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject,
  ChangeDetectionStrategy,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoginService } from '../../../service/login/login.service';
import HttpService from '../../../service/http/http.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { IndexCardResponse } from '../../../app.responses';
import { MatButtonModule } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';
import { MathjaxDirective } from '../../../directives/mathjax.directive';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';
import { MarkdownMathPipe } from '../../../pipes/markdown-math.pipe';

@Component({
  selector: 'app-quiz',
  imports: [MatButtonModule, TranslatePipe, MathjaxDirective, LoadingSpinner, MarkdownMathPipe],
  templateUrl: './quiz.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './quiz.css',
})
export class Quiz implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private loginService = inject(LoginService);
  private httpService = inject(HttpService);
  private snackbarService = inject(SnackbarService);
  private cdr = inject(ChangeDetectorRef);

  indexCards?: IndexCardResponse[];

  index = 0;

  showAnswer = false;

  id: string | null = '';

  loading = true;

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.httpService.get<IndexCardResponse[]>(
      `/api/indexCard/quizIndexCards?id=${this.id}`,
      (response) => {
        this.indexCards = response;
        this.canStartQuiz();
        this.loading = false;
        this.cdr.detectChanges();
      },
      () => this.router.navigate(['']),
    );
  }

  toggleAnswer(): void {
    this.showAnswer = !this.showAnswer;
  }

  assessIndexCard(assessment: string): void {
    const request = this.createAssessmentRequest(assessment);

    this.httpService.post<undefined>('/api/indexCard/assess', request, () => {
      this.nextIndexCard();
      this.cdr.detectChanges();
    });
  }

  nextIndexCard() {
    this.index++;
    this.showAnswer = false;

    if (this.index >= this.getIndexCardLength()) {
      this.router
        .navigate(['/project', this.id, 'quiz', 'stat'])
        .then(() => this.snackbarService.showSuccessMessage('indexcard.spaced_repetition_done'));
    }
  }

  getIndexCardLength(): number {
    if (this.indexCards) {
      return this.indexCards.length;
    }
    return 0;
  }

  getIndexCard(): IndexCardResponse | undefined {
    if (!this.indexCards) {
      return undefined;
    }
    return this.indexCards[this.index];
  }

  createAssessmentRequest(assessment: string) {
    return {
      indexCardId: this.getIndexCard()?.indexCardId,
      assessment: assessment,
    };
  }

  canStartQuiz() {
    if (this.indexCards!.length == 0) {
      this.router
        .navigate(['/project', this.id])
        .then(() => this.snackbarService.showErrorMessage('indexcard.no_index_cards'));
    }
  }
}
