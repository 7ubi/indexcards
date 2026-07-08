import {
  Component,
  EventEmitter,
  Input,
  Output,
  inject,
  ChangeDetectionStrategy,
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { Assessment, IndexCardResponse } from '../../app.responses';
import HttpService from '../../service/http/http.service';
import { ActivatedRoute, Router } from '@angular/router';
import { SnackbarService } from '../../service/snackbar/snackbar.service';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { MathjaxDirective } from '../../directives/mathjax.directive';
import { MarkdownMathPipe } from '../../pipes/markdown-math.pipe';

const ASSESSMENT_COLORS: Record<string, string> = {
  UNRATED: '#9e9e9e',
  BAD: '#c62828',
  OK: '#f9a825',
  GOOD: '#2e7d32',
};

@Component({
  selector: 'app-card-overview',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule,
    MatIcon,
    MathjaxDirective,
    DatePipe,
    TranslatePipe,
    MarkdownMathPipe,
  ],
  templateUrl: './card-overview.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./card-overview.css'],
})
export class CardOverview {
  private httpService = inject(HttpService);
  private router = inject(Router);
  private snackbarService = inject(SnackbarService);
  private route = inject(ActivatedRoute);

  @Input({ required: true })
  indexCard?: IndexCardResponse;

  isDue(): boolean {
    if (!this.indexCard?.dueDate) {
      return true;
    }
    return new Date(this.indexCard.dueDate).getTime() <= Date.now();
  }

  // assessment arrives from the API as its enum name (e.g. "GOOD"), not the numeric ordinal.
  private assessmentName(): string {
    return (this.indexCard?.assessment as unknown as string) ?? Assessment[Assessment.UNRATED];
  }

  assessmentColor(): string {
    return ASSESSMENT_COLORS[this.assessmentName()];
  }

  assessmentLabel(): string {
    return this.assessmentName().toLowerCase();
  }

  @Output()
  reloadIndexCards = new EventEmitter<void>();

  deleteIndexCard() {
    if (!this.indexCard) {
      return;
    }

    this.httpService.delete<undefined>(
      '/api/indexCard',
      () => {
        this.snackbarService.showSuccessMessage('indexcard.deleted');
        this.reloadIndexCards.emit();
      },
      () => this.router.navigate(['']),
      { indexcardId: this.indexCard.indexCardId },
    );
  }

  editIndexCard() {
    if (!this.indexCard) {
      return;
    }
    this.router
      .navigate(['editIndexCard', this.indexCard.indexCardId], { relativeTo: this.route })
      .then();
  }
}
