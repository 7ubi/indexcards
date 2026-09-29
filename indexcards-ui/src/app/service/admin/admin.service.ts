import { Injectable, inject } from '@angular/core';
import HttpService from '../http/http.service';
import { LoginService } from '../login/login.service';
import { UserResponse } from '../../app.responses';

/**
 * Whether the logged-in user is an admin. The answer is cached per bearer token, so logging in as someone else or
 * changing the username (new token) fetches it again. The backend enforces the role on /api/admin/** regardless.
 */
@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private httpService = inject(HttpService);
  private loginService = inject(LoginService);

  private token?: string;
  private admin?: boolean;
  private loading = false;
  private callbacks: ((admin: boolean) => void)[] = [];

  /** Cached answer for the current token; starts loading it in the background and returns false until known. */
  public isAdmin(): boolean {
    if (!this.loginService.isLoggedIn()) {
      return false;
    }
    if (this.token !== this.loginService.getBearer()) {
      this.load(() => undefined);
    }
    return this.admin ?? false;
  }

  public load(callback: (admin: boolean) => void): void {
    const token = this.loginService.getBearer();
    if (this.token === token && this.admin !== undefined) {
      callback(this.admin);
      return;
    }

    this.callbacks.push(callback);
    if (this.loading && this.token === token) {
      return;
    }

    this.token = token;
    this.admin = undefined;
    this.loading = true;
    this.httpService.get<UserResponse>(
      '/api/user',
      (response) => this.resolve(token, response.admin),
      () => this.resolve(token, false),
    );
  }

  private resolve(token: string, admin: boolean): void {
    if (this.token !== token) {
      return;
    }
    this.admin = admin;
    this.loading = false;
    const callbacks = this.callbacks;
    this.callbacks = [];
    callbacks.forEach((callback) => callback(admin));
  }
}
