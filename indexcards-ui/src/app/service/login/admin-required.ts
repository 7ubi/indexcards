import { Injectable, inject } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { AdminService } from '../admin/admin.service';
import { LoginService } from './login.service';
import { SnackbarService } from '../snackbar/snackbar.service';

/** Use after LoginRequired: only lets admins through and sends everyone else to the project overview. */
@Injectable()
export class AdminRequired {
  private adminService = inject(AdminService);
  private loginService = inject(LoginService);
  private snackbarService = inject(SnackbarService);
  private router = inject(Router);

  canActivate(): Promise<boolean | UrlTree> {
    return new Promise((resolve) =>
      this.adminService.load((admin) => {
        if (admin) {
          resolve(true);
          return;
        }
        // A failed /api/user may already have logged the user out and shown its own error.
        if (this.loginService.isLoggedIn()) {
          this.snackbarService.showErrorMessage('auth.admin_required');
        }
        resolve(this.router.createUrlTree(['']));
      }),
    );
  }
}
