import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {environment} from '../../../../environment/environment';
import {ResultResponse} from '../../../app.response';
import {MessageService} from 'primeng/api';

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
    private messageService: MessageService,
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
    if(!this.signUpFormGroup.valid) {
      this.messageService.add({
        key: 'tr',
        severity: 'error',
        summary: 'ERROR',
        detail: 'All fields are required!',
      });
      return;
    }

    if(this.signUpFormGroup.get('password')?.value !== this.signUpFormGroup.get('repeatPassword')?.value) {
      this.messageService.add({
        key: 'tr',
        severity: 'error',
        summary: 'ERROR',
        detail: 'Passwords do not match!',
      });
      return;
    }

    this.http.post<ResultResponse>(environment.apiUrl + 'auth/signup', this.getCreateAccountParameter())
      .subscribe((response) => {
        if(response.success) {
          this.messageService.add({
            key: 'tr',
            severity: 'success',
            summary: 'SUCCESS',
            detail: 'Account was created! Please login!',
          });
          this.router.navigate(['/login']);
        }
        response.errorMessages.forEach((error) => {
          this.messageService.add({
            key: 'tr',
            severity: 'error',
            summary: 'ERROR',
            detail: error.message,
          });
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
