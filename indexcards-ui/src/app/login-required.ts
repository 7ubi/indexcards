import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {LoginService} from "./component/auth/login/login.service";
import {Observable} from "rxjs";
import {MessageService} from "primeng/api";

@Injectable()
export class LoginRequired implements CanActivate {
  constructor(
    private loginService: LoginService,
    private router: Router,
    private messageService: MessageService
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean>|Promise<boolean>|boolean {
    if (!this.loginService.isLoggedIn()) {
      this.messageService.add({
        key: 'tr',
        severity: 'error',
        summary: 'ERROR',
        detail: 'You need to be logged in to enter this page!',
      });
      this.router.navigate(['login']);
    }
    return true;
  }
}
