import { Component, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FooterComponent } from '../footer/footer.component';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { ProductListComponent } from "../product-list/product-list.component";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cart',
  imports: [FormsModule, FooterComponent, NavBarComponent, ProductListComponent, CommonModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class CartComponent {
  totalAmount: number = 500;  
  address: string = '';  
  paymentMethod: string = '';  // Fizetési mód
  name: string = '';
  
  // Kártya adatok
  cardNumber: string = '';
  expiryDate: string = '';
  cvv: string = '';

  products = [
    { name: 'Termék 1', price: 100, quantity: 2 },
    { name: 'Termék 2', price: 200, quantity: 1 },
    { name: 'Termék 3', price: 50, quantity: 3 }
  ];

  checkout() {
    if (!this.isFormValid()) {
      alert('Kérjük, töltse ki az összes szükséges mezőt!');
      return;
    }

    alert(`Rendelés véglegesítve! Név: ${this.name}, Szállítási cím: ${this.address}, Fizetési mód: ${this.paymentMethod}`);
  }

  isFormValid(): boolean {
    // Ha nincs név, cím vagy fizetési mód, akkor nem engedélyezzük a gombot
    if (!this.name || !this.address || !this.paymentMethod) {
      return false;
    }

    // Ha a fizetési mód hitelkártya, akkor ellenőrizzük a kártyaadatokat
    if (this.paymentMethod === 'credit') {
      if (!this.cardNumber || !this.expiryDate || !this.cvv) {
        return false;
      }
    }

    return true; // Minden feltétel teljesült, a gomb aktív lehet
  }
}
