import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';


@Component({
  selector: 'app-shop-all',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './shop-all.component.html',
  styleUrl: './shop-all.component.css'
})
export class ShopAllComponent implements OnInit {
  toggleState = true;
  products: Product[] = [];
  loading = true;
  error: string | null = null;
  sortField: 'name' | 'price' | 'category' = 'name';
  sortDirection: 'asc' | 'desc' = 'asc';
  filterText = '';
  selectedCategory = '';
  categories: string[] = [];
  currentPage = 1;
  productPerPage = 10;
  productPerPageinList = 20;
  totalProducts = 0;
  defaultImage = 'http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/product/uploads/products/placeholderForProduct.jpg'


  constructor(private productService: ProductService, private route: ActivatedRoute, private cartService: CartService) { }


  ngOnInit(): void {
    this.productService.filterText$.subscribe(value => this.filterText = value);
    this.productService.selectedCategory$.subscribe(value => this.selectedCategory = value);
    this.totalProducts = this.products.length;

    this.route.queryParams.subscribe(params => {
      const category = params['category'];
      if (category) {
        this.selectedCategory = category;
      }
    });

    this.productService.getProducts().subscribe({
      next: (res) => {
        this.products = res.map(product => {
          if (product.slug) {
            const rawPath: string = product.slug;
            const fileName = rawPath;
            product.imageUrl = `http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/product/uploads/products/${fileName}.png`;
          }
          return product;
        });
        this.extractCategories();
        this.loading = false;
      },
      error: (err) => {
        console.error('Hiba:', err);
        this.error = 'Nem sikerült betölteni a termékeket.';
        this.loading = false;
      }
    });
  }


  get paginatedProducts() {
    const start = (this.currentPage - 1) * this.productPerPage;
    const end = start + this.productPerPage;
    return this.sortedProducts.slice(start, end);
  }

  get paginatedProductsInList() {
    const start = (this.currentPage - 1) * this.productPerPageinList;
    const end = start + this.productPerPageinList;
    return this.sortedProductsinList.slice(start, end);
  }

  get totalPages(): number {
    return Math.ceil(this.sortedProducts.length / this.productPerPage);
  }

  get totalPagesInList(): number {
    return Math.ceil(this.sortedProductsinList.length / this.productPerPageinList);
  }

  get fullPrice(): number {
    return this.products.reduce((total, product) => {
      const price = product.price || 0;
      const discountPrice = product.discountPrice || 0;
      return total + Math.ceil(price - discountPrice);
    }, 0);
  }

  toggle(): void {
    if (this.toggleState) {
      this.toggleState = false;
    } else {
      this.toggleState = true;
    }
  }

  get filteredProducts(): Product[] {
    let filtered = this.products;

    if (this.filterText) {
      filtered = filtered.filter(p =>
        p.name.toLowerCase().includes(this.filterText.toLowerCase())
      );
    }

    if (this.selectedCategory) {
      filtered = filtered.filter(p =>
        p.category.categoryName?.toLowerCase() === this.selectedCategory.toLowerCase()
      );
    }

    return filtered;
  }

  get sortedProducts(): Product[] {
    return [...this.filteredProducts].sort((a, b) => {
      const valA = (a[this.sortField] ?? '').toString().toLowerCase();
      const valB = (b[this.sortField] ?? '').toString().toLowerCase();

      if (this.sortDirection === 'asc') return valA > valB ? 1 : -1;
      else return valA < valB ? 1 : -1;
    });
  }

  get sortedProductsinList(): Product[] {
    return [...this.filteredProducts].sort((a, b) => {
      const valA = (a[this.sortField] ?? '').toString().toLowerCase();
      const valB = (b[this.sortField] ?? '').toString().toLowerCase();

      if (this.sortDirection === 'asc') return valA > valB ? 1 : -1;
      else return valA < valB ? 1 : -1;
    });
  }

  sortBy(field: 'name' | 'price' | 'category') {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
  }

  extractCategories(): void {
    const categorySet = new Set<string>();
    for (const product of this.products) {
      const name = product.category?.categoryName?.trim();
      if (name) categorySet.add(name);
    }
    this.categories = Array.from(categorySet).sort();
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