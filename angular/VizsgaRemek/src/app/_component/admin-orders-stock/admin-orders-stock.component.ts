import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-admin-orders-stock',
  imports: [],
  templateUrl: './admin-orders-stock.component.html',
  styleUrl: './admin-orders-stock.component.css'
})
export class AdminOrdersStockComponent {
  constructor(private titleService: Title) {
    titleService.setTitle("Admin | Orders Stock");
  }
}
