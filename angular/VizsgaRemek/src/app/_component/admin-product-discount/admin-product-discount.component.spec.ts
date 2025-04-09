import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminProductDiscountComponent } from './admin-product-discount.component';

describe('AdminProductDiscountComponent', () => {
  let component: AdminProductDiscountComponent;
  let fixture: ComponentFixture<AdminProductDiscountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminProductDiscountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminProductDiscountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
