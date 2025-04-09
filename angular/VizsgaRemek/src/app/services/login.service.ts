import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, catchError, Observable, throwError } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private authenticatedSubject: BehaviorSubject<boolean>;
  public authenticated$: Observable<boolean>;
  private url = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user';

  constructor(@Inject(PLATFORM_ID) private platformId: object, private http: HttpClient) {
    const isBrowser = isPlatformBrowser(this.platformId);
    this.authenticatedSubject = new BehaviorSubject<boolean>(isBrowser ? !!localStorage.getItem('token') : false);
    this.authenticated$ = this.authenticatedSubject.asObservable();
  }

  //goole login start

  async googleLogin(idToken: string): Promise<any> {
    try {
      const response = await fetch(`${this.url}/googleLogin`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ token: idToken })
      });
  
      if (!response.ok) {
        throw new Error('Google login failed');
      }
  
      if (isPlatformBrowser(this.platformId)) {
        console.log('A függvény böngészőben fut');
      const data = await response.json();
      localStorage.setItem('token', data.result.jwt);
      localStorage.setItem('id', data.result.id);
      this.authenticatedSubject.next(true);
      return data;
      }
    } catch (error) {
      console.error('Google login error:', error);
      throw error;
    }
  }
  


  //google login end

  async login(email: string, password: string): Promise<any> {
    const loginCreds = { email, password };

    try {
      const response = await fetch(`${this.url}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(loginCreds)
      });

      if (!response.ok) {
        const error = await response.text();
        throw new Error(`Error occurred: ${response.status} - ${error}`);
      }

      const data = await response.json();
      console.log('User data with JWT token: ', data);
      
      if (isPlatformBrowser(this.platformId)) {
        localStorage.setItem('token', data.result.jwt);
        localStorage.setItem('id', data.result.id);
        this.authenticatedSubject.next(true);
      }
      return data;
    } catch (error) {
      console.error('Error logging in: ', error);
      throw error;
    }
  }

  logOut(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('token');
      localStorage.removeItem('id');

      this.authenticatedSubject.next(false);
    }
  }

  private tokenDecoder(token: string): any {
    try {
      return jwtDecode(token);
    } catch (error) {
      console.error('Error trying to decode JWT token: ', error);
      return null;
    }
  }

  getUserId(): number | null {
    if (!isPlatformBrowser(this.platformId)) return null;
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No token found!');
      return null;
    }
    
    const JWTToken = this.tokenDecoder(token);
    return JWTToken?.id ? parseInt(JWTToken.id, 10) : null;
  }

  // Lekéri a felhasználó adatait az ID alapján
  getUserById(userId: number): Observable<any> {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No token found!');
      return throwError(() => new Error('Unauthorized: No token found'));
    }

    const headers = new HttpHeaders({
      Authorization: `${token}`
    });

    return this.http.get(`${this.url}/getUserById?userId=${userId}`, { headers }).pipe(
      catchError(err => {
        console.error('Error in getUserById:', err);
        return throwError(() => new Error('Error fetching user data'));
      })
    );
  }

  getIsAdmin(): boolean {
    if (!isPlatformBrowser(this.platformId)) return false;
    const token = localStorage.getItem('token');
    if (!token) return false;
    
    const JWTToken = this.tokenDecoder(token);
    return JWTToken?.isAdmin === true;
  }
}
