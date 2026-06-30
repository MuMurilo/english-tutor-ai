import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ChatMessage {
  id?: number;
  sender: 'USER' | 'TUTOR';
  content: string;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  getChatHistory(): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.apiUrl}/api/chat/history`);
  }

  sendMessage(content: string): Observable<ChatMessage> {
    return this.http.post<ChatMessage>(`${this.apiUrl}/api/chat/send`, { content });
  }
}
