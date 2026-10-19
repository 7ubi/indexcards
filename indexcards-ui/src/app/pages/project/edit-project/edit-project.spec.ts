import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { EditProject } from './edit-project';
import HttpService from '../../../service/http/http.service';

describe('EditProject', () => {
  let component: EditProject;
  let fixture: ComponentFixture<EditProject>;
  const httpService = { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [EditProject],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: HttpService, useValue: httpService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditProject);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
