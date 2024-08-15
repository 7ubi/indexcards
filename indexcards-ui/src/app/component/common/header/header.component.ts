import {Component, OnInit, Renderer2} from '@angular/core';
import {Router} from "@angular/router";
import {LoginService} from "../../auth/login/login.service";
import {MenuItem} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  items: MenuItem[] = [];
  isDarkTheme = true;

  constructor(
    private router: Router,
    private loginService: LoginService,
    private translateService: TranslateService,
    private renderer: Renderer2
  ) {
    const theme: string | null = localStorage.getItem('theme');
    if (theme === null) {
      localStorage.setItem('theme', 'dark');
    }
    this.isDarkTheme = theme === 'dark';
    this.setThemeClasses();
  }

  ngOnInit(): void {
    this.router.events.subscribe(() => this.setMenuItems());
    this.setMenuItems();
    this.translateService.onLangChange.subscribe(() => this.setMenuItems());
  }

  toggleTheme() {
    this.isDarkTheme = !this.isDarkTheme;
    this.setThemeClasses();
  }

  private setThemeClasses() {
    if (this.isDarkTheme) {
      localStorage.setItem('theme', 'dark');
      this.renderer.addClass(document.documentElement, 'dark-mode');
      this.renderer.removeClass(document.documentElement, 'light-mode');
    } else {
      localStorage.setItem('theme', 'light');
      this.renderer.addClass(document.documentElement, 'light-mode');
      this.renderer.removeClass(document.documentElement, 'dark-mode');
    }
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
    if (this.isLoggedIn()) {
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
