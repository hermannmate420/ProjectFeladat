import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NgApexchartsModule, ApexChart, ApexXAxis, ApexOptions, ApexDataLabels, ApexTitleSubtitle, ApexAxisChartSeries } from 'ng-apexcharts';


@Component({
  selector: 'app-admin-home',
  imports: [CommonModule, RouterModule, NgApexchartsModule],
  templateUrl: './admin-home.component.html',
  styleUrl: './admin-home.component.css'
})

export class AdminHomeComponent implements OnInit {
  userCount = 0;
  activeCount = 0;
  deletedCount = 0;
  adminCount = 0;
  monthlySignupCount = 0;


  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.getAllUsers().subscribe({
      next: (res) => {
        const users = res.result || res;
        this.userCount = users.length;
        this.activeCount = users.filter((u: any) => !u.isDeleted).length;
        this.deletedCount = users.filter((u: any) => u.isDeleted).length;
        this.adminCount = users.filter((u: any) => u.isAdmin).length;

        // 📊 Dinamikus chart adat generálás:
        const monthlyCounts = this.getUserRegistrationsPerMonth(users);
        const currentMonthIndex = new Date().getMonth();
        this.monthlySignupCount = monthlyCounts[currentMonthIndex];

        this.chartOptionss = {
          ...this.chartOptionss,
          series: [{ name: 'Users', data: monthlyCounts }]
        };


      },
      error: err => console.error(err)
    });
  }

  get statCards() {
    return [
      { label: 'Total Users', value: this.userCount, icon: '👥', bg: 'bg-primary text-light' },
      { label: 'Active Users', value: this.activeCount, icon: '✅', bg: 'bg-success text-light' },
      { label: 'Deleted Users', value: this.deletedCount, icon: '❌', bg: 'bg-danger text-light' },
      { label: 'Admins', value: this.adminCount, icon: '🛡️', bg: 'bg-dark text-light' },

      // ⬇ Új placeholder kártyák (még nincs backend)
      { label: 'Monthly Signups', value: this.monthlySignupCount, icon: '📈', bg: 'bg-info text-light' },
      { label: 'Pending Orders', value: 0, icon: '🛒', bg: 'bg-warning text-light' },
      { label: 'Products in Store', value: 0, icon: '📦', bg: 'bg-secondary text-light' },
      { label: 'Support Tickets', value: 0, icon: '🎫', bg: 'bg-light text-dark' }
    ];
  }


  public chartOptionss: Partial<ApexOptions> = {

    series: [
      {
        name: "Users",
        data: new Array(12).fill(0)  // biztosan 12 elem, 0-s kezdéssel
      }
    ],
    chart: {
      type: "line",
      height: 350
    },
    title: {
      text: "User registrations per month"
    },
    xaxis: {
      categories: [
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
      ]
    },
    dataLabels: {
      enabled: true
    }
  };

  getUserRegistrationsPerMonth(users: any[]): number[] {
    const counts = new Array(12).fill(0);
    console.log('--- Feldolgozás indul ---');

    users.forEach((user, i) => {
      const raw = user.createdAt;
      const cleaned = raw.replace('CET', 'GMT');

      const parsed = Date.parse(cleaned);
      if (!isNaN(parsed)) {
        const date = new Date(parsed);
        const month = date.getMonth();
        counts[month]++;
        console.log(`✔️ OK: ${raw} => ${month}`);
      } else {
        console.warn(`❌ Nem sikerült parse-olni: ${raw}`);
      }
    });

    console.log('📊 Havi bontás:', counts);
    return counts;
  }

}
