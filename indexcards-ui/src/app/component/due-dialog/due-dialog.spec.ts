import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { DueDialog, DueDialogData } from './due-dialog';
import { SettingsService } from '../../service/settings/settings.service';

describe('DueDialog', () => {
  let component: DueDialog;
  let fixture: ComponentFixture<DueDialog>;
  const settingsService = { setDueDialogEnabled: vi.fn() };
  const data: DueDialogData = { cardCount: 2, projects: [{ projectName: 'Math', cardCount: 2 }] };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [DueDialog],
      providers: [
        provideTranslateService(),
        { provide: MAT_DIALOG_DATA, useValue: data },
        { provide: SettingsService, useValue: settingsService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DueDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should disable the due dialog when "don\'t show again" is checked', () => {
    component.toggleDontShowAgain({ checked: true } as MatCheckboxChange);

    expect(settingsService.setDueDialogEnabled).toHaveBeenCalledWith(false);
  });

  it('should re-enable the due dialog when "don\'t show again" is unchecked', () => {
    component.toggleDontShowAgain({ checked: false } as MatCheckboxChange);

    expect(settingsService.setDueDialogEnabled).toHaveBeenCalledWith(true);
  });
});
