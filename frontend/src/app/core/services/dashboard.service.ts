import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Feedback {
  id?: number;
  userId: number;
  type: 'ERROR' | 'CONSOLIDATED';
  originalPhrase: string;
  content: string;
  explanation: string;
  timestamp: string;
}

export interface DidacticReport {
  summary: string;
  strengths: string[];
  weaknesses: string[];
  actionPlan: string;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private readonly apiUrl = 'http://localhost:8080';

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  getFeedback(): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this.apiUrl}/api/dashboard/feedback`, {
      headers: this.getHeaders()
    });
  }

  getReport(): Observable<DidacticReport> {
    return this.http.get<DidacticReport>(`${this.apiUrl}/api/dashboard/report`, {
      headers: this.getHeaders()
    });
  }
}
