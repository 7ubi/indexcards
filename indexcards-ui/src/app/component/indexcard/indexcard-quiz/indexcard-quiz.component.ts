import {Component, OnInit} from '@angular/core';
import {ResultResponse, UserProjectResponse} from "../../../app.response";
import {environment} from "../../../../environment/environment";
import {HttpClient} from "@angular/common/http";
import {NotificationsService} from "angular2-notifications";
import {LoginService} from "../../auth/login/login.service";
import {ActivatedRoute} from "@angular/router";
import {IndexCardResponse } from "../../../app.response";
import {faCheck, faThumbsUp, faXmark} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-indexcard-quiz',
  templateUrl: './indexcard-quiz.component.html',
  styleUrls: ['./indexcard-quiz.component.css']
})
export class IndexcardQuizComponent implements OnInit {

  readonly faCheck = faCheck;
  readonly faThumbsUp = faThumbsUp;
  readonly faXmark = faXmark;

  userProject?: UserProjectResponse;

  index: number = 0;

  showAnswer: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private notificationService: NotificationsService,
    private loginService: LoginService
  ) {
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    this.http.get<UserProjectResponse>(environment.apiUrl + 'project/project?id=' + id,
      { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => {
          if(response.success) {
            this.userProject = response;
          }
          response.errorMessages.forEach((error) => {
            this.notificationService.error(
              'ERROR',
              error.message
            );
          });
        }
      );
  }
  assessIndexCard(assessment: string): void  {
    const request = this.createAssessmentRequest(assessment);

    console.log(request)

    this.http.post<ResultResponse>(environment.apiUrl + 'indexCard/assess',
      request, { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => {
          if(response.success) {
            this.nextIndexCard();
          }
          response.errorMessages.forEach((error) => {
            this.notificationService.error(
              'ERROR',
              error.message
            );
          });
        }
      );
  }

  nextIndexCard() {
    this.index++;

    if(this.index >= this.getIndexCardLength()) {
      this.notificationService.success(
        'SUCCESS',
        'You played through all index cards!'
      );
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
