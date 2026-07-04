import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuizStat } from './quiz-stat';

describe('QuizStat', () => {
  let component: QuizStat;
  let fixture: ComponentFixture<QuizStat>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuizStat]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuizStat);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
