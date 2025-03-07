import { Component } from '@angular/core';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { FooterComponent } from '../footer/footer.component';
import { FormsModule } from '@angular/forms';
import { BlogBodyComponent } from '../blog-body/blog-body.component';

@Component({
  selector: 'app-blog',
  imports: [NavBarComponent, FooterComponent, BlogBodyComponent, FormsModule],
  templateUrl: './blog.component.html',
  styleUrl: './blog.component.css'
})
export class BlogComponent {

}
