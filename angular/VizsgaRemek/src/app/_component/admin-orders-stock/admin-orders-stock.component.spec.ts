import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminOrdersStockComponent } from './admin-orders-stock.component';

describe('AdminOrdersStockComponent', () => {
  let component: AdminOrdersStockComponent;
  let fixture: ComponentFixture<AdminOrdersStockComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminOrdersStockComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminOrdersStockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
