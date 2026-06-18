import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DashboardService, Feedback, DidacticReport } from '../../core/services/dashboard.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  private dashboardService = inject(DashboardService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  errors: Feedback[] = [];
  consolidated: Feedback[] = [];
  isLoading = false;
  userEmail = '';
  englishLevel = '';
  showAllErrors = false;
  showAllConsolidated = false;
  report: DidacticReport | null = null;
  isReportLoading = false;

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.extractUserInfo();
    this.loadFeedbackData();
  }

  extractUserInfo(): void {
    const token = this.authService.getToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.userEmail = payload.upn || 'Estudante';
        this.englishLevel = payload.englishLevel || 'BEGINNER';
      } catch (e) {
        this.userEmail = 'Estudante';
        this.englishLevel = 'BEGINNER';
      }
    }
  }

  loadFeedbackData(): void {
    this.isLoading = true;
    this.dashboardService.getFeedback().subscribe({
      next: (feedbacks) => {
        this.errors = feedbacks.filter(f => f.type === 'ERROR');
        this.consolidated = feedbacks.filter(f => f.type === 'CONSOLIDATED');
        this.isLoading = false;
        this.cdr.detectChanges();
        if (feedbacks.length > 0) {
          this.generateDidacticReport();
        }
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  generateDidacticReport(): void {
    this.isReportLoading = true;
    this.cdr.detectChanges();
    this.dashboardService.getReport().subscribe({
      next: (data) => {
        this.report = data;
        this.isReportLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao gerar relatório didático:', err);
        this.isReportLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  goToChat(): void {
    this.router.navigate(['/chat']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
