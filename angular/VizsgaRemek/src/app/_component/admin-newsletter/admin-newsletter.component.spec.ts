import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminNewsletterComponent } from './admin-newsletter.component';

describe('AdminNewsletterComponent', () => {
  let component: AdminNewsletterComponent;
  let fixture: ComponentFixture<AdminNewsletterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminNewsletterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminNewsletterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
