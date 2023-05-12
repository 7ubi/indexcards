import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
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
  public signUpFormGroup: FormGroup;

  constructor(
    private http: HttpClient,
    private router: Router,
    private notificationService: NotificationsService,
    private formBuilder: FormBuilder
  ) {
    this.signUpFormGroup = this.formBuilder.group({
      username: ['', Validators.required],
      surname: ['', Validators.required],
      firstname: ['', Validators.required],
      password: ['', Validators.required],
      repeatPassword: ['', Validators.required]
    });
  }

  createAccount() {
    console.log(this.getCreateAccountParameter())
    this.http.post<ResultResponse>(environment.apiUrl + 'auth/signup', this.getCreateAccountParameter())
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

  getCreateAccountParameter() {
    return {
      username: this.signUpFormGroup.get('username')?.value,
      firstname: this.signUpFormGroup.get('firstname')?.value,
      surname: this.signUpFormGroup.get('surname')?.value,
      password: this.signUpFormGroup.get('password')?.value,
    }
  }
}
