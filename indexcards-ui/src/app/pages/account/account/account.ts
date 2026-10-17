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
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { SettingsService } from '../../../service/settings/settings.service';
import { UserResponse } from '../../../app.responses';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';
import { DeleteAccount } from '../../../component/delete-account/delete-account';

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
    DeleteAccount,
  ],
  templateUrl: './account.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './account.css',
})
export class Account implements OnInit {
  private httpService = inject(HttpService);
  private snackbarService = inject(SnackbarService);
  private formBuilder = inject(FormBuilder);
  private settingsService = inject(SettingsService);
  private cdr = inject(ChangeDetectorRef);

  user?: UserResponse;
  loading = true;
  showDueDialog = this.settingsService.isDueDialogEnabled();

  usernameFormGroup: FormGroup = this.formBuilder.group({
    username: ['', Validators.required],
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

    this.httpService.put<UserResponse>('/api/user/username', { username }, (response) => {
      this.user = response;
      this.usernameFormGroup.reset({ username: response.username });
      this.snackbarService.showSuccessMessage('account.username_changed');
      this.cdr.detectChanges();
    });
  }

  toggleDueDialog(event: MatSlideToggleChange): void {
    this.showDueDialog = event.checked;
    this.settingsService.setDueDialogEnabled(event.checked);
  }
}
