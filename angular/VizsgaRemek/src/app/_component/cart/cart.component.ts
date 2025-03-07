import { Component, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FooterComponent } from '../footer/footer.component';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductListComponent } from "../product-list/product-list.component";

@Component({
  selector: 'app-cart',
  imports: [FormsModule, FooterComponent, NavBarComponent, ProductListComponent],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css',
  encapsulation: ViewEncapsulation.None
})
export class CartComponent {
  totalAmount: number = 500;  // Deklaráljuk a teljes összeget, amit a kosárból kaphatsz
  address: string = '';  // Alapértelmezett üres string
  paymentMethod: string = 'credit';  // Alapértelmezett érték, például 'credit' vagy 'paypal'
  name: string = '';

  products = [
    { name: 'Termék 1', price: 100, quantity: 2 },
    { name: 'Termék 2', price: 200, quantity: 1 },
    { name: 'Termék 3', price: 50, quantity: 3 }
  ];

  checkout() {
    // Ellenőrizzük, hogy a cím meg van-e adva
    if (!this.name || !this.address) {
      alert('Kérjük, adja meg a nevet és a szállítási címet!');
      return;
    }

    alert(`Rendelés véglegesítve! Név: ${this.name}, Szállítási cím: ${this.address}, Fizetési mód: ${this.paymentMethod}`);
  }
}

