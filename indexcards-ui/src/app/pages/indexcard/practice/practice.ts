import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject,
  ChangeDetectionStrategy,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import HttpService from '../../../service/http/http.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { IndexCardResponse, ProjectResponse } from '../../../app.responses';
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
  selector: 'app-practice',
  imports: [
    MatButtonModule,
    MatCardModule,
    TranslatePipe,
    LoadingSpinner,
    CardFlip,
    AssessmentChart,
  ],
  templateUrl: './practice.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './practice.css',
})
export class Practice implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private httpService = inject(HttpService);
  private snackbarService = inject(SnackbarService);
  private cdr = inject(ChangeDetectorRef);

  indexCards?: IndexCardResponse[];

  index = 0;

  showAnswer = false;

  finished = false;

  slices: AssessmentSlice[] = this.createEmptySlices();

  id: string | null = '';

  loading = true;

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.httpService.get<ProjectResponse>(
      `/api/project/${this.id}`,
      (response) => {
        this.indexCards = response.indexCardResponses;
        this.canStartPractice();
        this.loading = false;
        this.cdr.detectChanges();
      },
      () => this.router.navigate(['']),
    );
  }

  assessIndexCard(assessment: 'BAD' | 'OK' | 'GOOD'): void {
    const slice = this.slices.find((s) => s.label === assessment.toLowerCase());
    if (slice) {
      slice.count++;
    }
    this.nextIndexCard();
    this.cdr.detectChanges();
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

  getIndexCard(): IndexCardResponse | undefined {
    if (!this.indexCards) {
      return undefined;
    }
    return this.indexCards[this.index];
  }

  canStartPractice() {
    if (this.indexCards!.length == 0) {
      this.router
        .navigate(['/project', this.id])
        .then(() => this.snackbarService.showErrorMessage('indexcard.no_index_cards'));
    }
  }

  backToProject() {
    this.router.navigate(['/project', this.id]);
  }

  restartPractice(): void {
    this.index = 0;
    this.showAnswer = false;
    this.finished = false;
    this.slices = this.createEmptySlices();
  }

  goToSpacedRepetition(): void {
    this.router.navigate(['/project', this.id, 'quiz']);
  }

  private createEmptySlices(): AssessmentSlice[] {
    return (['BAD', 'OK', 'GOOD'] as const).map((assessment) => ({
      label: assessment.toLowerCase(),
      color: TALLY_COLORS[assessment],
      count: 0,
    }));
  }
}
