import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';


@Component({
  selector: 'app-shop-all',
  imports: [CommonModule, FormsModule,],
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
  totalProducts = 0;


  constructor(private productService: ProductService, private route: ActivatedRoute,) { }


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
        this.products = res;
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

  get totalPages(): number {
    return Math.ceil(this.sortedProducts.length / this.productPerPage);
  }

  toggle(): void {
    if (this.toggleState) {
      this.toggleState = false;
    } else {
      this.toggleState = true;
    }
  }

  addToCart(product: Product): void {
    const cart = JSON.parse(localStorage.getItem('cart') || '[]');
    const existingProduct = cart.find((item: Product) => item.id === product.id);
    if (existingProduct) {
      existingProduct.quantity += 1;
    } else {
      product.stockQuanty = 1;
      cart.push(product);
    }
    localStorage.setItem('cart', JSON.stringify(cart));
    console.log('Kosár:', cart);
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
        p.category.name?.toLowerCase() === this.selectedCategory.toLowerCase()
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
      const name = product.category?.name?.trim();
      if (name) categorySet.add(name);
    }
    this.categories = Array.from(categorySet).sort(); // ábécé sorrend
  }

}