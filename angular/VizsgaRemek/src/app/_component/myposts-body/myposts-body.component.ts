import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BlogPost } from '../../models/blog.model';
import { BlogService } from '../../services/blog.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-myposts-body',
  imports: [FormsModule, CommonModule],
  templateUrl: './myposts-body.component.html',
  styleUrl: './myposts-body.component.css'
})
export class MypostsBodyComponent implements OnInit {
  posts: BlogPost[] = [];
  selectedPost: BlogPost | null = null;
  data: BlogPost | null = null;
  editing = false;
  editedPost: Partial<BlogPost> = {};
  currentUserId = Number(sessionStorage.getItem('id'));
  sortOrder: 'latest' | 'oldest' | undefined;

  constructor(private blogService: BlogService) { }

    ngOnInit(): void {
      this.blogService.getAllPosts().subscribe({
        next: (res) => {
          this.posts = res
            .filter(post => post.authorId === this.currentUserId)
            .map(post => {
              const rawPath: string = post.imageUrl;
              const fileName = rawPath;
              return {
                ...post,
                imagesUrl: `http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/blog/uploads/blogs/${fileName}`
              };
            });
        },
        error: (err) => console.error('❌ Failed to load posts:', err)
      });
  }
  get filteredAndSortedPosts() {
    return [...this.posts]
      .sort((a, b) => {
        if (this.sortOrder === 'latest') {
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        } else if (this.sortOrder === 'oldest') {
          return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
        }
        return 0;
      });
  }

  openOverlay(post: BlogPost): void {
    this.selectedPost = post;
    this.editing = false;
    this.editedPost = { ...post };
  }

  closeOverlay(): void {
    this.selectedPost = null;
    this.editing = false;
  }

  enableEdit(): void {
    this.editing = true;
  }

  saveEdit(): void {
    if (!this.selectedPost || !this.editedPost) return;

    const updatedPost: BlogPost = {
      ...this.selectedPost,
      ...this.editedPost
    };

    this.blogService.updatePost(updatedPost).subscribe({
      next: () => {
        const index = this.posts.findIndex(p => p.blogId === updatedPost.blogId);
        if (index !== -1) {
          this.posts[index] = updatedPost;
        }

        this.selectedPost = updatedPost;
        this.editedPost = {};
        this.editing = false;
      },
      error: err => {
        console.error('❌ Failed to update post:', err);
      }
    });
  }

}