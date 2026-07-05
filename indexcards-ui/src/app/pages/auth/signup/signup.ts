import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import HttpService from '../../../service/http/http.service';
import { Router, RouterModule } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { TranslatePipe } from '@ngx-translate/core';
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { PasswordInput } from '../../../component/password-input/password-input';
import { LoginService } from '../../../service/login/login.service';

@Component({
  selector: 'app-signup',
  imports: [
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    TranslatePipe,
    MatFormField,
    MatInput,
    MatLabel,
    MatButton,
    PasswordInput,
  ],
  templateUrl: './signup.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './signup.css',
})
export class Signup {
  private httpService = inject(HttpService);
  private router = inject(Router);
  private formBuilder = inject(FormBuilder);
  private snackbarService = inject(SnackbarService);
  private loginService = inject(LoginService);

  public signUpFormGroup: FormGroup;

  constructor() {
    if (this.loginService.isLoggedIn()) {
      this.router.navigate(['/']).then();
    }

    this.signUpFormGroup = this.formBuilder.group({
      username: ['', Validators.required],
      surname: ['', Validators.required],
      firstname: ['', Validators.required],
      password: ['', Validators.required],
      repeatPassword: ['', Validators.required],
    });
  }

  createAccount() {
    if (!this.signUpFormGroup.valid) {
      this.snackbarService.showErrorMessage('auth.fields_required');
      return;
    }

    if (
      this.signUpFormGroup.get('password')?.value !==
      this.signUpFormGroup.get('repeatPassword')?.value
    ) {
      this.snackbarService.showErrorMessage('auth.password_match');
      return;
    }

    this.httpService.post<undefined>('/api/auth/signup', this.getCreateAccountParameter(), () => {
      this.snackbarService.showSuccessMessage('auth.account_created');
      this.router.navigate(['/login']).then();
    });
  }

  getCreateAccountParameter() {
    return {
      username: this.signUpFormGroup.get('username')?.value,
      firstname: this.signUpFormGroup.get('firstname')?.value,
      surname: this.signUpFormGroup.get('surname')?.value,
      password: this.signUpFormGroup.get('password')?.value,
    };
  }
}
