import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { DueQuiz } from './due-quiz';
import HttpService from '../../../service/http/http.service';

describe('DueQuiz', () => {
  let component: DueQuiz;
  let fixture: ComponentFixture<DueQuiz>;
  const httpService = { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [DueQuiz],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: HttpService, useValue: httpService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DueQuiz);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
