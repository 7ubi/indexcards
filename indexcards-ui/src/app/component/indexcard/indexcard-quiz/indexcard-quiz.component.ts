import {Component, OnInit} from '@angular/core';
import {IndexCardResponses, ResultResponse} from "../../../app.response";
import {HttpClient} from "@angular/common/http";
import {LoginService} from "../../auth/login/login.service";
import {ActivatedRoute, Router} from "@angular/router";
import {IndexCardResponse } from "../../../app.response";
import {MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-indexcard-quiz',
  templateUrl: './indexcard-quiz.component.html',
  styleUrls: ['./indexcard-quiz.component.css']
})
export class IndexcardQuizComponent implements OnInit {
  indexCards?: IndexCardResponses;

  index = 0;

  showAnswer = false;

  id: string | null = "";

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private messageService: MessageService,
    private loginService: LoginService,
    private translateService: TranslateService
  ) {
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.http.get<IndexCardResponses>('/api/indexCard/quizIndexCards?id=' + this.id,
      { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => {
          if(response.success) {
            this.indexCards = response;
          }
        }, err => {
          const response: ResultResponse = err.error;

          response.errorMessages.forEach(error => {
            this.messageService.add({
              key: 'tr',
              severity: 'error',
              summary: this.translateService.instant('common.error'),
              detail: error.message,
            });
          })

          this.router.navigate(['']);
        }
      );
  }
  assessIndexCard(assessment: string): void  {
    const request = this.createAssessmentRequest(assessment);

    this.http.post<ResultResponse>('/api/indexCard/assess',
      request, { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => {
          if(response.success) {
            this.nextIndexCard();
          }
        }, err => {
          const response: ResultResponse = err.error;

          response.errorMessages.forEach(error => {
            this.messageService.add({
              key: 'tr',
              severity: 'error',
              summary: this.translateService.instant('common.error'),
              detail: error.message,
            });
          })
        }
      );
  }

  nextIndexCard() {
    this.index++;
    this.showAnswer = false;

    if(this.index >= this.getIndexCardLength()) {

      this.router.navigate(["/project", this.id, "quiz", "stat"]);
      this.messageService.add({
        key: 'tr',
        severity: 'success',
        summary: this.translateService.instant('common.success'),
        detail: this.translateService.instant('indexcard.spaced_repetition_done'),
      });
    }
  }

  getIndexCardLength(): number {
    if(this.indexCards?.indexCardResponses) {
      return this.indexCards?.indexCardResponses.length;
    }
    return 0;
  }

  getIndexCard(): IndexCardResponse | undefined {
    return this.indexCards?.indexCardResponses[this.index];
  }

  createAssessmentRequest(assessment: string) {
    return {
      indexCardId: this.getIndexCard()?.indexCardId,
      assessment: assessment
    };
  }
}
