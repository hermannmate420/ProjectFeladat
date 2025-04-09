import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
    return this.http.get<ProductApiResponse>(`${this.baseUrl}/getAllProducts`).pipe(
      map((response: { result: any; }) => response.result),
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

  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(`${this.baseUrl}/createProduct`, product).pipe(
      catchError((error) => {
        console.error('Termék létrehozása sikertelen:', error);
        return throwError(() => new Error('Hiba történt a termék létrehozásakor.'));
      })
    );
  }

  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.baseUrl}/updateProduct`, product).pipe(
      catchError((error) => {
        console.error('Termék frissítése sikertelen:', error);
        return throwError(() => new Error('Hiba történt a termék frissítésekor.'));
      })
    );
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/deleteProduct/?id=${id}`).pipe(
      catchError((error) => {
        console.error('Termék törlése sikertelen:', error);
        return throwError(() => new Error('Hiba történt a termék törlésekor.'));
      })
    );
  }
}
