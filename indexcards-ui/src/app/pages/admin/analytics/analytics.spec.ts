import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';
import { of } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';

import { Analytics } from './analytics';
import HttpService from '../../../service/http/http.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { AnalyticsResponse } from '../../../app.responses';

describe('Analytics', () => {
  let component: Analytics;
  let fixture: ComponentFixture<Analytics>;
  const analytics: AnalyticsResponse = {
    totals: { users: 3, activeProjects: 2, archivedProjects: 1, indexCards: 4, assessments: 5 },
    assessmentDistribution: { unrated: 1, bad: 0, ok: 1, good: 2 },
    dailyActivity: [
      { date: '2026-09-28', assessments: 1, activeUsers: 1 },
      { date: '2026-09-29', assessments: 3, activeUsers: 2 },
    ],
    dailySignups: [
      { date: '2026-09-28', count: 0 },
      { date: '2026-09-29', count: 1 },
    ],
    users: [
      {
        username: 'test',
        createdAt: null,
        projects: 2,
        indexCards: 2,
        assessments: 3,
        lastActivity: '2026-09-29T10:00:00',
        admin: true,
      },
      {
        username: 'newbie',
        createdAt: '2026-09-29T09:00:00',
        projects: 0,
        indexCards: 0,
        assessments: 0,
        lastActivity: null,
        admin: false,
      },
    ],
  };
  const httpService = {
    get: vi.fn((_url: string, subscribe: (response: AnalyticsResponse) => void) =>
      subscribe(analytics),
    ),
    put: vi.fn((_url: string, _request: unknown, subscribe: () => void) => subscribe()),
  };
  const snackbarService = { showSuccessMessage: vi.fn() };
  let dialogResult = true;
  const dialog = { open: vi.fn(() => ({ afterClosed: () => of(dialogResult) })) };

  beforeEach(async () => {
    vi.clearAllMocks();
    dialogResult = true;

    await TestBed.configureTestingModule({
      imports: [Analytics],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: HttpService, useValue: httpService },
        { provide: SnackbarService, useValue: snackbarService },
        { provide: MatDialog, useValue: dialog },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Analytics);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should load the analytics', () => {
    expect(httpService.get).toHaveBeenCalledWith('/api/admin/analytics', expect.any(Function));
    expect(component.loading).toBe(false);
    expect(component.analytics).toEqual(analytics);
  });

  it('should build the chart data', () => {
    expect(component.slices.map((slice) => slice.count)).toEqual([1, 0, 1, 2]);
    expect(component.assessmentBars).toEqual([
      { label: '28.09.', value: 1 },
      { label: '29.09.', value: 3 },
    ]);
    expect(component.activeUserBars.map((bar) => bar.value)).toEqual([1, 2]);
    expect(component.signupBars.map((bar) => bar.value)).toEqual([0, 1]);
  });

  it('should list every user in the table', () => {
    const rows = fixture.nativeElement.querySelectorAll('tr.mat-mdc-row');
    expect(rows.length).toBe(2);
    expect(fixture.nativeElement.textContent).toContain('newbie');
  });

  it('should show the admin label or the make admin button', () => {
    const rows = fixture.nativeElement.querySelectorAll('tr.mat-mdc-row');
    expect(rows[0].querySelector('.admin-label')).not.toBeNull();
    expect(rows[0].querySelector('.make-admin')).toBeNull();
    expect(rows[1].querySelector('.make-admin')).not.toBeNull();
  });

  it('should make a user admin after confirming and reload', () => {
    fixture.nativeElement.querySelector('tr.mat-mdc-row .make-admin').click();

    expect(dialog.open).toHaveBeenCalledWith(
      expect.anything(),
      expect.objectContaining({
        data: { username: 'newbie', action: 'make_admin', keyPrefix: 'admin.make_admin' },
      }),
    );
    expect(httpService.put).toHaveBeenCalledWith(
      '/api/admin/users/newbie/admin',
      {},
      expect.any(Function),
    );
    expect(snackbarService.showSuccessMessage).toHaveBeenCalledWith('admin.made_admin');
    expect(httpService.get).toHaveBeenCalledTimes(2);
  });

  it('should not make a user admin when the dialog is cancelled', () => {
    dialogResult = false;

    component.makeAdmin('newbie');

    expect(httpService.put).not.toHaveBeenCalled();
    expect(httpService.get).toHaveBeenCalledTimes(1);
  });
});
