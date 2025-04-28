import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-cart-body',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './cart-body.component.html',
  styleUrl: './cart-body.component.css'
})
export class CartBodyComponent implements OnInit {
  cartItems: any[] = [];
  shippingFee: number = 1000;
  useProfileAddress: boolean = false;

  shippingAddress: any = {
    recipientName: '',
    addressLine1: '',
    addressLine2: '',
    city: '',
    state: '',
    postalCode: '',
    country: '',
    phone: ''
  };

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    const userId = Number(sessionStorage.getItem('id'));
    if (userId) {
      this.userService.getShippingAddress(userId).subscribe({
        next: (data) => {
          this.prefilledProfileAddress = data;
        },
        error: () => {
          console.warn('⚠️ Nem sikerült betölteni a felhasználó címét.');
        }
      });
    }
  }

  prefilledProfileAddress: any = {};

  prefillAddress(): void {
    if (this.useProfileAddress && this.prefilledProfileAddress) {
      this.shippingAddress = { ...this.prefilledProfileAddress };
    } else {
      // mezők resetelése
      this.shippingAddress = {
        recipient_name: '',
        address_line1: '',
        address_line2: '',
        city: '',
        state: '',
        postal_code: '',
        country: '',
        phone: ''
      };
    }
  }

  isShippingAddressValid(): boolean {
    const a = this.shippingAddress;
    return a.recipientName && a.addressLine1 && a.city && a.postalCode && a.country && a.phone;
  }


}
