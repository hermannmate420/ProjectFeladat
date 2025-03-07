import { Component } from '@angular/core';
import { NavBarComponent } from "../nav-bar/nav-bar.component";
import { FooterComponent } from "../footer/footer.component";
import { FormsModule } from '@angular/forms';
import { ProfileBodyComponent } from '../profile-body/profile-body.component';
import { MypostsComponent } from "../myposts/myposts.component";
import { BlogComponent } from "../blog/blog.component";
import { MypostsBodyComponent } from "../myposts-body/myposts-body.component";

@Component({
  selector: 'app-profile',
  imports: [NavBarComponent, FooterComponent, ProfileBodyComponent, FormsModule, MypostsBodyComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {

}
