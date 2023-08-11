import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import { LoginService } from "../../auth/login/login.service";
import {MenuItem} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  items: MenuItem[] = [];

  constructor(
    private router: Router,
    private loginService: LoginService,
    private translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.router.events.subscribe(() => this.setMenuItems());
    this.setMenuItems();
    this.translateService.onLangChange.subscribe(() => this.setMenuItems());
  }

  isLoggedIn() {
    return this.loginService.isLoggedIn();
  }

  logout() {
    this.loginService.logout();
  }

  onClickHome() {
    this.router.navigate(['/']);
  }

  setMenuItems() {
    this.items = [];
    if(this.isLoggedIn()) {
      this.items.push(
        {
          label: this.translateService.instant('common.projects'),
          icon: 'pi pi-book',
          command: () => this.router.navigate(['/']),
          escape: false,
        }
      );
    }

  }
}
