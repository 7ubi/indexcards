import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { IndexCardResponse } from '../../app.responses';
import HttpService from '../../service/http/http.service';
import { ActivatedRoute, Router } from '@angular/router';
import { SnackbarService } from '../../service/snackbar/snackbar.service';
import { MatIcon } from '@angular/material/icon';
import { MathjaxDirective } from '../../directives/mathjax.directive';

@Component({
  selector: 'app-card-overview',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, MatIcon, MathjaxDirective],
  templateUrl: './card-overview.html',
  styleUrls: ['./card-overview.css'],
})
export class CardOverview {
  private httpService = inject(HttpService);
  private router = inject(Router);
  private snackbarService = inject(SnackbarService);
  private route = inject(ActivatedRoute);

  @Input({ required: true })
  indexCard?: IndexCardResponse;

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
