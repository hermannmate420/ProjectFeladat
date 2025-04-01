import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user';

  constructor(private http: HttpClient) { }

  getUserById(userId: number): Observable<any> {
    const token = localStorage.getItem('token') || '';
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
    const token = localStorage.getItem('token');
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


}
