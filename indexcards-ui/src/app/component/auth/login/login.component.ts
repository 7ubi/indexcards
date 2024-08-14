import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {LoginService} from "./login.service";
import {Router} from "@angular/router";
import {MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";
import {LoginResponse} from "../../../app.response";
import {HttpService} from "../../../services/http.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  public loginFormGroup: FormGroup;

  constructor(
    private httpService: HttpService,
    private router: Router,
    private loginService: LoginService,
    private messageService: MessageService,
    private formBuilder: FormBuilder,
    private translateService: TranslateService
  ) {
    this.loginFormGroup = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    })
  }

  ngOnInit(): void {
    if (this.loginService.isLoggedIn()) {
      this.router.navigate(['/']);
    }
  }

  makeLogin() {

    if (!this.loginFormGroup.valid) {
      this.throwInvalidForm();
      return;
    }

    this.httpService.post<LoginResponse>('/api/auth/login', this.getLoginRequestParameter(), response => {
      this.loginService.saveBearer(response);
      this.router.navigate(['/']).then();
    });
  }

  private throwInvalidForm() {
    if (!this.loginFormGroup.get('username')?.valid) {
      this.messageService.add({
        key: 'tr',
        severity: 'error',
        summary: this.translateService.instant('common.error'),
        detail: this.translateService.instant('auth.username_required'),
      });
    }

    if (!this.loginFormGroup.get('password')?.valid) {
      this.messageService.add({
        key: 'tr',
        severity: 'error',
        summary: this.translateService.instant('common.error'),
        detail: this.translateService.instant('auth.password_required'),
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
