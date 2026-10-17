import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { vi } from 'vitest';

import { LoginService, USER_MANAGER_FACTORY } from './login.service';

describe('LoginService', () => {
  let service: LoginService;
  let httpMock: HttpTestingController;
  let router: Router;
  const user = { access_token: 'access', id_token: 'id', expired: false };
  const userManager = {
    getUser: vi.fn(),
    signinRedirect: vi.fn(() => Promise.resolve()),
    signinRedirectCallback: vi.fn(),
    signoutRedirect: vi.fn(() => Promise.resolve()),
  };
  const userManagerFactory = vi.fn(() => userManager);

  beforeEach(() => {
    vi.clearAllMocks();
    sessionStorage.clear();
    userManager.getUser.mockResolvedValue(null);

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: USER_MANAGER_FACTORY, useValue: userManagerFactory },
      ],
    });
    service = TestBed.inject(LoginService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
  });

  afterEach(() => httpMock.verify());

  async function init(): Promise<void> {
    const done = service.init();
    httpMock
      .expectOne('/api/auth/config')
      .flush({ authority: 'http://auth/application/o/indexcards/', clientId: 'indexcards' });
    await done;
  }

  it('should configure the OIDC client from the backend', async () => {
    await init();

    expect(userManagerFactory).toHaveBeenCalledWith(
      expect.objectContaining({
        authority: 'http://auth/application/o/indexcards/',
        client_id: 'indexcards',
        redirect_uri: `${window.location.origin}/login/callback`,
        response_type: 'code',
        scope: 'openid profile',
      }),
    );
    expect(service.isLoggedIn()).toBe(false);
    expect(service.getBearer()).toBe('');
  });

  it('should restore the session of this tab', async () => {
    userManager.getUser.mockResolvedValue(user);

    await init();

    expect(service.isLoggedIn()).toBe(true);
    expect(service.getBearer()).toBe('access');
    expect(service.getHeaderWithBearer().get('Authorization')).toBe('Bearer access');
    expect(service.getHeaderWithBearer().get('Content-Type')).toBe('application/json');
  });

  it('should treat an expired token as logged out and send no Authorization header', async () => {
    userManager.getUser.mockResolvedValue({ ...user, expired: true });

    await init();

    expect(service.isLoggedIn()).toBe(false);
    expect(service.getBearerOnlyHeader().has('Authorization')).toBe(false);
  });

  it('should stay logged out when the config cannot be loaded', async () => {
    vi.spyOn(console, 'error').mockImplementation(() => undefined);
    const done = service.init();
    httpMock
      .expectOne('/api/auth/config')
      .flush('down', { status: 502, statusText: 'Bad Gateway' });
    await done;

    expect(service.isLoggedIn()).toBe(false);
    expect(userManagerFactory).not.toHaveBeenCalled();
  });

  it('should redirect to Authentik on login', async () => {
    await init();

    await service.login();

    expect(userManager.signinRedirect).toHaveBeenCalled();
  });

  it('should reject the login when the login is not configured', async () => {
    vi.spyOn(console, 'error').mockImplementation(() => undefined);
    const done = service.login();
    httpMock.expectOne('/api/auth/config').error(new ProgressEvent('error'));

    await expect(done).rejects.toThrow('Login is not configured');
    expect(userManager.signinRedirect).not.toHaveBeenCalled();
  });

  it('should reject the login when the redirect fails', async () => {
    userManager.signinRedirect.mockRejectedValueOnce(new Error('network'));
    await init();

    await expect(service.login()).rejects.toThrow('network');
  });

  it('should store the user after the redirect back', async () => {
    userManager.signinRedirectCallback.mockResolvedValue(user);
    await init();

    await service.completeLogin();

    expect(service.isLoggedIn()).toBe(true);
    expect(service.getBearer()).toBe('access');
  });

  it('should log out locally', async () => {
    userManager.getUser.mockResolvedValue(user);
    await init();
    sessionStorage.setItem('loaded', 'true');

    service.logout();

    expect(service.isLoggedIn()).toBe(false);
    expect(sessionStorage.getItem('loaded')).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
    expect(userManager.signoutRedirect).not.toHaveBeenCalled();
  });

  it('should end the Authentik session on sign out', async () => {
    userManager.getUser.mockResolvedValue(user);
    await init();

    await service.signOut();

    expect(service.isLoggedIn()).toBe(false);
    expect(userManager.signoutRedirect).toHaveBeenCalledWith({ id_token_hint: 'id' });
  });
});
