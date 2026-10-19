import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { AllProjects } from './all-projects';
import HttpService from '../../../service/http/http.service';

describe('AllProjects', () => {
  let component: AllProjects;
  let fixture: ComponentFixture<AllProjects>;
  const httpService = { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [AllProjects],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: HttpService, useValue: httpService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AllProjects);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
