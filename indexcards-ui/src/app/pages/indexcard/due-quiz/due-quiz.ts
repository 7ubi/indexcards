import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject,
  ChangeDetectionStrategy,
} from '@angular/core';
import { Router } from '@angular/router';
import HttpService from '../../../service/http/http.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { DueIndexCardResponse } from '../../../app.responses';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { TranslatePipe } from '@ngx-translate/core';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';
import { CardFlip } from '../../../component/card-flip/card-flip';
import {
  AssessmentChart,
  AssessmentSlice,
} from '../../../component/assessment-chart/assessment-chart';

const TALLY_COLORS: Record<'BAD' | 'OK' | 'GOOD', string> = {
  BAD: '#c62828',
  OK: '#f9a825',
  GOOD: '#2e7d32',
};

@Component({
  selector: 'app-due-quiz',
  imports: [
    MatButtonModule,
    MatCardModule,
    TranslatePipe,
    LoadingSpinner,
    CardFlip,
    AssessmentChart,
  ],
  templateUrl: './due-quiz.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './due-quiz.css',
})
export class DueQuiz implements OnInit {
  private router = inject(Router);
  private httpService = inject(HttpService);
  private snackbarService = inject(SnackbarService);
  private cdr = inject(ChangeDetectorRef);

  indexCards?: DueIndexCardResponse[];

  index = 0;

  showAnswer = false;

  finished = false;

  slices: AssessmentSlice[] = (['BAD', 'OK', 'GOOD'] as const).map((assessment) => ({
    label: assessment.toLowerCase(),
    color: TALLY_COLORS[assessment],
    count: 0,
  }));

  loading = true;

  ngOnInit(): void {
    this.httpService.get<DueIndexCardResponse[]>(
      '/api/indexCard/due',
      (response) => {
        this.indexCards = response;
        this.canStartQuiz();
        this.loading = false;
        this.cdr.detectChanges();
      },
      () => this.router.navigate(['']),
    );
  }

  assessIndexCard(assessment: 'BAD' | 'OK' | 'GOOD'): void {
    const request = {
      indexCardId: this.getIndexCard()?.indexCardId,
      assessment: assessment,
    };

    this.httpService.post<undefined>('/api/indexCard/assess', request, () => {
      const slice = this.slices.find((s) => s.label === assessment.toLowerCase());
      if (slice) {
        slice.count++;
      }
      this.nextIndexCard();
      this.cdr.detectChanges();
    });
  }

  nextIndexCard() {
    this.index++;
    this.showAnswer = false;

    if (this.index >= this.getIndexCardLength()) {
      this.finished = true;
    }
  }

  getIndexCardLength(): number {
    if (this.indexCards) {
      return this.indexCards.length;
    }
    return 0;
  }

  getIndexCard(): DueIndexCardResponse | undefined {
    if (!this.indexCards) {
      return undefined;
    }
    return this.indexCards[this.index];
  }

  canStartQuiz() {
    if (this.indexCards!.length == 0) {
      this.router
        .navigate([''])
        .then(() => this.snackbarService.showSuccessMessage('indexcard.nothing_due'));
    }
  }

  backToProjects() {
    this.router.navigate(['']);
  }
}
