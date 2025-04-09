import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';

@Component({
  selector: 'app-admin-product-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-product-table.component.html',
  styleUrls: ['./admin-product-table.component.css']
})
export class AdminProductTableComponent implements OnInit {
  products: any[] = [];
  selectedProductId: string = '';
  selectedProduct: any = null;
  statusFilter: string = 'all';
  filteredProducts: any[] = [];
  currentPage = 1;
  productsPerPage = 10;
  searchTerm = '';
  error = '';
  isEdit = false;
  totalProducts = 0;
  activeProducts = 0;
  deletedProducts = 0;
  showToast: boolean = false;
  showOverlay: boolean = true;
  newProduct: any = [];

  constructor(private productService: ProductService, private titleService: Title) {
    titleService.setTitle("Admin | Product Table");
  }

  ngOnInit(): void {
    this.loadProducts();
  }

  onSelectProduct(): void {
    const found = this.products.find(p => p.productId === +this.selectedProductId);
    this.selectedProduct = found ? { ...found } : null;
    this.showOverlay = true;
  }

  get filteredProduct() {
    return this.products.filter(p => {
      const searchMatch = `${p.name} ${p.price} ${p.category?.name || ''} ${p.productId}`
        .toLowerCase()
        .includes(this.searchTerm.toLowerCase());

      const statusMatch =
        this.statusFilter === 'all' ||
        (this.statusFilter === 'active' && !p.isDeleted) ||
        (this.statusFilter === 'deleted' && p.isDeleted);

      return searchMatch && statusMatch;
    });
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (res) => {
        const data = res || res;
        this.products = data;
        this.filteredProducts = [...this.products];
        this.totalProducts = this.products.length;
        this.activeProducts = this.products.filter(p => !p.isDeleted).length;
        this.deletedProducts = this.products.filter(p => p.isDeleted).length;
      },
      error: (err) => {
        console.error('Error loading products:', err);
        this.error = 'Failed to load product list';
      }
    });
  }

  exportProductsToCSV(): void {
    let productsToExport = [...this.filteredProducts];

    if (this.statusFilter === 'active') {
      productsToExport = productsToExport.filter(p => p.status === 'active');
    } else if (this.statusFilter === 'deleted') {
      productsToExport = productsToExport.filter(p => p.status === 'inactive' || p.isDeleted);
    }

    const headers = ['ID', 'Name', 'Price', 'Discount Price', 'Status', 'Stock', 'Category'];
    const rows = productsToExport.map(product => [
      product.productId,
      product.name,
      product.price,
      product.discount_price,
      product.status,
      product.stockQuanty,
      product.category?.name || 'N/A'
    ]);

    const csvContent = [headers, ...rows]
      .map(e => e.map(field => `"${(field + '').replace(/"/g, '""')}"`).join(','))
      .join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);

    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'products_export.csv');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }


  toggleEdit(): void {
    this.isEdit = !this.isEdit;
  }

  get paginatedProducts() {
    const start = (this.currentPage - 1) * this.productsPerPage;
    return this.filteredProducts.slice(start, start + this.productsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredProducts.length / this.productsPerPage);
  }

  onSearchChange(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredProducts = this.products.filter(product =>
      `${product.name} ${product.description} ${product.category?.name}`.toLowerCase().includes(term)
    );
    this.currentPage = 1;
  }

  updateProduct(product: any): void {
    this.productService.updateProduct(product.productId, product).subscribe({
      next: () => alert(`Product "${product.name}" updated successfully.`),
      error: err => alert('Failed to update product.')
    });
  }

  toggleStatus(product: any): void {
    product.status = product.status === 'active' ? 'inactive' : 'active';
    this.updateProduct(product);
  }

  saveProduct(product: any): void {
    if (this.selectedProduct) {
      this.productService.updateProduct(this.selectedProduct.productId, this.selectedProduct).subscribe({
        next: () => {
          alert(`Product "${this.selectedProduct.name}" updated successfully.`);
          this.loadProducts();
        },
        error: err => alert('Failed to update product.')
      });
    }
  }

  deleteProduct(product: any): void {
    this.productService.deleteProduct(product.productId).subscribe({
      next: () => {
        this.products = this.products.filter(p => p.productId !== product.productId);
        this.filteredProducts = this.filteredProducts.filter(p => p.productId !== product.productId);
        alert(`Product "${product.name}" deleted successfully.`);
      },
      error: err => alert('Failed to delete product.')
    });
  }

  createNewProduct(): void {
    const asd = this.newProduct
    this.productService.createProduct(this.newProduct).subscribe({
      next: (createdProduct) => {
        alert(`✅ Product "${createdProduct.name}" successfully created.`);
        this.newProduct = {
          productId: 0, // Default or placeholder value
          name: '',
          description: '',
          price: 0,
          discount_price: 0,
          stockQuanty: 0,
          categoryId: 0,
          productPicture: '', // Default or placeholder value
          status: 'active'
        };
        this.loadProducts(); // újratöltés
      },
      error: (err) => {
        console.error(err);
        alert('❌ Failed to create product.');
      }
    });
  }
}
