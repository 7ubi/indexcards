import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideTranslateService } from '@ngx-translate/core';

import { Privacy } from './privacy';

describe('Privacy', () => {
  let component: Privacy;
  let fixture: ComponentFixture<Privacy>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Privacy],
      providers: [provideTranslateService()],
    }).compileComponents();

    fixture = TestBed.createComponent(Privacy);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render a heading for every section', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelectorAll('section h2').length).toBe(component.sections.length);
  });

  it('should only return string arrays from toLines', () => {
    expect(component.toLines(['a', 'b'])).toEqual(['a', 'b']);
    expect(component.toLines('privacy.sections.missing.items')).toEqual([]);
    expect(component.toLines(undefined)).toEqual([]);
  });
});
