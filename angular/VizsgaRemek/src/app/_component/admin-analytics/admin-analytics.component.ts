import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { ApexNonAxisChartSeries, ApexChart, ApexResponsive, ApexLegend, ApexOptions, NgApexchartsModule } from 'ng-apexcharts';
import { CommonModule } from '@angular/common';
import { Title } from '@angular/platform-browser';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { MessageService } from '../../services/message.service';

@Component({
  selector: 'app-admin-analytics',
  imports: [CommonModule, NgApexchartsModule],
  templateUrl: './admin-analytics.component.html',
  styleUrl: './admin-analytics.component.css'
})
export class AdminAnalyticsComponent implements OnInit {
  users: any[] = [];
  products: any[] = [];
  productCount = 0;
  totalStock = 0;
  chartOptions: Partial<ApexOptions> = {};
  topUsers = [
    { fullName: 'John Doe', email: 'john@example.com', orders: 17 },
    { fullName: 'Anna Smith', email: 'anna@example.com', orders: 14 },
    { fullName: 'Peter Parker', email: 'peter@dailybugle.com', orders: 11 },
    { fullName: 'Bruce Wayne', email: 'bruce@wayneenterprises.com', orders: 8 },
    { fullName: 'Clark Kent', email: 'clark@dailyplanet.com', orders: 6 }
  ];
  topProducts = [
    { name: 'Vintage Leather Bag', sales: 124 },
    { name: 'Retro Sunglasses', sales: 97 },
    { name: 'Classic Watch', sales: 85 },
    { name: 'Denim Jacket', sales: 79 },
    { name: 'Canvas Backpack', sales: 65 }
  ];
  recentActivity = [
    { user: 'Anna N.', action: 'registered a new account.', time: '2 minutes ago' },
    { user: 'Mark D.', action: 'updated user info.', time: '10 minutes ago' },
    { user: 'Admin', action: 'deleted user #42.', time: '1 hour ago' },
    { user: 'Sara T.', action: 'added product "Denim Jacket".', time: '3 hours ago' },
    { user: 'Admin', action: 'reactivated user #37.', time: 'Yesterday' }
  ];
  inventoryStats = {
    available: 235,
    low: 8,
    out: 2
  };
  supportTickets = {
    open: 14,
    pending: 6,
    resolved: 87
  };
  monthlySignupCount = 0;

  constructor(private userService: UserService, private titleService: Title, private productService: ProductService, private messageService: MessageService) {
    titleService.setTitle("Admin | Analytics");
  }

  ngOnInit(): void {
    this.userService.getAllUsers().subscribe({
      next: (res) => {
        this.users = res.result || res;
        this.prepareDonutChart(this.users);

        const monthlyCounts = this.getUserRegistrationsPerMonth(this.users);
        const currentMonthIndex = new Date().getMonth();
        this.monthlySignupCount = monthlyCounts[currentMonthIndex];

        this.chartOptionss = {
          ...this.chartOptionss,
          series: [{ name: 'Users', data: monthlyCounts }]
        };
      },
      error: (err) => {
        console.error('User fetch error', err);
      }
    });
    this.productService.getProducts().subscribe({
      next: (res) => {
        const products: Product[] = res || res;
        console.log('Products:', products);
        this.productCount = products.length;
        this.totalStock = products.reduce((sum, p) => sum + (p.stockQuanty || 0), 0);

        // 💡 Stock statisztikák
        const available = products.filter(p => p.stockQuanty > 5).length;
        const low = products.filter(p => p.stockQuanty > 0 && p.stockQuanty <= 5).length;
        const out = products.filter(p => p.stockQuanty === 0).length;

        this.inventoryStats = { available, low, out };
      },
      error: err => console.error('❌ Termékek betöltése sikertelen', err)
    });
    this.userService.getAllTicket().subscribe({
      next: (tickets) => {
        console.log('Tickets:', tickets);
        const open = tickets.result.filter((t: { status: string; }) => t.status === 'Open').length;
        const pending = tickets.result.filter((t: { status: string; }) => t.status === 'Pending').length;
        const resolved = tickets.result.filter((t: { status: string; }) => t.status === 'Resolved').length;
    
        this.supportTickets = { open, pending, resolved };
      },
      error: err => console.error('❌ Ticket betöltési hiba:', err)
    });
    

  }

  prepareDonutChart(users: any[]) {
    const activeCount = users.filter(u => !u.isDeleted).length;
    const deletedCount = users.filter(u => u.isDeleted).length;

    this.chartOptions = {
      series: [activeCount, deletedCount],
      chart: {
        type: 'donut',
        width: 380
      },
      labels: ['Active Users', 'Deleted Users'],
      responsive: [{
        breakpoint: 480,
        options: {
          chart: { width: 300 },
          legend: { position: 'bottom' }
        }
      }],
      legend: {
        position: 'right'
      }
    };
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
      const cleaned = raw
        .replace('CET', 'GMT') // vagy akár 'CET' -> '+0100' ha pontosabb kell
        .replace('CEST', 'GMT');
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

  public salesChartOptions: Partial<ApexOptions> = {
    series: [70, 20, 10], // Tesztadat: Paid, Pending, Refunded
    chart: {
      type: 'pie',
      height: 300
    },
    labels: ['Paid', 'Pending', 'Refunded'],
    legend: {
      position: 'bottom'
    },
    responsive: [
      {
        breakpoint: 480,
        options: {
          chart: {
            width: 300
          },
          legend: {
            position: 'bottom'
          }
        }
      }
    ]
  };

}
