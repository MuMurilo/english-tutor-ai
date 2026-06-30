import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
  private readonly apiUrl = 'http://localhost:8080';

  getFeedback(): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this.apiUrl}/api/dashboard/feedback`);
  }

  getReport(): Observable<DidacticReport> {
    return this.http.get<DidacticReport>(`${this.apiUrl}/api/dashboard/report`);
  }
}
