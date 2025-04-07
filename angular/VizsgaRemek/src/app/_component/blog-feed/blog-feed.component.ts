import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-blog-feed',
  imports: [FormsModule, CommonModule],
  templateUrl: './blog-feed.component.html',
  styleUrl: './blog-feed.component.css'
})
export class BlogFeedComponent implements OnInit {

  posts: any[] = [];

  replyInputVisible: { [commentId: number]: boolean } = {};

  constructor() { }

  ngOnInit(): void {
    // Ide jöhet majd a blogService.getPosts() ha lesz backend
    this.posts = [
      {
        id: 1,
        title: 'Welcome to our Blog!',
        content: 'We are happy to have you here.',
        image: '/retro_logo.png',
        author: 'Admin',
        createdAt: new Date(),
        likes: 0,
        editing: false,
        comments: []
      },
      {
        id: 2,
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
  }

  likePost(postId: number): void {
    const post = this.posts.find(p => p.id === postId);
    if (post) {
      post.likes = (post.likes || 0) + 1;
    }
  }

  toggleEditPost(post: any): void {
    post.editing = !post.editing;
  }

  deletePost(postId: number): void {
    this.posts = this.posts.filter(p => p.id !== postId);
  }

  addComment(postId: number, content: string): void {
    const post = this.posts.find(p => p.id === postId);
    if (post && content.trim()) {
      const comment = {
        id: Date.now(),
        author: 'You',
        content,
        likes: 0,
        editing: false,
        replies: []
      };
      post.comments.push(comment);
    }
  }

  toggleEditComment(comment: any): void {
    comment.editing = !comment.editing;
  }

  deleteComment(postId: number, commentId: number): void {
    const post = this.posts.find(p => p.id === postId);
    if (post) {
      post.comments = post.comments.filter((c: { id: number; }) => c.id !== commentId);
    }
  }

  likeComment(postId: number, commentId: number): void {
    const post = this.posts.find(p => p.id === postId);
    if (post) {
      const comment = post.comments.find((c: { id: number; }) => c.id === commentId) ||
        post.comments.flatMap((c: { replies: any; }) => c.replies).find((r: { id: number; }) => r.id === commentId);
      if (comment) {
        comment.likes = (comment.likes || 0) + 1;
      }
    }
  }

  toggleReplyInput(commentId: number): void {
    this.replyInputVisible[commentId] = !this.replyInputVisible[commentId];
  }

  addReply(postId: number, commentId: number, content: string): void {
    const post = this.posts.find(p => p.id === postId);
    if (post) {
      const comment = post.comments.find((c: { id: number; }) => c.id === commentId);
      if (comment && content.trim()) {
        const reply = {
          id: Date.now(),
          author: 'You',
          content,
          likes: 0
        };
        comment.replies.push(reply);
      }
    }
  }
}
