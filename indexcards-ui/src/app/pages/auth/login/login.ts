import { Component, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { LoginService } from '../../../service/login/login.service';

@Component({
  selector: 'app-login',
  imports: [TranslatePipe, MatButton, MatIcon, RouterLink],
  templateUrl: './login.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './login.css',
})
export class Login implements OnInit {
  private router = inject(Router);
  private loginService = inject(LoginService);
  private snackbarService = inject(SnackbarService);

  ngOnInit(): void {
    if (this.loginService.isLoggedIn()) {
      this.router.navigate(['/']);
    }
  }

  login(): void {
    this.loginService.login().catch((error: unknown) => {
      console.error('Could not start the login', error);
      this.snackbarService.showErrorMessage('auth.login_failed');
    });
  }
}
