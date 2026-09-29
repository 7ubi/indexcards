import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { AiApiKey } from './ai-api-key';
import { AiService } from '../../service/ai/ai.service';
import { SnackbarService } from '../../service/snackbar/snackbar.service';
import { AiStatusResponse } from '../../app.responses';

describe('AiApiKey', () => {
  let component: AiApiKey;
  let fixture: ComponentFixture<AiApiKey>;
  let status: AiStatusResponse;
  const saved: AiStatusResponse = {
    enabled: true,
    apiKeyConfigured: true,
    apiKeyHint: 'AbCd',
    model: 'gemini-3.8-flash',
    maxCards: 30,
    maxNotesChars: 20000,
    maxPdfBytes: 10485760,
    maxPdfPages: 20,
  };
  const aiService = {
    getStatus: vi.fn((callback: (s: AiStatusResponse) => void) => callback(status)),
    saveApiKey: vi.fn((_key: string, callback: (s: AiStatusResponse) => void) => callback(saved)),
    deleteApiKey: vi.fn((callback: (s: AiStatusResponse) => void) =>
      callback({ ...saved, apiKeyConfigured: false, apiKeyHint: null }),
    ),
  };
  const snackbarService = { showSuccessMessage: vi.fn(), showErrorMessage: vi.fn() };

  async function create() {
    await TestBed.configureTestingModule({
      imports: [AiApiKey],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: AiService, useValue: aiService },
        { provide: SnackbarService, useValue: snackbarService },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(AiApiKey);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  }

  beforeEach(() => {
    vi.clearAllMocks();
    status = { ...saved, apiKeyConfigured: false, apiKeyHint: null };
  });

  it('should be hidden when the server has the feature disabled', async () => {
    status = { ...status, enabled: false };
    await create();
    expect((fixture.nativeElement as HTMLElement).querySelector('h2')).toBeNull();
  });

  it('should save a trimmed key and forget it afterwards', async () => {
    await create();
    component.apiKey = '  AIzaSy-test-key  ';

    component.saveApiKey();

    expect(aiService.saveApiKey).toHaveBeenCalledWith(
      'AIzaSy-test-key',
      expect.any(Function),
      expect.any(Function),
    );
    expect(component.apiKey).toBe('');
    expect(component.status?.apiKeyConfigured).toBe(true);
    expect(snackbarService.showSuccessMessage).toHaveBeenCalledWith('ai.key_saved');
  });

  it('should not save an empty key', async () => {
    await create();
    component.apiKey = '   ';

    component.saveApiKey();

    expect(aiService.saveApiKey).not.toHaveBeenCalled();
  });

  it('should delete the key', async () => {
    status = saved;
    await create();

    component.deleteApiKey();

    expect(component.status?.apiKeyConfigured).toBe(false);
    expect(snackbarService.showSuccessMessage).toHaveBeenCalledWith('ai.key_deleted');
  });
});
