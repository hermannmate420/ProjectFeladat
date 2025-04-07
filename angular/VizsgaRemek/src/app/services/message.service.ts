import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Message } from '../models/message.model';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private baseUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/message';

  constructor(private http: HttpClient) { }

  getMessage(): Observable<any> {
      const headers = new HttpHeaders({
        'Content-Type': 'application/json',
      });
  
      return this.http.get('http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/message/getAll', { headers });
    }

  markAsRead(id: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/markAsRead/${id}`, {});
  }

  deleteMessage(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete/?messageId=${id}`);
  }
}
