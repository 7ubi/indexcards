import { TestBed } from '@angular/core/testing';
import { vi } from 'vitest';

import { AiService } from './ai.service';
import HttpService from '../http/http.service';

describe('AiService', () => {
  let service: AiService;
  const httpService = {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    postFormData: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();
    TestBed.configureTestingModule({
      providers: [{ provide: HttpService, useValue: httpService }],
    });
    service = TestBed.inject(AiService);
  });

  it('should load the status', () => {
    const callback = vi.fn();
    service.getStatus(callback);
    expect(httpService.get).toHaveBeenCalledWith('/api/ai/status', callback, undefined);
  });

  it('should save and delete the api key', () => {
    const callback = vi.fn();
    service.saveApiKey('AIzaSy-key', callback);
    expect(httpService.put).toHaveBeenCalledWith(
      '/api/ai/apiKey',
      { apiKey: 'AIzaSy-key' },
      callback,
      undefined,
    );

    service.deleteApiKey(callback);
    expect(httpService.delete).toHaveBeenCalledWith('/api/ai/apiKey', callback, undefined);
  });

  it('should send notes, count and pdf as form data', () => {
    const file = new File(['%PDF-1.7'], 'notes.pdf', { type: 'application/pdf' });

    service.generate('7', 'my notes', 15, file, vi.fn());

    const [url, formData] = httpService.postFormData.mock.calls[0];
    expect(url).toBe('/api/ai/generate');
    expect(formData.get('projectId')).toBe('7');
    expect(formData.get('notes')).toBe('my notes');
    expect(formData.get('cardCount')).toBe('15');
    expect(formData.get('file')).toBeInstanceOf(File);
  });

  it('should omit the file when none is selected', () => {
    service.generate('7', 'my notes', 10, undefined, vi.fn());

    const formData: FormData = httpService.postFormData.mock.calls[0][1];
    expect(formData.has('file')).toBe(false);
  });

  it('should save selected cards in bulk', () => {
    const callback = vi.fn();
    service.saveCards('7', [{ question: 'Q', answer: 'A' }], callback);
    expect(httpService.post).toHaveBeenCalledWith(
      '/api/indexCard/bulk',
      { projectId: '7', cards: [{ question: 'Q', answer: 'A' }] },
      callback,
      undefined,
    );
  });
});
