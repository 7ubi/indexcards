import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { LoginService } from './login.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable()
export class LoginRequired {
  private loginService = inject(LoginService);
  private router = inject(Router);
  private translateService = inject(TranslateService);
  private snackbar = inject(MatSnackBar);

  canActivate(): Observable<boolean> | Promise<boolean> | boolean {
    if (!this.loginService.isLoggedIn()) {
      if (sessionStorage.getItem('loaded')) {
        this.router.navigate(['login']);
        this.sleep(150).then(() =>
          this.snackbar.open(
            `${this.translateService.instant('common.error')}: ${this.translateService.instant('auth.unauthorized')}`,
            'Ok',
            {
              duration: 3000,
            },
          ),
        );
      } else {
        sessionStorage.setItem('loaded', String(true));
        this.router.navigate(['login']);
      }
      return false;
    }
    return true;
  }

  sleep(ms: number) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}
