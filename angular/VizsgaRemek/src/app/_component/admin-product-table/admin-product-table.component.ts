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
  inactiveProducts = 0;
  deletedProducts = 0;
  showToast: boolean = false;
  showOverlay: boolean = true;
  newProduct: any = [];
  categories: { id: number, name: string }[] = [];



  constructor(private productService: ProductService, private titleService: Title) {
    titleService.setTitle("Admin | Product Table");
  }

  ngOnInit(): void {
    this.loadProducts();
    this.loadCategories();

  }

  onSelectProduct(): void {
    const found = this.products.find(p => p.id === +this.selectedProductId);
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
        (this.statusFilter === 'inactive' && p.isDeleted) ||
        (this.statusFilter === 'archived' && p.isDeleted);

      return searchMatch && statusMatch;
    });
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (res) => {
        this.products = res;
        this.filteredProducts = [...this.products];
        this.totalProducts = this.products.length;
        this.activeProducts = this.products.filter(p => p.status === 'active').length;
        this.inactiveProducts = this.products.filter(p => p.status === 'inactive').length;
        this.deletedProducts = this.products.filter(p => p.status === 'archived').length;
        console.log("products: ", this.products)
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
    } else if (this.statusFilter === 'inactive') {
      productsToExport = productsToExport.filter(p => p.status === 'inactive' || p.isDeleted);
    } else if (this.statusFilter === 'archived') {
      productsToExport = productsToExport.filter(p => p.status === 'archived' || p.isDeleted);
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
      `${product.name} ${product.description} ${product.category?.categoryName}`.toLowerCase().includes(term)
    );
    this.currentPage = 1;
  }

  updateProduct(product: any): void {
    if (!product.category?.id && product.category?.categoryName) {
      const matched = this.categories.find(c => c.name === product.category.categoryName);
      if (matched) {
        product.category = { id: matched.name || matched.id };
      }
    }

    if (!product.category?.id) {
      alert("❗ Nincs érvényes kategória kiválasztva!");
      return;
    }

    this.productService.updateProduct(product).subscribe({
      next: () => {
        alert(`✅ Product "${product.name}" updated successfully.`);
        this.loadProducts();
      },
      error: err => {
        console.error("❌ Hiba a frissítés közben:", err);
        alert('❌ Failed to update product.');
      }
    });
  }




  toggleStatus(product: any): void {
    product.status = product.status === 'active' ? 'inactive' : 'active';
    this.updateProduct(product);
  }

  saveProduct(product: any): void {
    const payload = {
      productId: product.id,
      name: product.name,
      description: product.description,
      price: product.price,
      discountPrice: product.discountPrice ?? 0, // ha undefined, akkor 0 lesz
      stockQuanty: product.stockQuanty,
      status: product.status,
      imageUrl: product.imageUrl || '',
      category: {
        id: product.category?.id || product.category?.categoryId
      }
    };

    console.log("✅ Küldendő payload:", payload);

    this.productService.updateProduct(payload).subscribe({
      next: () => {
        alert(`✅ Product "${product.name}" updated successfully.`);
        this.loadProducts();
      },
      error: err => {
        console.error('❌ Failed to update product:', err);
        alert('❌ Hiba történt a frissítéskor!');
      }
    });
  }


  deleteProduct(product: any): void {
    if (product.category?.id) {
      product.categoryId = product.category.id;
    }

    this.productService.updateProduct(product).subscribe({
      next: () => {
        alert(`Product "${product.name}" now inactive.`),
          product = {
            status: product.stats
          }
      },
      error: err => alert('Failed to update product.')
    });
  }

  activateProduct(product: any): void {
    if (product.category?.id) {
      product.categoryId = product.category.id;
    }

    this.productService.activetaProduct(product).subscribe({
      next: () => {
        alert(`Product "${product.name}" now inactive.`),
          product = {
            status: product.stats
          }
        this.loadProducts();
      },
      error: err => alert('Failed to update product.')
    });
  }

  createNewProduct(newProduct: any): void {
    this.productService.createProduct(newProduct).subscribe({
      next: () => {
        alert(`✅ Termék sikeresen létrehozva: ${newProduct.name}`);
        this.loadProducts();

        // Form reset
        this.newProduct = {
          name: '',
          description: '',
          price: 0,
          discount_price: 0,
          stockQuanty: 0,
          categoryId: null,
          image_url: '',
          status: 'active',
        };
      },
      error: err => {
        console.error('❌ Termék létrehozása sikertelen:', err);
        alert('❌ Hiba történt a termék létrehozásakor!');
      }
    });
  }


  loadCategories(): void {
    this.productService.getAllCategories().subscribe({
      next: (cats) => {
        this.categories = cats;
        console.log("✅ Kategóriák betöltve:", this.categories);
      },
      error: (err) => {
        console.error('❌ Kategóriák betöltése sikertelen:', err);
      }
    });
  }


}
