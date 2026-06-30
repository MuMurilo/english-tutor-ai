import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { Chat } from './chat';
import { ChatService, ChatMessage } from '../../core/services/chat.service';
import { AuthService } from '../../core/services/auth.service';

class MockChatService {
  messages: ChatMessage[] = [
    { id: 1, sender: 'TUTOR', content: 'Hello! How are you?', timestamp: new Date().toISOString() },
    { id: 2, sender: 'USER', content: 'I am fine, thanks', timestamp: new Date().toISOString() }
  ];
  sendMessageCalledWith: string | null = null;
  getChatHistoryCalled = false;

  getChatHistory() {
    this.getChatHistoryCalled = true;
    return of(this.messages);
  }

  sendMessage(content: string) {
    this.sendMessageCalledWith = content;
    return of({ id: 3, sender: 'TUTOR', content: 'Great to hear!', timestamp: new Date().toISOString() });
  }
}

class MockAuthService {
  isAuthenticated() {
    return true;
  }
  getToken() {
    // base64 for {"upn":"student@test.com","englishLevel":"BEGINNER"}
    return "header.eyJ1cG4iOiJzdHVkZW50QHRlc3QuY29tIiwiZW5nbGlzaExldmVsIjoiQkVHSU5ORVIifQ==.signature";
  }
  getUserInfo() {
    return { email: 'student@test.com', englishLevel: 'BEGINNER' };
  }
  logout() {}
}

class MockRouter {
  navigate(commands: any[]) {
    return Promise.resolve(true);
  }
}

describe('Chat', () => {
  let component: Chat;
  let fixture: ComponentFixture<Chat>;
  let mockChatService: MockChatService;

  beforeEach(async () => {
    mockChatService = new MockChatService();

    await TestBed.configureTestingModule({
      imports: [Chat, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        { provide: ChatService, useValue: mockChatService },
        { provide: AuthService, useClass: MockAuthService },
        { provide: Router, useClass: MockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Chat);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load history on init', () => {
    expect(mockChatService.getChatHistoryCalled).toBe(true);
    expect(component.allMessages.length).toBe(2);
    expect(component.allMessages[0].content).toBe('Hello! How are you?');
  });

  it('should send user message and append tutor response', () => {
    component.messageControl.setValue('Hello tutor');
    component.sendMessage();

    expect(mockChatService.sendMessageCalledWith).toBe('Hello tutor');
    // The allMessages array should contain: initial 2 + user message + tutor response = 4 messages
    expect(component.allMessages.length).toBe(4);
    expect(component.allMessages[2].content).toBe('Hello tutor');
    expect(component.allMessages[2].sender).toBe('USER');
    expect(component.allMessages[3].content).toBe('Great to hear!');
    expect(component.allMessages[3].sender).toBe('TUTOR');
    expect(component.messageControl.value).toBe(''); // cleared after send
  });
});
