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
}
