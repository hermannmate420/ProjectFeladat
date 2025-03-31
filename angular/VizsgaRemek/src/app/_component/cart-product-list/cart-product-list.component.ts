import { Component, Input, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cart-product-list',
  imports: [FormsModule, CommonModule],
  templateUrl: './cart-product-list.component.html',
  styleUrl: './cart-product-list.component.css'
})
export class CartProductListComponent {
  @Input() products: any[] = [];
  @Input() shippingFee: number = 1000;

  // Nettó összeg számítása
  get netTotal(): number {
    return this.products.reduce((total, product) => total + (product.price * product.quantity), 0);
  }

  // Áfa 27% és bruttó összeg számítása
  get grossTotal(): number {
    return this.netTotal * 1.27;
  }

  // Teljes összeg
  get totalAmount(): number {
    return this.grossTotal + this.shippingFee;
  }

  // Termékek mennyiségének módosítása
  increaseQuantity(product: any) {
    product.quantity++;
  }

  decreaseQuantity(product: any) {
    if (product.quantity > 1) {
      product.quantity--;
    }
  }

  // Termék törlése
  removeProduct(product: any) {
    const index = this.products.indexOf(product);
    if (index !== -1) {
      this.products.splice(index, 1);
    }
  }
}
