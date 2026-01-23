import {Component, OnInit} from '@angular/core';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterModule} from '@angular/router';
import {LoginService} from '../../../service/login/login.service';
import HttpService from '../../../service/http/http.service';
import {LoginResponse} from '../../../app.responses';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {CommonModule} from '@angular/common';
import {PasswordInput} from '../../../component/password-input/password-input';

@Component({
  selector: 'app-login',
  imports: [
    CommonModule,
    TranslateModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatSnackBarModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    PasswordInput,
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {

  public loginFormGroup: FormGroup;

  constructor(
    private httpService: HttpService,
    private router: Router,
    private loginService: LoginService,
    private formBuilder: FormBuilder,
    private translateService: TranslateService,
    private snackbar: MatSnackBar
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
      this.snackbar.open(`${this.translateService.instant(`common.error`)}: ${this.translateService.instant(`auth.username_required`)}`, 'Ok', {
        duration: 3000
      });
    }

    if (!this.loginFormGroup.get('password')?.valid) {
      this.snackbar.open(`${this.translateService.instant(`common.error`)}: ${this.translateService.instant(`auth.password_required`)}`, 'Ok', {
        duration: 3000
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
