import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators
} from '@angular/forms';
import { BlogService } from '../../services/blog.service';
import { BlogPost } from '../../models/blog.model';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-blog-editor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './blog-editor.component.html',
  styleUrls: ['./blog-editor.component.css']
})
export class BlogEditorComponent implements OnInit {
  blogForm!: FormGroup;
  showEditor = false;
  successMessage = '';
  errorMessage = '';
  selectedImageFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;
  currentUserName = '';
  currentUserId: number = Number(sessionStorage.getItem('id'));
  isEdit = false;
  loggedInStatus = false;

  constructor(
    private fb: FormBuilder,
    private blogService: BlogService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.blogForm = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
      productId: [''],
      image: [null],
    });

    this.userService.getUserById(this.currentUserId).subscribe({
      next: (res) => {
        this.currentUserName = res?.result?.username || 'Unknown';
      },
      error: (err) => {
        console.error('❌ Nem sikerült lekérni a felhasználót:', err);
      }
    });
    this.checkLoggedIn();
  }

  checkLoggedIn(): void {
    this.loggedInStatus = !!this.currentUserId;
  }

  toggleEditor(): void {
    this.showEditor = !this.showEditor;
  }

  onSubmit(): void {
    if (this.blogForm.valid) {
      const newPost: Partial<BlogPost> = {
        title: this.blogForm.value.title,
        content: this.blogForm.value.content,
        author: this.currentUserName,
        authorId: this.currentUserId,
        image: this.blogForm.value.image,
        likesCount: 0,
        postCommentId: 0,
        ...(this.blogForm.value.productId && { productId: Number(this.blogForm.value.productId) })
      };

      this.createBlog(newPost);
    } else {
      this.successMessage = '';
      this.errorMessage = '❌ Kérlek tölts ki minden kötelező mezőt!';
    }
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file && file.type.startsWith('image/')) {
      this.selectedImageFile = file;
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreview = e.target?.result ?? null;
      };
      reader.readAsDataURL(file);
    } else {
      alert('❌ Csak kép fájl tölthető fel.');
    }
  }

  createBlog(post: Partial<BlogPost>): void {
    if (this.selectedImageFile) {
      post.image = this.selectedImageFile;
    }

    this.blogService.createPost(post).subscribe({
      next: () => {
        this.successMessage = '✅ Blog post sikeresen elmentve!';
        this.errorMessage = '';
        this.blogForm.reset();
        this.imagePreview = null;
        this.selectedImageFile = null;
        this.showEditor = false;
      },
      error: (err) => {
        console.error('❌ Hiba mentés közben:', err);
        this.successMessage = '';
        this.errorMessage = '❌ Hiba történt a mentés során.';
      }
    });
  }


}