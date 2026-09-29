import { Component, inject, ChangeDetectionStrategy, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import HttpService from '../../service/http/http.service';
import { LoginService } from '../../service/login/login.service';
import { SnackbarService } from '../../service/snackbar/snackbar.service';
import { PasswordInput } from '../password-input/password-input';

@Component({
  selector: 'app-delete-account',
  imports: [ReactiveFormsModule, TranslatePipe, MatButton, MatIcon, PasswordInput],
  templateUrl: './delete-account.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './delete-account.css',
})
export class DeleteAccount {
  private httpService = inject(HttpService);
  private loginService = inject(LoginService);
  private snackbarService = inject(SnackbarService);
  private formBuilder = inject(FormBuilder);

  readonly deletedItems = ['account', 'projects', 'cards', 'progress', 'images', 'ai_key'];

  readonly deleting = signal(false);

  deleteAccountFormGroup: FormGroup = this.formBuilder.group({
    password: ['', Validators.required],
  });

  deleteAccount(): void {
    if (this.deleting()) {
      return;
    }

    if (!this.deleteAccountFormGroup.valid) {
      this.snackbarService.showErrorMessage('auth.password_required');
      return;
    }

    this.deleting.set(true);
    this.httpService.delete<undefined>(
      '/api/user',
      () => {
        this.loginService.logout();
        this.snackbarService.showSuccessMessage('account.deleted');
      },
      () => this.deleting.set(false),
      { password: this.deleteAccountFormGroup.get('password')?.value },
    );
  }
}
