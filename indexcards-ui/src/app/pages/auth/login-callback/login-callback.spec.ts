import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { LoginCallback } from './login-callback';
import { LoginService } from '../../../service/login/login.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';

describe('LoginCallback', () => {
  const loginService = { completeLogin: vi.fn() };
  const snackbarService = { showErrorMessage: vi.fn() };
  let router: Router;

  beforeEach(async () => {
    vi.clearAllMocks();
    await TestBed.configureTestingModule({
      imports: [LoginCallback],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: LoginService, useValue: loginService },
        { provide: SnackbarService, useValue: snackbarService },
      ],
    }).compileComponents();
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
  });

  it('should open the projects after a successful login', async () => {
    loginService.completeLogin.mockResolvedValue(undefined);

    TestBed.createComponent(LoginCallback).detectChanges();

    await vi.waitFor(() => expect(router.navigate).toHaveBeenCalledWith(['/']));
  });

  it('should go back to the login page when the login failed', async () => {
    vi.spyOn(console, 'error').mockImplementation(() => undefined);
    loginService.completeLogin.mockRejectedValue(new Error('state mismatch'));

    TestBed.createComponent(LoginCallback).detectChanges();

    await vi.waitFor(() => expect(router.navigate).toHaveBeenCalledWith(['/login']));
    expect(snackbarService.showErrorMessage).toHaveBeenCalledWith('auth.login_failed');
  });
});
