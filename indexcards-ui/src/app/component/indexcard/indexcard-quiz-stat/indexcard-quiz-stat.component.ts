import {Component, OnInit} from '@angular/core';
import {ResultResponse, UserProjectResponse} from "../../../app.response";
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {MessageService} from "primeng/api";
import {LoginService} from "../../auth/login/login.service";

@Component({
  selector: 'app-indexcard-quiz-stat',
  templateUrl: './indexcard-quiz-stat.component.html',
  styleUrls: ['./indexcard-quiz-stat.component.css']
})
export class IndexcardQuizStatComponent implements OnInit{

  userProject?: UserProjectResponse;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private messageService: MessageService,
    private loginService: LoginService
  ) {
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    this.http.get<UserProjectResponse>('/api/project/project?id=' + id,
      { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => {
          this.userProject = response;
        }, err => {
          const response: ResultResponse = err.error;

          response.errorMessages.forEach(error => {
            this.messageService.add({
              key: 'tr',
              severity: 'error',
              summary: 'ERROR',
              detail: error.message,
            });
          });
          this.router.navigate(['']);
        }
      );
  }

}
