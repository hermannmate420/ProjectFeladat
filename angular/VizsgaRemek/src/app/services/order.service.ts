import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private baseUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/order';

  constructor(private http: HttpClient) { }

  createOrder(orderData: any): Observable<any> {
    const token = sessionStorage.getItem('token');
    if (!token) {
      throw new Error('Nincs token! Jelentkezz be újra!');
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'token': token
    });

    return this.http.post(`${this.baseUrl}/checkout`, orderData, { headers }).pipe(
      catchError(error => {
        console.error('❌ Hiba az order létrehozásakor:', error);
        return throwError(() => new Error('Order létrehozása sikertelen.'));
      })
    );
  }
}   