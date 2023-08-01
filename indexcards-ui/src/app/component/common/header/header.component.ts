import {Component, OnInit} from '@angular/core';
import {LocalService} from "../../../services/local.service";
import {Router} from "@angular/router";
import { LoginService } from "../../auth/login/login.service";
import {MenuItem} from "primeng/api";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  items: MenuItem[] = [];

  constructor(private router: Router, private loginService: LoginService) {
    router.events.subscribe(() => this.setMenuItems());
  }

  ngOnInit(): void {
    this.setMenuItems();
  }

  isLoggedIn() {
    return this.loginService.isLoggedIn();
  }

  logout() {
    LocalService.clearData();
    this.router.navigate(['/login']);
  }

  onClickHome() {
    this.router.navigate(['/']);
  }

  setMenuItems() {
    if(this.isLoggedIn()) {
      this.items = [
        {
          label: 'Projects',
          icon: 'pi pi-book',
        }
      ];
    } else {
      this.items = [{}];
    }
  }
}
