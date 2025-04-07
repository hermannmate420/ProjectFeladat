import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-blog-editor',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './blog-editor.component.html',
  styleUrl: './blog-editor.component.css'
})
export class BlogEditorComponent implements OnInit {
  blogForm!: FormGroup;
  showEditor = false;
  successMessage = '';
  errorMessage = '';
  selectedImageFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;
  http: any;

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    this.blogForm = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
      image: [''],
      productId: ['']
    });
  }

  toggleEditor(): void {
    this.showEditor = !this.showEditor;
  }

  onSubmit(): void {
    if (this.blogForm.valid) {
      const formData = new FormData();
      formData.append('title', this.blogForm.value.title);
      formData.append('content', this.blogForm.value.content);
      if (this.blogForm.value.productId) {
        formData.append('productId', this.blogForm.value.productId);
      }
      if (this.selectedImageFile) {
        formData.append('image', this.selectedImageFile);
      }

      // ✅ Backend POST (mock URL-t használtam, cseréld a sajátodra)
      this.http.post('http://127.0.0.1:8080/api/blog', formData).subscribe({
        next: (res: any) => {
          this.successMessage = '✅ Blog post sikeresen elmentve!';
          this.errorMessage = '';
          this.blogForm.reset();
          this.selectedImageFile = null;
          this.imagePreview = null;
          this.showEditor = false;
        },
        error: (err: any) => {
          console.error('❌ Hiba:', err);
          this.successMessage = '';
          this.errorMessage = 'Hiba a mentés közben.';
        }
      });
    } else {
      this.successMessage = '';
      this.errorMessage = '❌ Kérlek tölts ki minden kötelező mezőt!';
    }
  }


  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedImageFile = input.files[0];

      // 🖼️ Előnézet generálása
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result;
      };
      reader.readAsDataURL(this.selectedImageFile);
    }
  }
}
