import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { LoginService } from '../../service/login/login.service';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [MatToolbar, MatIcon, MatIconButton, RouterLink],
  templateUrl: './header.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './header.css',
})
export class Header {
  private loginService = inject(LoginService);

  isLoggedIn() {
    return this.loginService.isLoggedIn();
  }

  logout() {
    this.loginService.logout();
  }
}
