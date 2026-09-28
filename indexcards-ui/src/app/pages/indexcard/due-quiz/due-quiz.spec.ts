import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DueQuiz } from './due-quiz';

describe('DueQuiz', () => {
  let component: DueQuiz;
  let fixture: ComponentFixture<DueQuiz>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DueQuiz],
    }).compileComponents();

    fixture = TestBed.createComponent(DueQuiz);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
