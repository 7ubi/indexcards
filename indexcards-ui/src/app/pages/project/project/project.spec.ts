import { ComponentFixture, TestBed } from '@angular/core/testing';

import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { vi } from 'vitest';

import { Project } from './project';
import HttpService from '../../../service/http/http.service';
import { ProjectResponse } from '../../../app.responses';

describe('Project', () => {
  let component: Project;
  let fixture: ComponentFixture<Project>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Project],
    }).compileComponents();

    fixture = TestBed.createComponent(Project);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

describe('Project AI entry point', () => {
  async function render(enabled: boolean, archived: boolean): Promise<HTMLElement> {
    const project: ProjectResponse = {
      id: 1,
      name: 'P',
      examDate: null,
      archived,
      indexCardResponses: [],
    };
    TestBed.resetTestingModule();
    await TestBed.configureTestingModule({
      imports: [Project],
      providers: [
        provideRouter([]),
        provideTranslateService(),
        // Answers asynchronously like real HTTP calls, which never answer during ngOnInit.
        {
          provide: HttpService,
          useValue: {
            get: vi.fn((url: string, cb: (response: unknown) => void) =>
              Promise.resolve().then(() => cb(url === '/api/ai/status' ? { enabled } : project)),
            ),
          },
        },
      ],
    }).compileComponents();
    const fixture = TestBed.createComponent(Project);
    fixture.detectChanges();
    await fixture.whenStable();
    return fixture.nativeElement as HTMLElement;
  }

  it('should show the AI button when the feature is enabled', async () => {
    expect((await render(true, false)).querySelector('[data-testid="ai-generate"]')).not.toBeNull();
  });

  it('should hide the AI button when the feature is disabled', async () => {
    expect((await render(false, false)).querySelector('[data-testid="ai-generate"]')).toBeNull();
  });

  it('should hide the AI button for archived projects', async () => {
    expect((await render(true, true)).querySelector('[data-testid="ai-generate"]')).toBeNull();
  });
});
