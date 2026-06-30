import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Feedback, DidacticReport } from '../models/tutor.models';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  getFeedback(): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this.apiUrl}/api/dashboard/feedback`);
  }

  getReport(): Observable<DidacticReport> {
    return this.http.get<DidacticReport>(`${this.apiUrl}/api/dashboard/report`);
  }
}
