import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { Quiz } from './quiz';
import HttpService from '../../../service/http/http.service';
import { LoginService } from '../../../service/login/login.service';

describe('Quiz', () => {
  let component: Quiz;
  let fixture: ComponentFixture<Quiz>;
  const httpService = { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() };
  const loginService = { isLoggedIn: vi.fn(() => true), logout: vi.fn() };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [Quiz],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: HttpService, useValue: httpService },
        { provide: LoginService, useValue: loginService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Quiz);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
