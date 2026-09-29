import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { LoginService } from '../../service/login/login.service';
import { AdminService } from '../../service/admin/admin.service';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-header',
  imports: [
    MatToolbar,
    MatIcon,
    MatIconButton,
    RouterLink,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
    TranslatePipe,
  ],
  templateUrl: './header.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './header.css',
})
export class Header {
  private loginService = inject(LoginService);
  private adminService = inject(AdminService);

  isLoggedIn() {
    return this.loginService.isLoggedIn();
  }

  isAdmin() {
    return this.adminService.isAdmin();
  }

  logout() {
    this.loginService.logout();
  }
}
