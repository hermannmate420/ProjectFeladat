import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BlogEditorComponent } from "../blog-editor/blog-editor.component";
import { BlogFeedComponent } from "../blog-feed/blog-feed.component";

@Component({
  selector: 'app-blog-body',
  imports: [FormsModule, CommonModule, BlogEditorComponent, BlogFeedComponent],
  templateUrl: './blog-body.component.html',
  styleUrl: './blog-body.component.css'
})
export class BlogBodyComponent {
  
}