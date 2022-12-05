import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {LoginService} from "./component/auth/login/login.service";
import {Observable} from "rxjs";
import {NotificationsService} from "angular2-notifications";

@Injectable()
export class LoginRequired implements CanActivate {
  constructor(
    private loginService: LoginService,
    private router: Router,
    private notificationService: NotificationsService
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean>|Promise<boolean>|boolean {
    if (!this.loginService.isLoggedIn()) {
      this.notificationService.error('ERROR', 'You need to be logged in to visit this page!');
      this.router.navigate(['login']);
    }
    return true;
  }
}
