import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cart-product-list',
  imports: [FormsModule, CommonModule],
  templateUrl: './cart-product-list.component.html',
  styleUrl: './cart-product-list.component.css'
})
export class CartProductListComponent implements OnInit {
  cartItems: any[] = [];
  shippingFee: number = 1000;

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    const items = localStorage.getItem('cart');
    this.cartItems = items ? JSON.parse(items) : [];
  }

  // Nettó összeg számítása
  get netTotal(): number {
    return this.cartItems.reduce((total, product) => total + (product.price * product.quantity), 0);
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
  increaseQuantity(cartItems: any) {
    cartItems.quantity++;
  }

  decreaseQuantity(cartItems: any) {
    if (cartItems.quantity > 1) {
      cartItems.quantity--;
    }
  }

  // Termék törlése
  removeProduct(product: any) {
    const index = this.cartItems.indexOf(product);
    if (index !== -1) {
      this.cartItems.splice(index, 1);
    }
  }
  
  clearCart(): void {
    localStorage.removeItem('cart');
    this.cartItems = [];
  }

  checkout(): void {
    alert('💳 Fizetési folyamat még nincs implementálva.');
  }
}
