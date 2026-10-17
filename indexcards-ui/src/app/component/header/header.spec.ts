import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { Header } from './header';
import { LoginService } from '../../service/login/login.service';
import { AdminService } from '../../service/admin/admin.service';

describe('Header', () => {
  let component: Header;
  let fixture: ComponentFixture<Header>;
  const loginService = { isLoggedIn: vi.fn(() => true), signOut: vi.fn(() => Promise.resolve()) };
  const adminService = { isAdmin: vi.fn(() => false) };

  beforeEach(async () => {
    vi.clearAllMocks();
    await TestBed.configureTestingModule({
      imports: [Header],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: LoginService, useValue: loginService },
        { provide: AdminService, useValue: adminService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Header);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should sign out at Authentik on logout', () => {
    component.logout();

    expect(loginService.signOut).toHaveBeenCalled();
  });
});
