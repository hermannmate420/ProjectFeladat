import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { FooterComponent } from '../footer/footer.component';
import { MypostsBodyComponent } from '../myposts-body/myposts-body.component';

@Component({
  selector: 'app-myposts',
  imports: [FormsModule, FooterComponent, NavBarComponent, MypostsBodyComponent],
  templateUrl: './myposts.component.html',
  styleUrl: './myposts.component.css'
})
export class MypostsComponent {

}
