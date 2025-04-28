import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user';
  private shippingUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/shippingAddress';

  constructor(private http: HttpClient) { }

  getUserById(userId: number): Observable<any> {
    const token = sessionStorage.getItem('token') || '';
    const headers = new HttpHeaders({
      'token': token
    });

    return this.http.get(`${this.apiUrl}/getUserById?userId=${userId}`, { headers });
  }

  uploadProfilePicture(userId: number, file: File): Observable<any> {

    const formData = new FormData();
    formData.append('profile_picture', file);

    return this.http.post(`${this.apiUrl}/${userId}/upload-profile-picture`, formData);
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/registerUser`, userData);
  }

  registerAdmin(adminData: any, headers: any): Observable<any> {
    return this.http.post(
      'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/registerAdmin',
      adminData,
      { headers }
    );
  }

  getAllUsers(): Observable<any> {
    const token = sessionStorage.getItem('token');
    if (!token) {
      throw new Error('Nincs token, kérlek jelentkezz be!');
    }

    const headers = new HttpHeaders({
      'token': token
    });

    return this.http.get('http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/getAllUser', { headers });
  }

  updateUser(editorId: number, editedUserId: number, user: any): Observable<any> {
    const cleanUser = {
      username: user.username,
      firstname: user.firstName,
      lastname: user.lastName,
      email: user.email,
      phoneNumber: user.phoneNumber,
      isAdmin: user.isAdmin
    };

    return this.http.put(
      `http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/${editorId}/update/${editedUserId}`,
      cleanUser,
      {
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );
  }

  deleteUserLogically(userId: number, token: string) {
    const headers = {
      'Content-Type': 'application/json',
      'token': token
    };

    return this.http.put(
      `http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/deleteUser/${userId}`,
      {},
      { headers }
    );
  }

  reactivateUser(userId: number, token: string) {
    const headers = {
      'Content-Type': 'application/json',
      token: token
    };

    const url = `http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/reactivateUser/${userId}`;
    return this.http.put(url, {}, { headers });
  }

  reactivateUserById(userId: number) {
    const token = sessionStorage.getItem('token');
    if (!token) {
      throw new Error('Nincs token, kérlek jelentkezz be!');
    }
    const headers = {
      'Content-Type': 'application/json',
      'token': token
    };

    const url = `http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/reactivate-user?userId=${userId}`;
    return this.http.put(url, {}, { headers });
  }

  getAllTicket(): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });

    return this.http.get('http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/ticket/getAllTickets', { headers });
  }

  deleteTicket(ticketId: number): Observable<any> {
    const token = sessionStorage.getItem('token') || '';
    const headers = new HttpHeaders({
      'token': token
    });

    return this.http.delete(`http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/ticket/deleteTicket/?ticketId=${ticketId}`, { headers });
  }

  createTicket(userId: number, body: string): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    const ticket = {
      userId: userId,
      ticketBody: body,
      status: 'Open',
    };

    return this.http.post('http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/ticket/addTicket', ticket, { headers });
  }

  addShippingAddress(address: any) {
    const userId = sessionStorage.getItem('id') || '';
    const token = sessionStorage.getItem('token') || '';
    const headers = new HttpHeaders({
      'token': token
    });

    address = {
      userId: userId,
      recipientName: address.recipientName,
      addressLine1: address.addressLine1,
      addressLine2: address.addressLine2,
      city: address.city,
      state: address.state,
      postalCode: address.postalCode,
      country: address.country,
      phone: address.phone
    }

    return this.http.post<any>(this.shippingUrl + '/addShippingAddress', address, { headers });
  }

  getShippingAddress(userId: number) {
    const token = sessionStorage.getItem('token') || '';
    const headers = new HttpHeaders({
      'token': token
    });

    return this.http.get(this.shippingUrl + '/getShippingAddressByUserId?userId=' + userId, { headers })
  }

  updateShippingAddress(addressData: any) {
    const userId = sessionStorage.getItem('id') || '';
    const token = sessionStorage.getItem('token') || '';
    const headers = new HttpHeaders({
      'token': token
    });

    addressData = {
      userId: addressData.userId,
      shippingAddressId: addressData.shippingAddressId,
      recipientName: addressData.recipientName,
      addressLine1: addressData.addressLine1,
      addressLine2: addressData.addressLine2,
      city: addressData.city,
      state: addressData.state,
      postalCode: addressData.postalCode,
      country: addressData.country,
      phone: addressData.phone
    }

    return this.http.put<any>(this.shippingUrl + '/updateShippingAddress', addressData, { headers });
  }


}
