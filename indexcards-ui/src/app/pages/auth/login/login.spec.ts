import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { Login } from './login';
import { LoginService } from '../../../service/login/login.service';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let loggedIn = false;
  const loginService = {
    isLoggedIn: vi.fn(() => loggedIn),
    login: vi.fn(() => Promise.resolve()),
  };

  async function create(): Promise<Router> {
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: LoginService, useValue: loginService },
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

  it('should go to the projects when already logged in', async () => {
    loggedIn = true;

    const router = await create();

    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
