import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateIndexcard } from './create-indexcard';

describe('CreateIndexcard', () => {
  let component: CreateIndexcard;
  let fixture: ComponentFixture<CreateIndexcard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateIndexcard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateIndexcard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
