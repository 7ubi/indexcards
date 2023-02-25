import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateIndexcardComponent } from './create-indexcard.component';

describe('CreateIndexcardComponent', () => {
  let component: CreateIndexcardComponent;
  let fixture: ComponentFixture<CreateIndexcardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateIndexcardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateIndexcardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
