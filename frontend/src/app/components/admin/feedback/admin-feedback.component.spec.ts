import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminFeedbackComponent } from './admin-feedback.component';

describe('FeedbackComponent', () => {
  let component: AdminFeedbackComponent;
  let fixture: ComponentFixture<AdminFeedbackComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminFeedbackComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminFeedbackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
