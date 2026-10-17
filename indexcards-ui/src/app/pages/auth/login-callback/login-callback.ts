import { Component, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../../../service/login/login.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';

/** Authentik redirects here after the login (redirect URI `<origin>/login/callback`). */
@Component({
  selector: 'app-login-callback',
  imports: [LoadingSpinner],
  templateUrl: './login-callback.html',
  changeDetection: ChangeDetectionStrategy.Eager,
})
export class LoginCallback implements OnInit {
  private loginService = inject(LoginService);
  private router = inject(Router);
  private snackbarService = inject(SnackbarService);

  ngOnInit(): void {
    this.loginService.completeLogin().then(
      () => this.router.navigate(['/']),
      (error: unknown) => {
        console.error('Login failed', error);
        this.snackbarService.showErrorMessage('auth.login_failed');
        this.router.navigate(['/login']);
      },
    );
  }
}
