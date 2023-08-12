import {Injectable} from "@angular/core";
import {CanActivate, Router} from "@angular/router";
import {LoginService} from "./component/auth/login/login.service";
import {Observable} from "rxjs";
import {MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";

@Injectable()
export class LoginRequired implements CanActivate {
  constructor(
    private loginService: LoginService,
    private router: Router,
    private messageService: MessageService,
    private translateService: TranslateService
  ) {}

  canActivate(): Observable<boolean>|Promise<boolean>|boolean {
    if (!this.loginService.isLoggedIn()) {
      if(localStorage.getItem('loaded')) {
        this.messageService.add({
          key: 'tr',
          severity: 'error',
          summary: this.translateService.instant('common.error'),
          detail: this.translateService.instant('auth.unauthorized'),
        });
      } else {
        localStorage.setItem('loaded', String(true));
      }
      this.router.navigate(['login']);
    }
    return true;
  }
}
