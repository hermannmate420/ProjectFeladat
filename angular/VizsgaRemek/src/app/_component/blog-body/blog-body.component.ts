import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-blog-body',
  imports: [FormsModule, CommonModule],
  templateUrl: './blog-body.component.html',
  styleUrl: './blog-body.component.css'
})
export class BlogBodyComponent {
  newPostContent: string = '';
  selectedImage: string | null = null;
  searchTerm: string = '';
  sortOrder: string = 'latest';
  replyInputVisible: { [key: number]: boolean } = {};

  posts: Post[] = [];

  constructor() {
    const savedPosts = localStorage.getItem('likedPosts');
    if (savedPosts) {
      this.posts = JSON.parse(savedPosts).map((post: any) => ({
        ...post,
        createdAt: new Date(post.createdAt),
        comments: post.comments?.map((comment: any) => ({
          ...comment,
          createdAt: new Date(comment.createdAt),
          replies: comment.replies?.map((reply: any) => ({
            ...reply,
            createdAt: new Date(reply.createdAt)
          })) || []
        })) || []
      }));
    } else {
      this.posts = [
        {
          id: 1,
          author: 'Kata',
          content: 'Sziasztok! Ez az első poszt a blogon. 😊',
          createdAt: new Date('2025-03-30T10:00:00'),
          comments: [
            {
              id: 101,
              author: 'Bence',
              content: 'Nagyon jó lett! Várom a folytatást.',
              createdAt: new Date('2025-03-30T10:15:00'),
              likes: 2,
              replies: []
            }
          ],
          image: null,
          likes: 5
        },
        {
          id: 2,
          author: 'Luca',
          content: 'Valaki tud egy jó kávézót ajánlani Pesten? ☕',
          createdAt: new Date('2025-04-01T09:45:00'),
          comments: [],
          image: null,
          likes: 3
        }
      ];
      this.savePostsToLocalStorage();
    }
  }

  savePostsToLocalStorage(): void {
    localStorage.setItem('likedPosts', JSON.stringify(this.posts));
  }

  get filteredPosts(): Post[] {
    let posts = [...this.posts];

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      posts = posts.filter(p =>
        p.content.toLowerCase().includes(term) ||
        p.author.toLowerCase().includes(term)
      );
    }

    switch (this.sortOrder) {
      case 'oldest':
        posts.sort((a, b) => a.createdAt.getTime() - b.createdAt.getTime());
        break;
      case 'mostComments':
        posts.sort((a, b) => b.comments.length - a.comments.length);
        break;
      default:
        posts.sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime());
        break;
    }

    return posts;
  }

  addPost(): void {
    if (this.newPostContent.trim()) {
      const newPost: Post = {
        id: Date.now(),
        author: 'Anonymous',
        content: this.newPostContent,
        createdAt: new Date(),
        comments: [],
        image: this.selectedImage,
        likes: 0
      };
      this.posts.unshift(newPost);
      this.newPostContent = '';
      this.selectedImage = null;
      this.savePostsToLocalStorage();
    }
  }

  addComment(postId: number, commentText: string): void {
    const post = this.posts.find(p => p.id === postId);
    if (post && commentText.trim()) {
      const newComment: Comment = {
        id: Date.now(),
        author: 'Anonymous',
        content: commentText,
        createdAt: new Date(),
        likes: 0,
        replies: []
      };
      post.comments.push(newComment);
      this.savePostsToLocalStorage();
    }
  }

  addReply(postId: number, parentCommentId: number, replyText: string): void {
    const post = this.posts.find(p => p.id === postId);
    const parent = post?.comments.find(c => c.id === parentCommentId);
    if (parent && replyText.trim()) {
      const newReply: Comment = {
        id: Date.now(),
        author: 'Anonymous',
        content: replyText,
        createdAt: new Date(),
        likes: 0,
        replies: []
      };
      parent.replies.push(newReply);
      this.replyInputVisible[parentCommentId] = false;
      this.savePostsToLocalStorage();
    }
  }

  toggleReplyInput(commentId: number): void {
    this.replyInputVisible[commentId] = !this.replyInputVisible[commentId];
  }

  toggleEditPost(post: Post): void {
    post.editing = !post.editing;
    this.savePostsToLocalStorage();
  }

  toggleEditComment(comment: Comment): void {
    comment.editing = !comment.editing;
    this.savePostsToLocalStorage();
  }

  deletePost(postId: number): void {
    this.posts = this.posts.filter(post => post.id !== postId);
    this.savePostsToLocalStorage();
  }

  deleteComment(postId: number, commentId: number): void {
    const post = this.posts.find(p => p.id === postId);
    if (post) {
      post.comments = post.comments.filter(comment => comment.id !== commentId);
      this.savePostsToLocalStorage();
    }
  }

  likePost(postId: number): void {
    const post = this.posts.find(p => p.id === postId);
    if (post) {
      post.likes = (post.likes || 0) + 1;
      this.savePostsToLocalStorage();
    }
  }

  likeComment(postId: number, commentId: number): void {
    const post = this.posts.find(p => p.id === postId);
    const comment = post?.comments.find(c => c.id === commentId);
    if (comment) {
      comment.likes = (comment.likes || 0) + 1;
      this.savePostsToLocalStorage();
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.selectedImage = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }
}

interface Post {
  id: number;
  author: string;
  content: string;
  createdAt: Date;
  comments: Comment[];
  image?: string | null;
  likes?: number;
  editing?: boolean;
}

interface Comment {
  id: number;
  author: string;
  content: string;
  createdAt: Date;
  likes?: number;
  replies: Comment[];
  editing?: boolean;
}