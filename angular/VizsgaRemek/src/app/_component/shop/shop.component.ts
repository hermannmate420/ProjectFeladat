import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavBarComponent } from "../nav-bar/nav-bar.component";
import { FooterComponent } from "../footer/footer.component";
import { ShopBodyComponent } from "../shop-body/shop-body.component";

@Component({
  selector: 'app-shop',
  imports: [FormsModule, NavBarComponent, FooterComponent, ShopBodyComponent],
  templateUrl: './shop.component.html',
  styleUrl: './shop.component.css'
})
export class ShopComponent {

}
