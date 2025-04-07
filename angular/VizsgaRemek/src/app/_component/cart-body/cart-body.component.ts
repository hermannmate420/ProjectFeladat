import { Component, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cart-body',
  imports: [FormsModule, CommonModule],
  templateUrl: './cart-body.component.html',
  styleUrl: './cart-body.component.css'
})
export class CartBodyComponent {
  cartItems: any[] = [];
  shippingFee: number = 1000;

  
}
