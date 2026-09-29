import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, UrlTree } from '@angular/router';
import { vi } from 'vitest';

import { AdminRequired } from './admin-required';
import { AdminService } from '../admin/admin.service';
import { LoginService } from './login.service';
import { SnackbarService } from '../snackbar/snackbar.service';

describe('AdminRequired', () => {
  let guard: AdminRequired;
  let loggedIn = true;
  const adminService = { load: vi.fn() };
  const loginService = { isLoggedIn: vi.fn(() => loggedIn) };
  const snackbarService = { showErrorMessage: vi.fn() };

  beforeEach(() => {
    vi.clearAllMocks();
    loggedIn = true;

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        AdminRequired,
        { provide: AdminService, useValue: adminService },
        { provide: LoginService, useValue: loginService },
        { provide: SnackbarService, useValue: snackbarService },
      ],
    });
    guard = TestBed.inject(AdminRequired);
  });

  it('should let admins through', async () => {
    adminService.load.mockImplementation((callback: (admin: boolean) => void) => callback(true));

    await expect(guard.canActivate()).resolves.toBe(true);
    expect(snackbarService.showErrorMessage).not.toHaveBeenCalled();
  });

  it('should redirect everyone else to the project overview with an error', async () => {
    adminService.load.mockImplementation((callback: (admin: boolean) => void) => callback(false));

    const result = await guard.canActivate();

    expect(result).toBeInstanceOf(UrlTree);
    expect(TestBed.inject(Router).serializeUrl(result as UrlTree)).toBe('/');
    expect(snackbarService.showErrorMessage).toHaveBeenCalledWith('auth.admin_required');
  });

  it('should not show an error when the user was logged out meanwhile', async () => {
    loggedIn = false;
    adminService.load.mockImplementation((callback: (admin: boolean) => void) => callback(false));

    const result = await guard.canActivate();

    expect(result).toBeInstanceOf(UrlTree);
    expect(snackbarService.showErrorMessage).not.toHaveBeenCalled();
  });
});
