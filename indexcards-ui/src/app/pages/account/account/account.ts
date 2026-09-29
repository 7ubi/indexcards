import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject,
  ChangeDetectionStrategy,
} from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { MatButton } from '@angular/material/button';
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { MatSlideToggle, MatSlideToggleChange } from '@angular/material/slide-toggle';
import HttpService from '../../../service/http/http.service';
import { LoginService } from '../../../service/login/login.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { SettingsService } from '../../../service/settings/settings.service';
import { LoginResponse, UserResponse } from '../../../app.responses';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';
import { PasswordInput } from '../../../component/password-input/password-input';
import { DeleteAccount } from '../../../component/delete-account/delete-account';
import { AiApiKey } from '../../../component/ai-api-key/ai-api-key';

@Component({
  selector: 'app-account',
  imports: [
    ReactiveFormsModule,
    TranslatePipe,
    MatButton,
    MatFormField,
    MatInput,
    MatLabel,
    MatSlideToggle,
    LoadingSpinner,
    PasswordInput,
    DeleteAccount,
    AiApiKey,
  ],
  templateUrl: './account.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './account.css',
})
export class Account implements OnInit {
  private httpService = inject(HttpService);
  private loginService = inject(LoginService);
  private snackbarService = inject(SnackbarService);
  private formBuilder = inject(FormBuilder);
  private settingsService = inject(SettingsService);
  private cdr = inject(ChangeDetectorRef);

  user?: UserResponse;
  loading = true;
  showDueDialog = this.settingsService.isDueDialogEnabled();

  usernameFormGroup: FormGroup = this.formBuilder.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  passwordFormGroup: FormGroup = this.formBuilder.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', Validators.required],
    repeatPassword: ['', Validators.required],
  });

  ngOnInit(): void {
    this.httpService.get<UserResponse>('/api/user', (response) => {
      this.user = response;
      this.usernameFormGroup.patchValue({ username: response.username });
      this.loading = false;
      this.cdr.detectChanges();
    });
  }

  changeUsername(): void {
    if (!this.usernameFormGroup.valid) {
      this.snackbarService.showErrorMessage('auth.fields_required');
      return;
    }

    const username = this.usernameFormGroup.get('username')?.value.trim();
    if (username === this.user?.username) {
      this.snackbarService.showWarnMessage('account.username_not_changed');
      return;
    }

    this.httpService.put<LoginResponse>(
      '/api/user/username',
      { username, password: this.usernameFormGroup.get('password')?.value },
      (response) => {
        // The username is the subject of the token, so the old token no longer identifies this user.
        this.loginService.saveBearer(response);
        this.user = { ...this.user!, username: response.username };
        this.usernameFormGroup.reset({ username: response.username, password: '' });
        this.snackbarService.showSuccessMessage('account.username_changed');
        this.cdr.detectChanges();
      },
    );
  }

  changePassword(): void {
    if (!this.passwordFormGroup.valid) {
      this.snackbarService.showErrorMessage('auth.fields_required');
      return;
    }

    if (
      this.passwordFormGroup.get('newPassword')?.value !==
      this.passwordFormGroup.get('repeatPassword')?.value
    ) {
      this.snackbarService.showErrorMessage('auth.password_match');
      return;
    }

    this.httpService.put<undefined>(
      '/api/user/password',
      {
        currentPassword: this.passwordFormGroup.get('currentPassword')?.value,
        newPassword: this.passwordFormGroup.get('newPassword')?.value,
      },
      () => {
        this.passwordFormGroup.reset({ currentPassword: '', newPassword: '', repeatPassword: '' });
        this.snackbarService.showSuccessMessage('account.password_changed');
        this.cdr.detectChanges();
      },
    );
  }

  toggleDueDialog(event: MatSlideToggleChange): void {
    this.showDueDialog = event.checked;
    this.settingsService.setDueDialogEnabled(event.checked);
  }
}
