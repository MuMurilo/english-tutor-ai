import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class SidebarComponent implements OnInit {
  @Input() activeTab: 'chat' | 'dashboard' = 'chat';

  private authService = inject(AuthService);
  private router = inject(Router);

  userEmail = '';
  englishLevel = '';

  ngOnInit(): void {
    const userInfo = this.authService.getUserInfo();
    if (userInfo) {
      this.userEmail = userInfo.email;
      this.englishLevel = userInfo.englishLevel;
    } else {
      this.userEmail = 'Estudante';
      this.englishLevel = 'BEGINNER';
    }
  }

  goToChat(): void {
    this.router.navigate(['/chat']);
  }

  goToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
