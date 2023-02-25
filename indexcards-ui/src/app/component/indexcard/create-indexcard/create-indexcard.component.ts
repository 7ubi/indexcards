import {Component, OnInit} from '@angular/core';
import {NgForm} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ResultResponse} from "../../../app.response";
import {environment} from "../../../../environment/environment";
import {LoginService} from "../../auth/login/login.service";
import {NotificationsService} from "angular2-notifications";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-create-indexcard',
  templateUrl: './create-indexcard.component.html',
  styleUrls: ['./create-indexcard.component.css']
})
export class CreateIndexcardComponent implements OnInit {

  id: string | null | undefined;

  constructor(
    private http: HttpClient,
    private loginService: LoginService,
    private notificationService: NotificationsService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
      this.id = this.route.snapshot.paramMap.get('id');
    }

  createIndexcard(indexcard: NgForm) {
    const request = this.createRequest(indexcard);

    this.http.post<ResultResponse>(environment.apiUrl + 'indexCard/create', request,
      { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => {
          if(response.success) {
            this.notificationService.success("SUCCESS", "Index Card was created");
          }

          response.errorMessages.forEach(error => {
            this.notificationService.error("ERROR", error.message)
          })
        }
      )
  }

  createRequest(indexcard: NgForm) {
    return {
      projectId: this.id,
      question: indexcard.value.question,
      answer: indexcard.value.answer
    }
  }
}
