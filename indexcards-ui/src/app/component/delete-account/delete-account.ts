import { Component, inject, input, ChangeDetectionStrategy, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import HttpService from '../../service/http/http.service';
import { LoginService } from '../../service/login/login.service';
import { SnackbarService } from '../../service/snackbar/snackbar.service';

@Component({
  selector: 'app-delete-account',
  imports: [
    ReactiveFormsModule,
    TranslatePipe,
    MatButton,
    MatIcon,
    MatFormField,
    MatInput,
    MatLabel,
  ],
  templateUrl: './delete-account.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './delete-account.css',
})
export class DeleteAccount {
  private httpService = inject(HttpService);
  private loginService = inject(LoginService);
  private snackbarService = inject(SnackbarService);
  private formBuilder = inject(FormBuilder);

  /** The user has to type it to confirm the deletion (there is no password to ask for anymore). */
  readonly username = input.required<string>();

  readonly deletedItems = ['account', 'projects', 'cards', 'progress', 'images'];

  readonly deleting = signal(false);

  deleteAccountFormGroup: FormGroup = this.formBuilder.group({
    username: [''],
  });

  deleteAccount(): void {
    if (this.deleting()) {
      return;
    }

    const confirmation = (this.deleteAccountFormGroup.get('username')?.value ?? '').trim();
    if (confirmation === '' || confirmation !== this.username()) {
      this.snackbarService.showErrorMessage('account.delete_username_mismatch');
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
      { username: confirmation },
    );
  }
}
