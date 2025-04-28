import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartBaseUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/cart';
  private orderBaseUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/orders';

  constructor(private http: HttpClient) { }

  getCartByUserId(userId: number): Observable<any> {
    const token = sessionStorage.getItem('token');
    if (!token) {
      throw new Error('Nincs token! Kérlek jelentkezz be.');
    }


    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'token': token
    });

    return this.http.get(`${this.cartBaseUrl}/getCart?userId=${userId}`, { headers });
  }

  addToCart(productId: number, quantity: number): Observable<any> {
    const token = sessionStorage.getItem('token');
    if (!token) {
      throw new Error('Nincs token! Kérlek jelentkezz be.');
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'token': token
    });

    const userdata = sessionStorage.getItem('id');
    const payload = {
      userId: Number(userdata),
      items: [
        {
          productId: productId,
          quantity: quantity
        }
      ],
    };

    return this.http.post(`${this.cartBaseUrl}/addItem`, payload, { headers }).pipe(
      catchError(error => {
        console.error('❌ Hiba a kosárhoz adáskor:', error);
        return throwError(() => new Error('Sikertelen kosárhoz adás.'));
      })
    );
  }

  updateItemQuantity(productId: number, newQuantity: number): Observable<any> {
    const token = sessionStorage.getItem('token');
    if (!token) {
      throw new Error('Nincs token! Kérlek jelentkezz be.');
    }
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'token': token
    });
    return this.http.put(`${this.cartBaseUrl}/updateItemQuantity?productId=${productId}&newQuantity=${newQuantity}`, {}, { headers });
  }

  removeItemFromCart(productId: number): Observable<any> {
    const token = sessionStorage.getItem('token');
    if (!token) {
      throw new Error('Nincs token! Kérlek jelentkezz be.');
    }
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'token': token
    });
    return this.http.delete(`${this.cartBaseUrl}/removeItem?productId=${productId}`, { headers });
  }

  chackoutCart(): Observable<any> {
    const token = sessionStorage.getItem('token');
    if (!token) {
      console.error('❌ Token hiányzik! Kérlek jelentkezz be.');
      throw new Error('Nincs token! Kérlek jelentkezz be.');
    }
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'token': token
    });
    return this.http.post(`${this.orderBaseUrl}/checkout`, {}, { headers }).pipe(
      catchError(error => {
        console.error('❌ Hiba a rendelés leadásakor:', error);
        return throwError(() => new Error('Sikertelen rendelés leadás.'));
      })
    );
  }

  getAllOrders(): Observable<any> {
    return this.http.get(`${this.orderBaseUrl}/getAll`).pipe(
      catchError(error => {
        console.error('❌ Hiba a rendelések lekérdezésekor:', error);
        return throwError(() => new Error('Sikertelen rendeléslekérdezés.'));
      })
    );

  }
}