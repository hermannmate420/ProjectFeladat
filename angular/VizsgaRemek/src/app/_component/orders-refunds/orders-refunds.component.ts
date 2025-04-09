import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FooterComponent } from "../footer/footer.component";
import { NavBarComponent } from "../nav-bar/nav-bar.component";
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-orders-refunds',
  imports: [FormsModule, FooterComponent, NavBarComponent, CommonModule, RouterModule],
  templateUrl: './orders-refunds.component.html',
  styleUrl: './orders-refunds.component.css'
})
export class OrdersRefundsComponent {

}
