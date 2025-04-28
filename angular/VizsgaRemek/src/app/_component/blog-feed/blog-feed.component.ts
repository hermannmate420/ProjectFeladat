import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BlogService } from '../../services/blog.service';
import { CommentService } from '../../services/comment.service';
import { BlogWithComments, BlogPost } from '../../models/blog.model';
import { Comment } from '../../models/comment.model';

@Component({
  selector: 'app-blog-feed',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './blog-feed.component.html',
  styleUrl: './blog-feed.component.css'
})
export class BlogFeedComponent implements OnInit {
  posts: BlogWithComments[] = [];
  replyInputVisible: { [commentId: number]: boolean } = {};
  commentInputs: { [postId: number]: string } = {};
  isEdit = false;
  searchTerm = '';
  sortOrder: 'latest' | 'oldest' | 'mostComments' = 'latest';
  currentUserId: number = Number(sessionStorage.getItem('id'));
  selectedPost: BlogPost | null = null;
  editing = false;
  editedPost: Partial<BlogPost> = {};
  toastMessage = '';
  showToast = false;
  toastColorClass = 'bg-success';


  constructor(
    private blogService: BlogService,
    private commentService: CommentService
  ) { }

  ngOnInit(): void {
    this.loadPostsWithComments();
  }

  get filteredAndSortedPosts() {
    return [...this.posts]
      .filter(post =>
        post.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        post.content.toLowerCase().includes(this.searchTerm.toLowerCase())
      )
      .sort((a, b) => {
        if (this.sortOrder === 'latest') {
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        } else if (this.sortOrder === 'oldest') {
          return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
        } else if (this.sortOrder === 'mostComments') {
          return (b.comments?.length || 0) - (a.comments?.length || 0);
        }
        return 0;
      });
  }

  loadPostsWithComments(): void {
    this.blogService.getAllPosts().subscribe({
      next: (res) => {
        const posts: BlogWithComments[] = res.map(post => {
          const rawPath: string = post.imageUrl;
          const fileName = rawPath;
          return {
            ...post,
            comments: [],
            editing: false,
            imagesUrl: `http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/blog/uploads/blogs/${fileName}`
          };
        });

        console.log("📥 Full blog list with comments INIT:", posts);
        posts.forEach(post => {
          this.commentService.getCommentsByBlogId(post.blogId).subscribe({
            next: (comments) => {
              console.log(`💬 Comments for blogId ${post.blogId}:`, comments);
              post.comments = comments;
            },
            error: err => console.error(`❌ Error loading comments for blogId ${post.blogId}:`, err)
          });
        });

        this.posts = posts;
      },
      error: err => {
        console.error('❌ Failed to load blog posts:', err);
      }
    });
  }


  likePost(postId: number): void {
    this.blogService.likePost(postId).subscribe({
      next: () => {
        const post = this.posts.find(p => p.blogId === postId);
        if (post) {
          post.likesCount++;
          this.showSuccessToast('👍 Lájk sikeresen hozzáadva!');
        }
      },
      error: (err) => {
        console.error('❌ Lájkolás sikertelen:', err);
        this.showErrorToast('❌ Hiba történt a lájkolás során.');
      }
    });
  }


  toggleEditPost(post: BlogWithComments): void {
    if (post.authorId === this.currentUserId) {
      post.editing = !post.editing;
    }
  }


  deletePost(postId: number): void {
    this.posts = this.posts.filter(p => p.blogId !== postId);
  }

  addComment(postId: number): void {
    const content = this.commentInputs[postId]?.trim();
    if (!content) return;

    const post = this.posts.find(p => p.blogId === postId);
    if (!post) return;

    const newComment: Partial<Comment> = {
      blogId: postId,
      commentText: content,
      commenter: 'You'
    };

    this.commentService.addComment(newComment).subscribe({
      next: (createdComment) => {
        post.comments.push(createdComment);
        this.commentInputs[postId] = '';
      },
      error: (err) => {
        console.error('❌ Failed to add comment:', err);
      }
    });
  }

  deleteComment(postId: number, commentId: number): void {
    const post = this.posts.find(p => p.blogId === postId);
    if (!post) return;

    this.commentService.deleteComment(commentId).subscribe({
      next: () => {
        post.comments = post.comments.filter((c: Comment) => c.commentId !== commentId);
      },
      error: (err) => {
        console.error('❌ Failed to delete comment:', err);
      }
    });
  }


  toggleReplyInput(commentId: number): void {
    this.replyInputVisible[commentId] = !this.replyInputVisible[commentId];
  }

  likeComment(postId: number, commentId: number): void {
    const post = this.posts.find(p => p.blogId === postId);
    const comment = post?.comments.find((c: Comment) => c.commentId === commentId);
    if (comment) {
      (comment as any).likes = ((comment as any).likes || 0) + 1;
    }
  }

  toggleEditComment(comment: any): void {
    comment.editing = !comment.editing;
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

  saveEdit(post: BlogWithComments): void {
    if (post.authorId !== this.currentUserId) return;

    this.blogService.updatePost(post).subscribe({
      next: () => {
        this.showSuccessToast('✔️ Bejegyzés sikeresen frissítve!');
        post.editing = false;
      },
      error: err => {
        console.error('❌ Failed to update post:', err);
        this.showErrorToast('❌ Hiba történt a mentés során!');
      }
    });
  }

  showSuccessToast(message: string): void {
    this.toastMessage = message;
    this.toastColorClass = 'bg-success';
    this.showToast = true;

    setTimeout(() => this.showToast = false, 2500);
  }

  showErrorToast(message: string): void {
    this.toastMessage = message;
    this.toastColorClass = 'bg-danger';
    this.showToast = true;

    setTimeout(() => this.showToast = false, 2500);
  }

  onFileSelected(event: any, post: BlogWithComments): void {
    const file: File = event.target.files[0];
    if (file && file.type.startsWith('image/')) {
      post.image = file;
    } else {
      alert('❌ Csak kép fájl tölthető fel.');
    }
  }

  deletpost(postId: number): void {
    this.blogService.deletePost(postId).subscribe({
      next: () => {
        alert(`Product "${postId}" now inactive.`)
        location.reload();
      },
      error: err => alert('Failed to delet post.')
    });
  }

}

function c(value: Comment, index: number, obj: Comment[]): value is Comment {
  throw new Error('Function not implemented.');
}

