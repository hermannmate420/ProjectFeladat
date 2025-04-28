import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, map, throwError } from 'rxjs';
import { Product } from '../models/product.model';

interface ProductApiResponse {
  result: Product[];
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private baseUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/product';
  filterText$ = new BehaviorSubject<string>('');
  selectedCategory$ = new BehaviorSubject<string>('');
  inStockOnly$ = new BehaviorSubject<boolean>(false);

  constructor(private http: HttpClient) { }

  getProducts(): Observable<Product[]> {
    return this.http.get<{ products: Product[] }>(`${this.baseUrl}/getAllProducts`).pipe(
      map((response) => response.products),
      catchError((error) => {
        console.error('Termékek betöltése sikertelen:', error);
        return throwError(() => new Error('Hiba történt a termékek lekérésekor.'));
      })
    );
  }


  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/getProduct/?id=${id}`).pipe(
      catchError((error) => {
        console.error('Termék lekérése sikertelen:', error);
        return throwError(() => new Error('Hiba történt a termék lekérésekor.'));
      })
    );
  }

  createProduct(product: any): Observable<any> {
    const token = sessionStorage.getItem('token');
    if (!token) {
      throw new Error('Nincs token, kérlek jelentkezz be!');
    }

    const headers = new HttpHeaders({
      'token': token
    });

    const formData = new FormData();
    formData.append('name', product.name);
    formData.append('description', product.description);
    formData.append('price', product.price);
    formData.append('discount_price', product.discount_price || 0);
    formData.append('stockQuanty', product.stockQuanty);
    formData.append('status', product.status || 'active');
    formData.append('categoryId', product.categoryId);
    if (product.image_url) {
      formData.append('imageUrl', product.image_url);
    }

    return this.http.post(`${this.baseUrl}/addProduct`, formData, { headers }).pipe(
      catchError((error) => {
        console.error('❌ Termék létrehozása sikertelen:', error);
        return throwError(() => new Error('Hiba történt a termék létrehozásakor.'));
      })
    );
  }


  updateProduct(product: any): Observable<Product> {
    const token = sessionStorage.getItem('token');
    if (!token) {
      throw new Error('Nincs token, kérlek jelentkezz be!');
    }

    const headers = {
      'Content-Type': 'application/json',
      'token': token
    };

    console.log("✅ Küldött payload:", product);

    return this.http.put<Product>(`${this.baseUrl}/updateProduct`, product, { headers }).pipe(
      catchError((error) => {
        console.error('Termék frissítése sikertelen:', error);
        return throwError(() => new Error('Hiba történt a termék frissítésekor.'));
      })
    );
  }



  activetaProduct(product: any): Observable<Product> {
    const token = sessionStorage.getItem('token');
    if (!token) {
      throw new Error('Nincs token, kérlek jelentkezz be!');
    }
    const headers = {
      'Content-Type': 'application/json',
      'token': token
    };
    product =
    {
      id: product.id,
      name: product.name,
      description: product.description,
      price: product.price,
      discount_price: product.discount_price,
      stockQuanty: product.stockQuanty,
      status: "active",
      imageUrl: product.imageUrl,
      category: {
        id: product.category.categoryId
      }


    }
    return this.http.put<Product>(`${this.baseUrl}/updateProduct`, product, { headers }).pipe(
      catchError((error) => {
        console.error('Termék aktiválása sikertelen:', error);
        return throwError(() => new Error('Hiba történt a termék aktiválásánál.'));
      })
    );
  }

  getAllCategories(): Observable<any[]> {
    return this.http.get<{ result: any[] }>('http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/category/getAll')
      .pipe(
        map(res => res.result),
        catchError(err => {
          console.error("Kategóriák lekérése sikertelen", err);
          return throwError(() => new Error('Hiba a kategóriák lekérése közben'));
        })
      );
  }

}
