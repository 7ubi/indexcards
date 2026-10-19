import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { Project } from './project';
import HttpService from '../../../service/http/http.service';

describe('Project', () => {
  let component: Project;
  let fixture: ComponentFixture<Project>;
  const httpService = { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [Project],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: HttpService, useValue: httpService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Project);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
