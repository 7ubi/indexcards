import { Component } from '@angular/core';
import { faArrowRightFromBracket } from '@fortawesome/free-solid-svg-icons';
import {LocalService} from "../local.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  faArrowRightFromBracket = faArrowRightFromBracket;

  constructor(private router: Router) {
  }

  logout() {
    LocalService.clearData();
    this.router.navigate(['/login']);
  }
}
