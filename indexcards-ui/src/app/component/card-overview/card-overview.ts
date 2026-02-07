import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {IndexCardResponse} from '../../app.responses';
import HttpService from '../../service/http/http.service';
import {ActivatedRoute, Router} from '@angular/router';
import {SnackbarService} from '../../service/snackbar/snackbar.service';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-card-overview',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, MatIcon],
  templateUrl: './card-overview.html',
  styleUrls: ['./card-overview.css']
})
export class CardOverview {

  @Input({required: true})
  indexCard?: IndexCardResponse;

  @Output()
  reloadIndexCards = new EventEmitter<void>();

  constructor(private httpService: HttpService, private router: Router, private snackbarService: SnackbarService,
              private route: ActivatedRoute) {
  }


  deleteIndexCard() {
    if (!this.indexCard) {
      return;
    }

    this.httpService.delete<undefined>('/api/indexCard/delete', (_) => {
      this.snackbarService.showSuccessMessage('indexcard.deleted');
      this.reloadIndexCards.emit();

    }, () => this.router.navigate(['']), {indexcardId: this.indexCard.indexCardId});
  }

  editIndexCard() {
    if (!this.indexCard) {
      return;
    }
    this.router.navigate(['editIndexCard', this.indexCard.indexCardId], {relativeTo: this.route}).then();
  }
}
