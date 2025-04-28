import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';
import { Comment } from '../models/comment.model';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/comment';

  constructor(private http: HttpClient) { }

  getAllComments(): Observable<Comment[]> {
    return this.http.get<{ result: Comment[] }>(`${this.apiUrl}/getAll`).pipe(
      map(response => response.result),
      catchError(err => {
        console.warn('⚠️ Nincs komment vagy hiba történt:', err);
        return of([]);
      })
    );
  }

    getCommentsByBlogId(blogId: number): Observable<Comment[]> {
      if (!blogId) {
          console.warn(`⚠️ Érvénytelen blogId: ${blogId}`);
          return of([]);
      }
  
      return this.http.get<{ result: Comment[] }>(`${this.apiUrl}/getByBlogId?blogId=${blogId}`).pipe(
          map(response => {
              if (!response.result || response.result.length === 0) {
                  console.warn(`⚠️ Nincsenek kommentek blogId=${blogId} esetén`);
                  return [];
              }
              return response.result;
          }),
          catchError(err => {
              console.warn(`⚠️ Hiba történt a kommentek lekérésekor blogId=${blogId} esetén`, err);
              return of([]);
          })
      );
  }

  addComment(comment: Partial<Comment>): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl}/create`, comment);
  }

  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete?commentId=${commentId}`);
  }

  updateComment(commentId: number, updated: Partial<Comment>): Observable<Comment> {
    return this.http.put<Comment>(`${this.apiUrl}/update?commentId=${commentId}`, updated);
  }
}