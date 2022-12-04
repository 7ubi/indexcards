import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import {NgForm} from '@angular/forms';
import {Router} from "@angular/router";
import {NotificationsService} from "angular2-notifications";

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
    this.http.post('http://localhost:8080/api/auth/signup', signup.value)
      .subscribe((response) => {
        this.router.navigate(['/login']);
        this.notificationService.success(
          'SUCCESS',
          'Account was created! Please login!',
        );
      }, (error) => {
        this.notificationService.error(
          'ERROR',
          'Username is already taken',
        );
      });
  }
}
