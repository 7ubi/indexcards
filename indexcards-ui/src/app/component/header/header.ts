import {Component} from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';
import {LoginService} from '../../service/login/login.service';
import {MatIcon} from '@angular/material/icon';
import {MatIconButton} from '@angular/material/button';
import {Router} from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [
    MatToolbar,
    MatIcon,
    MatIconButton
  ],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {

  constructor(
    private router: Router,
    private loginService: LoginService
  ) {
  }

  isLoggedIn() {
    return this.loginService.isLoggedIn();
  }

  logout() {
    this.loginService.logout();
  }

  toHome() {
    this.router.navigate(['/']).then();
  }
}
