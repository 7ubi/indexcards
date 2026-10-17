import { Injectable, InjectionToken, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { User, UserManager, UserManagerSettings, WebStorageStateStore } from 'oidc-client-ts';
import { AuthConfigResponse } from '../../app.responses';
import LocalService from '../local/local.service';

/** Creates the OIDC client. A token so tests can replace it (vi.mock does not work with the Angular test builder). */
export const USER_MANAGER_FACTORY = new InjectionToken<
  (settings: UserManagerSettings) => UserManager
>('USER_MANAGER_FACTORY', {
  providedIn: 'root',
  factory: () => (settings: UserManagerSettings) => new UserManager(settings),
});

/** Login via Authentik (OIDC Authorization Code + PKCE), which offers username/password and Google. */
@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private router = inject(Router);
  // Not HttpService: that one depends on this service and logs out on errors.
  private http = inject(HttpClient);
  private createUserManager = inject(USER_MANAGER_FACTORY);

  private userManager?: UserManager;
  private user: User | null = null;

  /** Runs on app start: loads the Authentik settings from the backend and restores this tab's session. */
  public async init(): Promise<void> {
    try {
      const config = await firstValueFrom(this.http.get<AuthConfigResponse>('/api/auth/config'));
      this.userManager = this.createUserManager({
        authority: config.authority,
        client_id: config.clientId,
        redirect_uri: `${window.location.origin}/login/callback`,
        post_logout_redirect_uri: `${window.location.origin}/login`,
        response_type: 'code',
        scope: 'openid profile',
        // Like the former app token: only kept for this tab and dropped when it is closed.
        userStore: new WebStorageStateStore({ store: window.sessionStorage }),
        automaticSilentRenew: false,
      });
      this.user = await this.userManager.getUser();
    } catch (error) {
      console.error('Could not initialise the login', error);
    }
  }

  /** Redirects to Authentik. */
  public async login(): Promise<void> {
    if (!this.userManager) {
      await this.init();
    }
    if (!this.userManager) {
      throw new Error('Login is not configured');
    }
    await this.userManager.signinRedirect();
  }

  /** Finishes the login when Authentik redirects back to /login/callback. */
  public async completeLogin(): Promise<void> {
    if (!this.userManager) {
      await this.init();
    }
    if (!this.userManager) {
      throw new Error('Login is not configured');
    }
    this.user = await this.userManager.signinRedirectCallback();
  }

  public getBearer(): string {
    return this.user && !this.user.expired ? this.user.access_token : '';
  }

  public isLoggedIn(): boolean {
    return this.getBearer() !== '';
  }

  public getHeaderWithBearer(): HttpHeaders {
    return this.getBearerOnlyHeader().set('Content-Type', 'application/json');
  }

  public getBearerOnlyHeader(): HttpHeaders {
    const token = this.getBearer();
    // Without a token send no Authorization header: the backend rejects an empty bearer token with 401.
    return token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : new HttpHeaders();
  }

  /** Ends the session in this tab only, e.g. when the backend rejects the token or the account was deleted. */
  public logout(): void {
    this.user = null;
    // Also removes the stored OIDC user (session storage).
    LocalService.clearData();
    this.router.navigate(['/login']);
  }

  /** Logout button: also ends the Authentik session, so the next login does not happen silently. */
  public async signOut(): Promise<void> {
    const idToken = this.user?.id_token;
    this.user = null;
    LocalService.clearData();
    if (!this.userManager) {
      this.router.navigate(['/login']);
      return;
    }
    await this.userManager.signoutRedirect({ id_token_hint: idToken });
  }
}
