import {Component, OnInit} from '@angular/core';
import {IndexCardResponse} from "../../../app.response";
import {HttpClient} from "@angular/common/http";
import {LoginService} from "../../auth/login/login.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";
import {HttpService} from "../../../services/http.service";

@Component({
  selector: 'app-indexcard-quiz',
  templateUrl: './indexcard-quiz.component.html',
  styleUrls: ['./indexcard-quiz.component.css']
})
export class IndexcardQuizComponent implements OnInit {
  indexCards?: IndexCardResponse[];

  index = 0;

  showAnswer = false;

  id: string | null = "";

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private messageService: MessageService,
    private loginService: LoginService,
    private translateService: TranslateService,
    private httpService: HttpService
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

      this.router.navigate(["/project", this.id, "quiz", "stat"]).then();
      this.messageService.add({
        key: 'tr',
        severity: 'success',
        summary: this.translateService.instant('common.success'),
        detail: this.translateService.instant('indexcard.spaced_repetition_done'),
      });
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
      this.messageService.add({
        key: 'tr',
        severity: 'error',
        summary: this.translateService.instant('common.error'),
        detail: this.translateService.instant('indexcard.no_index_cards'),
      });
      this.router.navigate(["/project", this.id]).then();
    }
  }
}
