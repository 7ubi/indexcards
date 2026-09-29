import { TestBed } from '@angular/core/testing';
import { vi } from 'vitest';

import { AdminService } from './admin.service';
import HttpService from '../http/http.service';
import { LoginService } from '../login/login.service';
import { UserResponse } from '../../app.responses';

describe('AdminService', () => {
  let service: AdminService;
  let token = 'token-a';
  let admin = true;
  const httpService = {
    get: vi.fn((_url: string, subscribe: (response: UserResponse) => void) =>
      subscribe({ username: 'test', firstname: 'Max', surname: 'Muster', admin }),
    ),
  };
  const loginService = { isLoggedIn: vi.fn(() => token !== ''), getBearer: vi.fn(() => token) };

  beforeEach(() => {
    vi.clearAllMocks();
    token = 'token-a';
    admin = true;

    TestBed.configureTestingModule({
      providers: [
        { provide: HttpService, useValue: httpService },
        { provide: LoginService, useValue: loginService },
      ],
    });
    service = TestBed.inject(AdminService);
  });

  it('should cache the admin flag per token', () => {
    const callback = vi.fn();

    service.load(callback);
    service.load(callback);

    expect(callback).toHaveBeenCalledTimes(2);
    expect(callback).toHaveBeenCalledWith(true);
    expect(httpService.get).toHaveBeenCalledTimes(1);
    expect(service.isAdmin()).toBe(true);
  });

  it('should fetch again for a different token', () => {
    service.load(vi.fn());
    token = 'token-b';
    admin = false;

    expect(service.isAdmin()).toBe(false);
    expect(httpService.get).toHaveBeenCalledTimes(2);
  });

  it('should not fetch when logged out', () => {
    token = '';

    expect(service.isAdmin()).toBe(false);
    expect(httpService.get).not.toHaveBeenCalled();
  });
});
