import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import {NgForm} from '@angular/forms';
import {Router} from "@angular/router";
import {NotificationsService} from "angular2-notifications";
import {environment} from "../../../../environment/environment";
import {ResultResponse} from "../../../app.response";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {

  constructor(
    private http: HttpClient,
    private router: Router,
    private notificationService: NotificationsService
  ) { }

  createAccount(signup: NgForm) {
    this.http.post<ResultResponse>(environment.apiUrl + 'auth/signup', signup.value)
      .subscribe((response) => {
        if(response.success) {
          this.router.navigate(['/login']);

          this.notificationService.success(
            'SUCCESS',
            'Account was created! Please login!',
          );
        }
        response.errorMessages.forEach((error) => {
          this.notificationService.error(
            'ERROR',
            error.message
          );
        });
      });
  }
}
