import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-user-table',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-user-table.component.html',
  styleUrl: './admin-user-table.component.css'
})
export class AdminUserTableComponent implements OnInit {
  users: any[] = [];
  error: string = '';
  isEdit: boolean = false;
  searchTerm: string = '';
  selectedUserId: string = '';
  selectedUser: any = null;
  currentPage = 1;
  usersPerPage = 10;
  newAdmin = {
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    phoneNumber: '',
    password: ''
  };
  statusFilter: string = 'all';
  totalUsers = 0;
  activeUsers = 0;
  deletedUsers = 0;
  adminUsers = 0;
  exportFilter: 'all' | 'active' | 'admin' = 'all';
  toastMessage: string = '';
  toastColorClass: string = 'bg-success';
  showToast: boolean = false;
  showOverlay: boolean = true;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data.result || data; // attól függ, hogy csomagolva jön-e
        console.log('Felhasználók:', this.users);

        this.totalUsers = this.users.length;
        this.activeUsers = this.users.filter(u => !u.isDeleted).length;
        this.deletedUsers = this.users.filter(u => u.isDeleted).length;
        this.adminUsers = this.users.filter(u => u.isAdmin).length;
      },
      error: (err) => {
        this.error = 'Hiba a felhasználók lekérésekor!';
        console.error(err);
      }
    });
  }
  editUsers(): void {
    if (this.isEdit) {
      this.isEdit = false;
    } else {
      this.isEdit = true;
    }
  }

  saveUser(user: any): void {
    const editorId = Number(localStorage.getItem('id'));
    if (isNaN(editorId)) {
      console.error('Invalid editor ID');
      return;
    }
    this.userService.updateUser(editorId, user.id, user).subscribe({
      next: (updated) => {
        console.log('Sikeres mentés:', updated);
        alert('Sikeresen mentve!');
        this.showSaveToast(`✔ User #${user.id} saved successfully!`);
        // this.showSaveToast(`❌ User #${user.id} has been deleted.`, 'bg-danger'); // ezeket majd a helyükre kell tenni
        // this.showSaveToast(`🔄 User #${user.id} reactivated.`, 'bg-warning'); // ezeket majd a helyükre kell tenni


      },
      error: (err) => {
        console.error('Mentési hiba:', err);
        alert('Hiba a mentés közben!');
      }
    });
  }



  onSelectUser(): void {
    const found = this.users.find(u => u.id === +this.selectedUserId);
    this.selectedUser = found ? { ...found } : null;
    this.showOverlay = true;
  }

  get filteredUsers() {
    return this.users.filter(user => {
      const searchMatch = `${user.firstName} ${user.lastName} ${user.email} ${user.id}`
        .toLowerCase()
        .includes(this.searchTerm.toLowerCase());

      const statusMatch =
        this.statusFilter === 'all' ||
        (this.statusFilter === 'active' && !user.isDeleted) ||
        (this.statusFilter === 'deleted' && user.isDeleted) ||
        (this.statusFilter === 'admins' && user.isAdmin);

      return searchMatch && statusMatch;
    });
  }

  get paginatedUsers() {
    const start = (this.currentPage - 1) * this.usersPerPage;
    const end = start + this.usersPerPage;
    return this.filteredUsers.slice(start, end);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredUsers.length / this.usersPerPage);
  }

  exportUsersToCSV(): void {
    let usersToExport = [...this.filteredUsers];

    if (this.exportFilter === 'active') {
      usersToExport = usersToExport.filter(u => !u.isDeleted);
    } else if (this.exportFilter === 'admin') {
      usersToExport = usersToExport.filter(u => u.isAdmin);
    }

    const headers = ['ID', 'Email', 'First Name', 'Last Name', 'Phone Number', 'Admin'];
    const rows = usersToExport.map(user => [
      user.id,
      user.email,
      user.firstName,
      user.lastName,
      user.phoneNumber,
      user.isAdmin ? 'Yes' : 'No'
    ]);

    const csvContent = [headers, ...rows]
      .map(e => e.map(field => `"${(field + '').replace(/"/g, '""')}"`).join(','))
      .join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);

    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'users_export.csv');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }


  registerNewAdmin(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Token is missing – please login.');
      return;
    }

    if (Object.values(this.newAdmin).some(v => !v)) {
      alert('Please fill in all admin registration fields!');
      return;
    }

    const headers = {
      'Content-Type': 'application/json',
      'token': token
    };

    const adminData = {
      username: this.newAdmin.username,
      email: this.newAdmin.email,
      firstName: this.newAdmin.firstName,
      lastName: this.newAdmin.lastName,
      phoneNumber: this.newAdmin.phoneNumber,
      password: this.newAdmin.password
    };

    this.userService.registerAdmin(adminData, headers).subscribe({
      next: (res) => {
        alert('New admin registered successfully!');
        this.newAdmin = {
          username: '', email: '', firstName: '', lastName: '', phoneNumber: '', password: ''
        };
        this.ngOnInit(); // újratölti a felhasználókat
      },
      error: (err) => {
        console.error('Admin reg error:', err);
        alert('Registration failed.');
      }
    });
  }

  toggleUserStatus(user: any): void {
    const editorId = Number(localStorage.getItem('id'));
    if (isNaN(editorId)) return;

    const updatedStatus = !user.isDeleted;
    const payload = { ...user, isDeleted: updatedStatus };

    this.userService.updateUser(editorId, user.id, payload).subscribe({
      next: () => {
        alert(updatedStatus ? 'User deleted ❌' : 'User reactivated ✅');
        this.ngOnInit(); // újra betöltés
      },
      error: err => {
        console.error('Hiba a státusz váltás közben:', err);
        alert('Hiba történt.');
      }
    });
  }

  showSaveToast(message: string, colorClass: string = 'bg-success'): void {
    this.toastMessage = message;
    this.toastColorClass = colorClass;
    this.showToast = true;
    setTimeout(() => {
      this.showToast = false;
    }, 2000); // 2 másodperc után eltűnik
  }

  deleteUser(user: any): void {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Token not found – please log in again.');
      return;
    }

    if (!confirm(`Are you sure you want to delete user: ${user.username}?`)) {
      return;
    }

    this.userService.deleteUserLogically(user.id, token).subscribe({
      next: () => {
        this.showSaveToast(`❌ User ${user.username} marked as deleted.`, 'bg-danger');
        this.ngOnInit();
      },
      error: (err) => {
        console.error('User deletion error:', err);
        alert('Failed to delete user.');
      }
    });
  }

  reactivateUserById(user: any): void {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Token not found – please log in again.');
      return;
    }

    this.userService.reactivateUserById(user.id).subscribe({
      next: () => {
        this.showSaveToast(`♻️ User "${user.username}" reactivated.`, 'bg-success');
        this.ngOnInit(); // újratöltés
      },
      error: (err) => {
        console.error('Reactivation error:', err);
        alert('Failed to reactivate user.');
      }
    });
  }



}