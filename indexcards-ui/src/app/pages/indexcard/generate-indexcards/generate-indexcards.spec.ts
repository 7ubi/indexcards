import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap, provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { GenerateIndexcards } from './generate-indexcards';
import { AiService } from '../../../service/ai/ai.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { AiStatusResponse, GeneratedCardsResponse } from '../../../app.responses';

describe('GenerateIndexcards', () => {
  let component: GenerateIndexcards;
  let fixture: ComponentFixture<GenerateIndexcards>;
  let status: AiStatusResponse;
  let navigate: ReturnType<typeof vi.spyOn>;

  const aiService = {
    getStatus: vi.fn((callback: (s: AiStatusResponse) => void) => callback(status)),
    generate: vi.fn(
      (
        _id: string,
        _notes: string,
        _count: number,
        _file: File | undefined,
        callback: (r: GeneratedCardsResponse) => void,
      ) =>
        callback({
          cards: [
            { question: 'Q1', answer: 'A1' },
            { question: 'Q2', answer: 'A2' },
          ],
        }),
    ),
    saveCards: vi.fn((_id: string, _cards: unknown, callback: () => void) => callback()),
  };
  const snackbarService = { showSuccessMessage: vi.fn(), showErrorMessage: vi.fn() };

  async function create() {
    await TestBed.configureTestingModule({
      imports: [GenerateIndexcards],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        { provide: AiService, useValue: aiService },
        { provide: SnackbarService, useValue: snackbarService },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '7' }) } },
        },
      ],
    }).compileComponents();
    navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    fixture = TestBed.createComponent(GenerateIndexcards);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  }

  beforeEach(() => {
    vi.clearAllMocks();
    status = {
      enabled: true,
      apiKeyConfigured: true,
      apiKeyHint: 'AbCd',
      model: 'gemini-3.8-flash',
      maxCards: 30,
      maxNotesChars: 20000,
      maxPdfBytes: 10485760,
      maxPdfPages: 20,
    };
  });

  it('should not allow generating without input', async () => {
    await create();
    expect(component.canGenerate()).toBe(false);
    component.notes = 'Mitochondria produce ATP.';
    expect(component.canGenerate()).toBe(true);
  });

  it('should ask for an api key when none is saved', async () => {
    status = { ...status, apiKeyConfigured: false, apiKeyHint: null };
    await create();
    component.notes = 'notes';

    expect(component.canGenerate()).toBe(false);
    expect(
      (fixture.nativeElement as HTMLElement).querySelector('[data-testid="ai-no-key"]'),
    ).not.toBeNull();
  });

  it('should show all suggestions selected after generating', async () => {
    await create();
    component.notes = 'notes';

    component.generate();

    expect(aiService.generate).toHaveBeenCalledWith(
      '7',
      'notes',
      10,
      undefined,
      expect.any(Function),
      expect.any(Function),
    );
    expect(component.step).toBe('review');
    expect(component.selectedCount()).toBe(2);
  });

  it('should save only selected, edited cards', async () => {
    await create();
    component.notes = 'notes';
    component.generate();
    component.cards[0].question = '  Edited Q1  ';
    component.cards[1].selected = false;

    component.save();

    expect(aiService.saveCards).toHaveBeenCalledWith(
      '7',
      [{ question: 'Edited Q1', answer: 'A1' }],
      expect.any(Function),
      expect.any(Function),
    );
    expect(navigate).toHaveBeenCalledWith(['/project', '7']);
  });

  it('should not save when nothing is selected or a selected card is empty', async () => {
    await create();
    component.notes = 'notes';
    component.generate();

    component.setAllSelected(false);
    expect(component.canSave()).toBe(false);

    component.setAllSelected(true);
    component.cards[0].answer = '   ';
    expect(component.canSave()).toBe(false);
  });

  it('should keep the input when going back', async () => {
    await create();
    component.notes = 'notes';
    component.generate();

    component.backToInput();

    expect(component.step).toBe('input');
    expect(component.notes).toBe('notes');
  });

  it('should only accept pdf files', async () => {
    await create();
    const input = document.createElement('input');
    Object.defineProperty(input, 'files', {
      value: [new File(['x'], 'notes.txt', { type: 'text/plain' })],
    });

    component.onFileSelected({ target: input } as unknown as Event);

    expect(component.file).toBeUndefined();
    expect(snackbarService.showErrorMessage).toHaveBeenCalledWith('ai.pdf_only');
  });

  it('should leave the page when the feature is disabled', async () => {
    status = { ...status, enabled: false };
    await create();
    expect(navigate).toHaveBeenCalledWith(['/project', '7']);
  });
});
