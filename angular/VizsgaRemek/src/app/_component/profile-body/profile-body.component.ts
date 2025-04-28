import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-profile-body',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile-body.component.html',
  styleUrl: './profile-body.component.css'
})
export class ProfileBodyComponent implements OnInit {
  isEditeProfile = false;
  isEditeAddress = true;
  userData: any;
  selectedFile: File | null = null;
  newPassword: string = '';
  confirmPassword: string = '';
  shippingAddress: any = {
    recipient_name: '',
    address_line1: '',
    address_line2: '',
    city: '',
    state: '',
    postal_code: '',
    country: '',
    phone: ''
  };



  constructor(private userService: UserService, private http: HttpClient) {

  }
  ngOnInit(): void {
    const userId = sessionStorage.getItem('id');
    if (userId) {
      this.getUserData(Number(userId));
    } else {
      console.error('User ID not found in sessionStorage');
    }
    this.getShippingAddress(Number(userId));

  }

  getShippingAddress(userId: number): void {
    this.userService.getShippingAddress(userId).subscribe({
      next: (data) => {
        if (data && data) {
          this.shippingAddress = data;
          console.log('📦 Cím betöltve:', this.shippingAddress);
        }
      },
      error: (err) => {
        console.error('❌ Hiba a cím betöltésekor:', err);
      }
    });
  }

  saveShippingAddress(): void {
    const userId = Number(sessionStorage.getItem('id'));
    if (!userId) {
      alert('User ID missing!');
      return;
    }

    const request = this.shippingAddress.shippingAddressId
      ? this.userService.updateShippingAddress(this.shippingAddress)
      : this.userService.addShippingAddress(this.shippingAddress);

    request.subscribe({
      next: () => {
        alert(this.shippingAddress.shippingAddressId ? '📦 Cím frissítve!' : '📦 Cím elmentve!');
        this.isEditeAddress = true;
        this.getShippingAddress(userId);
      },
      error: (err) => {
        console.error('❌ Cím mentési hiba:', err);
        alert('Hiba történt a cím mentése közben!');
      }
    });
  }



  editAddress(): void {
    if (this.isEditeAddress) {
      this.isEditeAddress = false;
    } else {
      this.isEditeAddress = true;
    }
  }

  editProfile(): void {
    if (this.isEditeProfile) {
      this.isEditeProfile = false;
    } else {
      this.isEditeProfile = true;
    }
  }

  getUserData(userId: number): void {
    this.userService.getUserById(userId).subscribe({
      next: (data) => {
        if (data?.result?.profile_picture) {
          const rawPath: string = data.result.profile_picture;
          const fileName = rawPath.split('\\').pop(); 
          data.result.profile_picture = `http://localhost:8080/vintage_project-1.0-SNAPSHOT/webresources/user/uploads/${fileName}`;
        }
        this.userData = data;
        console.log('User Data:', this.userData);
      },
      error: (error) => {
        console.error('Error fetching user data:', error);
      }
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file && (file.type === 'image/png' || file.type === 'image/jpeg')) {
      this.selectedFile = file;
    } else {
      alert('Csak PNG vagy JPEG fájl engedélyezett!');
    }
  }

  uploadProfilePicture(): void {
    if (!this.selectedFile) {
      alert('Előbb válassz ki egy képet!');
      return;
    }

    const userId = sessionStorage.getItem('id');
    if (!userId) {
      alert('Hiányzó user ID!');
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    const uploadUrl = `http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/${userId}/upload-profile-picture`;

    this.http.post(uploadUrl, formData).subscribe({
      next: () => {
        alert('Kép feltöltve!');
        this.getUserData(Number(userId));
      },
      error: (err: any) => {
        console.error('Hiba a feltöltésnél:', err);
        alert('Hiba történt a kép feltöltésekor!');
      }
    });
  }

  saveProfile(): void {
    const userId = Number(sessionStorage.getItem('id'));
    if (isNaN(userId)) {
      alert('Hibás vagy hiányzó user ID!');
      return;
    }

    const userToUpdate = {
      username: this.userData.result.username,
      firstName: this.userData.result.firstName,
      lastName: this.userData.result.lastName,
      email: this.userData.result.email,
      phoneNumber: this.userData.result.phone,
      isAdmin: this.userData.result.isAdmin || false
    };

    console.log('Mentésre küldött adat:', userToUpdate);

    this.userService.updateUser(userId, userId, userToUpdate).subscribe({
      next: () => {
        alert('Profil sikeresen frissítve!');
        this.isEditeProfile = false;
        this.getUserData(userId);
      },
      error: (err) => {
        console.error('Profil mentési hiba:', err);
        alert('Hiba történt a mentés során!');
      }
    });
  }

  changePassword(): void {
    const userId = Number(sessionStorage.getItem('id'));
    const token = sessionStorage.getItem('token');
    if (isNaN(userId)) {
      alert('User ID not found!');
      return;
    }

    if (!this.newPassword || !this.confirmPassword) {
      alert('Please fill in both password fields.');
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      alert('Passwords do not match.');
      return;
    }

    const passwordRegex = /^(?=.*[!@#$%^&*(),.?":{}|<>]).{8,}$/;
    if (!passwordRegex.test(this.newPassword)) {
      alert('Password must be at least 8 characters and include at least one special character.');
      return;
    }

    const payload = {
      userId: userId,
      newPassword: this.newPassword
    };

    const headers: { [header: string]: string } = {
      'Content-Type': 'application/json'
    };

    if (token) {
      headers['token'] = token;
    }

    this.http.put(
      'http://127.0.0.1:8080/vintage_project-1.0-SNAPSHOT/webresources/user/changePassword',
      payload,
      { headers }
    ).subscribe({
      next: () => {
        alert('Password successfully changed!');
        this.newPassword = '';
        this.confirmPassword = '';
      },
      error: (err) => {
        console.error('Password change error:', err);
        alert('Failed to change password.');
      }
    });
  }

}
