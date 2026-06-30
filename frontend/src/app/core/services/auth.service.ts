import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginResponse {
  token: string;
}

export interface UserInfo {
  email: string;
  englishLevel: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly tokenKey = 'tutor_auth_token';
  private readonly apiUrl = 'http://localhost:8080'; // Backend runs on 8080 by default

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/api/auth/login`, { email, password }).pipe(
      tap((res) => {
        if (res && res.token) {
          this.setToken(res.token);
        }
      })
    );
  }

  register(email: string, password: string, englishLevel: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/auth/register`, {
      email,
      password,
      englishLevel,
    });
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getUserInfo(): UserInfo | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;
      const payload = JSON.parse(atob(parts[1]));
      return {
        email: payload.upn || 'Estudante',
        englishLevel: payload.englishLevel || 'BEGINNER',
      };
    } catch (e) {
      return null;
    }
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }
}
