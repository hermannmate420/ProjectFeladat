import { CommonModule, DOCUMENT } from '@angular/common';
import { Component, HostListener, Inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { BlogService } from '../../services/blog.service';

@Component({
  selector: 'app-home-body',
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './home-body.component.html',
  styleUrl: './home-body.component.css'
})
export class HomeBodyComponent implements OnInit {
  windowScrolled: boolean = false;
  products: Product[] = [];
  chunkedProducts: Product[][] = [];
  defaultImage = 'http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/product/uploads/products/placeholderForProduct.jpg';
  latestBlogPost: any = null;


  constructor(@Inject(DOCUMENT) private document: Document, private productService: ProductService, private blogService: BlogService) { }

  @HostListener("window:scroll", [])

  onWindowScroll() {
    if (window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop > 100) {
      this.windowScrolled = true;
    }
    else if (this.windowScrolled && window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop < 10) {
      this.windowScrolled = false;
    }
  }
  scrollToTop() {
    (function smoothscroll() {
      var currentScroll = document.documentElement.scrollTop || document.body.scrollTop;
      if (currentScroll > 0) {
        window.requestAnimationFrame(smoothscroll);
        window.scrollTo(0, 0);
      }
    })();
  }
  
  ngOnInit(): void {
    this.productService.getProducts().subscribe({
      next: (res) => {
        const allProducts = res.map(product => {
          if (product.slug) {
            const fileName = product.slug;
            product.imageUrl = `http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/product/uploads/products/${fileName}.png`;
          }
          return product;
        });
        this.products = allProducts.filter(p => 
          p.category?.categoryName?.toLowerCase() === 'bögrék'
        );
  
        this.chunkedProducts = this.chunkArray(this.products.slice(3, 12), 3);
      },
      error: (err) => {
        console.error('Hiba a termékek betöltésekor:', err);
      }
    });

    this.loadLatestBlogPost();
  }
  

  private chunkArray(arr: Product[], size: number): Product[][] {
    const result: Product[][] = [];
    for (let i = 0; i < arr.length; i += size) {
      result.push(arr.slice(i, i + size));
    }
    return result;
  }

    loadLatestBlogPost(): void {
      this.blogService.getAllPosts().subscribe({
        next: (posts) => {
          if (posts.length > 0) {
            const sortedPosts = posts.sort((a: any, b: any) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
            this.latestBlogPost = sortedPosts[0];
            console.log("✅ Latest blog post loaded:", this.latestBlogPost);
          }
        },
        error: (err) => {
          console.error('❌ Failed to load latest blog post:', err);
        }
      });
  }
}
