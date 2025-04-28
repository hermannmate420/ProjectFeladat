import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavBarComponent } from "../nav-bar/nav-bar.component";
import { FooterComponent } from "../footer/footer.component";
import { Title } from '@angular/platform-browser';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-shop-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, NavBarComponent, FooterComponent],
  templateUrl: './shop-detail.component.html',
  styleUrl: './shop-detail.component.css'
})
export class ShopDetailComponent implements OnInit {
  product!: Product;
  relatedProducts: Product[] = [];
  quantity = 1;
  defaultImage = 'http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/product/uploads/products/placeholderForProduct.jpg';
  title = '';

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private titleService: Title,
    private cartService: CartService
  ) {
    titleService.setTitle('Shop');
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const productId = Number(params.get('id'));
      if (!productId) {
        console.error('❌ Nincs termék ID a query paraméterek között!');
        return;
      }

      this.loadProduct(productId);
    });
  }



  loadProduct(productId: number): void {
    this.productService.getProducts().subscribe({
      next: (products) => {
        const foundProduct = products.find(p => p.id === productId);
        if (!foundProduct) {
          console.error('❌ Termék nem található a listában!');
          return;
        }

        this.product = foundProduct;

        this.product.imageUrl = this.product.slug
          ? `http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/product/uploads/products/${this.product.slug}.png`
          : this.defaultImage;

        this.relatedProducts = products
          .filter(p =>
            p.category?.categoryName === this.product.category?.categoryName &&
            p.id !== this.product.id
          )
          .slice(0, 4)
          .map(p => ({
            ...p,
            imageUrl: p.slug
              ? `http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/product/uploads/products/${p.slug}.png`
              : this.defaultImage
          }));
      },
      error: (err) => {
        console.error('❌ Hiba a termékek lekérésekor:', err);
      }
    });
  }

  addToCart(product: Product): void {
    const quantity = 1;
    this.cartService.addToCart(product.id, quantity).subscribe({
      next: () => {
        alert('✅ Product added to cart successfully!');
      },
      error: (err) => {
        console.error('❌ Hiba kosárhoz adáskor:', err);
        alert('❌ Failed to add product to cart.');
      }
    });
  }

}
