import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {LoginService} from '../../../service/login/login.service';
import HttpService from '../../../service/http/http.service';
import {SnackbarService} from '../../../service/snackbar/snackbar.service';
import {IndexCardResponse} from '../../../app.responses';

@Component({
  selector: 'app-quiz',
  imports: [],
  templateUrl: './quiz.html',
  styleUrl: './quiz.css',
})
export class Quiz implements OnInit {
  indexCards?: IndexCardResponse[];

  index = 0;

  showAnswer = false;

  id: string | null = "";

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private loginService: LoginService,
    private httpService: HttpService,
    private snackbarService: SnackbarService
  ) {
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.httpService.get<IndexCardResponse[]>(`/api/indexCard/quizIndexCards?id=${this.id}`,
      response => {
        this.indexCards = response;
        this.canStartQuiz();
      }, () => this.router.navigate(['']));
  }

  assessIndexCard(assessment: string): void {
    const request = this.createAssessmentRequest(assessment);

    this.httpService.post<undefined>('/api/indexCard/assess',
      request, _ => {
        this.nextIndexCard();
      });
  }

  nextIndexCard() {
    this.index++;
    this.showAnswer = false;

    if (this.index >= this.getIndexCardLength()) {
      this.router.navigate(["/project", this.id, "quiz", "stat"]).then(() => this.snackbarService.showSuccessMessage('indexcard.spaced_repetition_done'));
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
      assessment: assessment
    };
  }

  canStartQuiz() {
    if (this.indexCards!.length == 0) {
      this.router.navigate(["/project", this.id]).then(() => this.snackbarService.showErrorMessage('indexcard.no_index_cards'));
    }
  }
}
