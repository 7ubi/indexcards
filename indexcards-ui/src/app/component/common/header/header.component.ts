import {Component} from '@angular/core';
import { faArrowRightFromBracket } from '@fortawesome/free-solid-svg-icons';
import {LocalService} from "../../../local.service";
import {Router} from "@angular/router";
import { LoginService } from "../../auth/login/login.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  faArrowRightFromBracket = faArrowRightFromBracket;

  constructor(private router: Router, private loginService: LoginService) {
  }

  isLoggedIn() {
    return this.loginService.isLoggedIn();
  }

  logout() {
    LocalService.clearData();
    this.router.navigate(['/login']);
  }
}
