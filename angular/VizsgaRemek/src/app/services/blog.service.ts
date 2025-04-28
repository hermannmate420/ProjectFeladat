import { Injectable } from '@angular/core';
import { BlogPost } from '../models/blog.model';
import { catchError, map, Observable, of, switchMap, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})
export class BlogService {
  private baseUrl = 'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/blog';

  constructor(private http: HttpClient) { }

  getAllPosts(): Observable<BlogPost[]> {
    return this.http.get<any>(`${this.baseUrl}/getAll`).pipe(
      map(res => {
        const result = res.result;
        if (Array.isArray(result)) {
          return result;
        } else if (typeof result === 'object' && result !== null) {
          return [result];
        } else {
          return [];
        }
      }),
      catchError((error) => {
        console.error('❌ Failed to fetch blog posts:', error);
        return throwError(() => new Error('Hiba a blogbejegyzések lekérésekor.'));
      })
    );
  }
  getPostById(id: number): Observable<BlogPost> {
    return this.http.get<BlogPost>(`${this.baseUrl}/getById?id=${id}`).pipe(
      catchError((error) => {
        console.error('❌ Failed to fetch post:', error);
        return throwError(() => new Error('Nem sikerült lekérni a bejegyzést.'));
      })
    );
  }

  createPost(post: Partial<BlogPost>): Observable<any> {
    const postData = {
      title: post.title,
      content: post.content,
      author: post.author,
      authorId: post.authorId,
      likesCount: 0,
      postCommentId: 0,
      imageUrl: post.imageUrl ?? '',
    };



    return this.http.post(`${this.baseUrl}/create`, postData).pipe(
      switchMap((response: any) => {
        if (response.blogId && post.image) {
          return this.uploadBlogImageObservable(post.image, response.blogId).pipe(
            map(() => response)
          );
        }
        console.log('📬 Feltöltött blog válasz:', response);
        return of(response);
      }),
      catchError((error) => {
        console.error('❌ Blog létrehozása sikertelen:', error);
        return throwError(() => new Error('Nem sikerült létrehozni a blogot.'));
      })
    );
  }



  uploadBlogImageObservable(file: File, blogId: number): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    console.log(`📸 Feltöltés elindítva: blogId=${blogId}, fájl neve=${file.name}`);
    return this.http.post(`${this.baseUrl}/${blogId}/uploadImage`, formData);
  }


  deletePost(blogId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete?blogId=` + blogId).pipe(
      catchError((error) => {
        console.error('❌ Blog törlése sikertelen:', error);
        return throwError(() => new Error('Nem sikerült törölni a blogot.'));
      })
    );
  }

  likePost(id: number): Observable<any> {
    const body = {
      blogId: id
    }
    return this.http.put(`${this.baseUrl}/like`, body).pipe(
      catchError((error) => {
        console.error('❌ Lájk sikertelen:', error);
        return throwError(() => new Error('Nem sikerült lájkolni a bejegyzést.'));
      })
    );
  }

  updatePost(data: Partial<BlogPost>): Observable<any> {
    const updateData = {
      blogId: data.blogId,
      title: data.title,
      content: data.content,
      author: data.author,
      imageUrl: data.imageUrl,
      authorId: data.authorId,
      image: data.image
    };

    const obs = this.http.put(`${this.baseUrl}/update`, updateData, {
      headers: { 'Content-Type': 'application/json' }
    });

    // ha van új kép fájl
    if (data.image && data.blogId) {
      this.uploadBlogImage(data.image, data.blogId);
    }

    return obs;
  }


  uploadBlogImage(file: File, blogId: number): void {
    const formData = new FormData();
    formData.append('file', file); // A kulcs neve ('file') egyezzen a szerver elvárásával
    console.log('FormData:', formData);
    console.log('URL:', `${this.baseUrl}/${blogId}/uploadImage`);

    this.http.post(`${this.baseUrl}/${blogId}/uploadImage`, formData).subscribe({
      next: () => alert('📸 Kép sikeresen feltöltve!'),
      error: (err) => {
        console.error('❌ Hiba a kép feltöltésekor:', err);
        alert('Hiba történt a kép feltöltésekor!');
      }
    });
  }

}


