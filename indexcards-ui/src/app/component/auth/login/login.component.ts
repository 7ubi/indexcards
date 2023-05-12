import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { HttpClient } from "@angular/common/http";
import {LoginResponse} from "../../../app.response";
import {LoginService} from "./login.service";
import {Router} from "@angular/router";
import {environment} from "../../../../environment/environment";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  public loginFormGroup: FormGroup;

  constructor(
    private http: HttpClient,
    private router: Router,
    private loginService: LoginService,
    private messageService: MessageService,
    private formBuilder: FormBuilder
  ) {
    this.loginFormGroup = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    })
  }

  ngOnInit(): void {
    if(this.loginService.isLoggedIn()) {
      this.router.navigate(['/']);
    }
  }

  makeLogin() {

    if(!this.loginFormGroup.valid) {
      this.throwInvalidForm();
      return;
    }

    this.http.post<LoginResponse>(environment.apiUrl + 'auth/login', this.getLoginRequestParameter())
      .subscribe(
        response => {
          this.loginService.saveBearer(response);

          this.router.navigate(['/']);
        }, error => {
          if(error.status === 401){
            this.messageService.add({
              key: 'tr',
              severity: 'error',
              summary: 'ERROR',
              detail: 'Wrong Username or Password!',
            });
          }
        }
      );
  }

  private throwInvalidForm() {
    if (!this.loginFormGroup.get('username')?.valid) {
      this.messageService.add({
        key: 'tr',
        severity: 'error',
        summary: 'ERROR',
        detail: 'Username is required',
      });
    }

    if (!this.loginFormGroup.get('password')?.valid) {
      this.messageService.add({
        key: 'tr',
        severity: 'error',
        summary: 'ERROR',
        detail: 'Password is required',
      });
    }
  }

  getLoginRequestParameter() {
    return {
      username: this.loginFormGroup.get('username')?.value,
      password: this.loginFormGroup.get('password')?.value,
    };
  }
}
