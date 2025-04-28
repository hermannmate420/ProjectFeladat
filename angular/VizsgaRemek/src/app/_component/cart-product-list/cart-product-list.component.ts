import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-cart-product-list',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './cart-product-list.component.html',
  styleUrl: './cart-product-list.component.css'
})
export class CartProductListComponent implements OnInit {
  cartItems: any[] = [];
  shippingFee: number = 1000;
  @Output() checkoutRequested = new EventEmitter<void>();

  // ÚJ ÁLLAPOT: hogy épp a kosár rész vagy a kifizetés rész látszik
  currentStep: 'cart' | 'payout' = 'cart';

  constructor(private cartService: CartService) { }

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    const userId = Number(sessionStorage.getItem('id'));
    if (!userId) {
      console.error("❌ User ID not found in sessionStorage!");
      return;
    }
    this.cartService.getCartByUserId(userId).subscribe({
      next: (cart) => {
        this.cartItems = cart.cartItemsCollection || [];
        console.log('✅ Cart loaded successfully:', this.cartItems);
      },
      error: (err) => {
        console.error('❌ Failed to load cart:', err);
      }
    });
  }

  get netTotal(): number {
    return this.cartItems.reduce((total, item) => {
      const price = item.productId.price || 0;
      const discount = item.productId.discountPrice || 0;
      return total + ((price - discount) * item.quantity);
    }, 0);
  }

  get grossTotal(): number {
    return this.netTotal * 1.27;
  }

  get totalAmount(): number {
    return this.grossTotal + this.shippingFee;
  }

  decreaseQuantity(item: any): void {
    if (item.quantity > 1) {
      item.quantity--;
    }
  }

  increaseQuantity(item: any): void {
    item.quantity++;
  }

  removeProduct(item: any): void {
    // implementáld a törlést itt (pl. service-ből hívva a backend DELETE-et)
    console.log('Remove product:', item);
  }

  clearCart(): void {
    this.cartItems = [];
  }

  continueToCheckout(): void {
    this.currentStep = 'payout'; // Átváltunk a kifizetés részre
  }

  goBackToCart(): void {
    this.currentStep = 'cart'; // Vissza a kosár nézetre
  }

  submitOrder(): void {
    this.cartService.chackoutCart().subscribe({
      next: (response) => {
        console.log('✅ Order submitted successfully:', response);
        this.checkoutRequested.emit();
      },
      error: (error) => {
        console.error('❌ Failed to submit order:', error);
      }
    });
  }
}