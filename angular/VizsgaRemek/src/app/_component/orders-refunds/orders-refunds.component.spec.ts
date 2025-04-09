import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrdersRefundsComponent } from './orders-refunds.component';

describe('OrdersRefundsComponent', () => {
  let component: OrdersRefundsComponent;
  let fixture: ComponentFixture<OrdersRefundsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrdersRefundsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrdersRefundsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
