import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { CreateIndexcard } from './create-indexcard';
import HttpService from '../../../service/http/http.service';

describe('CreateIndexcard', () => {
  let component: CreateIndexcard;
  let fixture: ComponentFixture<CreateIndexcard>;
  const httpService = { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [CreateIndexcard],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: HttpService, useValue: httpService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateIndexcard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
