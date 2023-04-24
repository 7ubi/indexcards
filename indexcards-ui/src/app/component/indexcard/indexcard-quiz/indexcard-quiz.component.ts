import {Component, OnInit} from '@angular/core';
import {UserProjectResponse} from "../../../app.response";
import {environment} from "../../../../environment/environment";
import {HttpClient} from "@angular/common/http";
import {NotificationsService} from "angular2-notifications";
import {LoginService} from "../../auth/login/login.service";
import {ActivatedRoute} from "@angular/router";
import {IndexCardResponse } from "../../../app.response";

@Component({
  selector: 'app-indexcard-quiz',
  templateUrl: './indexcard-quiz.component.html',
  styleUrls: ['./indexcard-quiz.component.css']
})
export class IndexcardQuizComponent implements OnInit {

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

  getIndexCard(): IndexCardResponse | undefined {
    return this.userProject?.projectResponse?.indexCardResponses[this.index];
  }
}
