import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cart-body',
  standalone: true,  // Ha standalone módon van használva
  imports: [CommonModule],  // Importálni kell a CommonModule-t
  templateUrl: './cart-body.component.html',
  styleUrl: './cart-body.component.css'
})
export class CartBodyComponent {
  @Input() products: any[] = [];  // Termékek listája a szülő komponenstől
  @Input() shippingFee: number = 1000;  // Szállítási díj

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
