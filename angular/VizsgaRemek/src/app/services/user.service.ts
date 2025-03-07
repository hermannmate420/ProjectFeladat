import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/getUserById';

  constructor(private http: HttpClient) {}
  
  getUserById(userId: number): Observable<any> {
    const token = localStorage.getItem('token') || '';
const headers = new HttpHeaders({
  'token': token
});


    return this.http.get(`${this.apiUrl}?userId=${userId}`, { headers });
  }
}
