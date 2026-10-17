import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { Login } from './login';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { LoginService } from '../../../service/login/login.service';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let loggedIn = false;
  const loginService = {
    isLoggedIn: vi.fn(() => loggedIn),
    login: vi.fn(() => Promise.resolve()),
  };

  const snackbarService = { showErrorMessage: vi.fn() };

  async function create(): Promise<Router> {
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: LoginService, useValue: loginService },
        { provide: SnackbarService, useValue: snackbarService },
      ],
    }).compileComponents();
    const router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    await fixture.whenStable();
    return router;
  }

  beforeEach(() => {
    vi.clearAllMocks();
    loggedIn = false;
  });

  it('should start the login when the button is clicked', async () => {
    await create();

    (fixture.nativeElement as HTMLElement).querySelector('button')!.click();

    expect(loginService.login).toHaveBeenCalled();
    expect(component).toBeTruthy();
  });

  it('should show a message when the login cannot be started', async () => {
    vi.spyOn(console, 'error').mockImplementation(() => undefined);
    loginService.login.mockRejectedValueOnce(new Error('Login is not configured'));
    await create();

    (fixture.nativeElement as HTMLElement).querySelector('button')!.click();
    await fixture.whenStable();

    expect(snackbarService.showErrorMessage).toHaveBeenCalledWith('auth.login_failed');
  });

  it('should go to the projects when already logged in', async () => {
    loggedIn = true;

    const router = await create();

    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
