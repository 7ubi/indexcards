import {Component, OnInit} from '@angular/core';
import {ResultResponse, UserProjectResponse} from "../../../app.response";
import {environment} from "../../../../environment/environment";
import {HttpClient} from "@angular/common/http";
import {LoginService} from "../../auth/login/login.service";
import {ActivatedRoute, Router} from "@angular/router";
import {IndexCardResponse } from "../../../app.response";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-indexcard-quiz',
  templateUrl: './indexcard-quiz.component.html',
  styleUrls: ['./indexcard-quiz.component.css']
})
export class IndexcardQuizComponent implements OnInit {
  userProject?: UserProjectResponse;

  index: number = 0;

  showAnswer: boolean = false;

  id: string | null = "";

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private messageService: MessageService,
    private loginService: LoginService
  ) {
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.http.get<UserProjectResponse>(environment.apiUrl + 'project/project?id=' + this.id,
      { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => {
          if(response.success) {
            this.userProject = response;
          }
        }, err => {
          const response: ResultResponse = err.error;

          response.errorMessages.forEach(error => {
            this.messageService.add({
              key: 'tr',
              severity: 'error',
              summary: 'ERROR',
              detail: error.message,
            });
          })

          this.router.navigate(['']);
        }
      );
  }
  assessIndexCard(assessment: string): void  {
    const request = this.createAssessmentRequest(assessment);

    this.http.post<ResultResponse>(environment.apiUrl + 'indexCard/assess',
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
              summary: 'ERROR',
              detail: error.message,
            });
          })
        }
      );
  }

  nextIndexCard() {
    this.index++;

    if(this.index >= this.getIndexCardLength()) {

      this.router.navigate(["/project", this.id]);
      this.messageService.add({
        key: 'tr',
        severity: 'success',
        summary: 'SUCCESS',
        detail: 'You played through all index cards!',
      });
    }
  }

  getIndexCardLength(): number {
    if(this.userProject?.projectResponse?.indexCardResponses) {
      return this.userProject?.projectResponse?.indexCardResponses.length;
    }
    return 0;
  }

  getIndexCard(): IndexCardResponse | undefined {
    return this.userProject?.projectResponse?.indexCardResponses[this.index];
  }

  createAssessmentRequest(assessment: string) {
    return {
      indexCardId: this.getIndexCard()?.indexCardId,
      assessment: assessment
    };
  }
}
