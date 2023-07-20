import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IndexcardQuizStatComponent } from './indexcard-quiz-stat.component';

describe('IndexcardQuizStatComponent', () => {
  let component: IndexcardQuizStatComponent;
  let fixture: ComponentFixture<IndexcardQuizStatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IndexcardQuizStatComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IndexcardQuizStatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
