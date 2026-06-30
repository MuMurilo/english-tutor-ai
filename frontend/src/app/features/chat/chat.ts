import { Component, OnInit, inject, ElementRef, ViewChild, AfterViewChecked, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ChatService } from '../../core/services/chat.service';
import { ChatMessage } from '../../core/models/tutor.models';
import { AuthService } from '../../core/services/auth.service';

import { SidebarComponent } from '../../shared/components/sidebar/sidebar';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SidebarComponent],
  templateUrl: './chat.html',
  styleUrl: './chat.css'
})
export class Chat implements OnInit {
  private chatService = inject(ChatService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  allMessages: ChatMessage[] = [];
  visibleMessages: ChatMessage[] = [];
  displayLimit = 20;
  messageControl = new FormControl('', [Validators.required]);
  isLoading = false;
  isSending = false;
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
    this.loadHistory();
  }

  loadHistory(): void {
    console.log('Iniciando carregamento do histórico...');
    this.isLoading = true;
    this.chatService.getChatHistory().subscribe({
      next: (history) => {
        console.log('Histórico carregado com sucesso:', history);
        this.allMessages = history;
        this.updateVisibleMessages();
        this.isLoading = false;
        this.cdr.detectChanges();
        this.scrollToBottom();
      },
      error: (err) => {
        console.error('Erro ao carregar histórico:', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  updateVisibleMessages(): void {
    if (this.allMessages.length <= this.displayLimit) {
      this.visibleMessages = [...this.allMessages];
    } else {
      this.visibleMessages = this.allMessages.slice(this.allMessages.length - this.displayLimit);
    }
  }

  loadOlderMessages(): void {
    if (!this.scrollContainer) {
      this.displayLimit += 20;
      this.updateVisibleMessages();
      this.cdr.detectChanges();
      return;
    }

    const container = this.scrollContainer.nativeElement;
    const oldScrollHeight = container.scrollHeight;
    const oldScrollTop = container.scrollTop;

    this.displayLimit += 20;
    this.updateVisibleMessages();
    this.cdr.detectChanges();

    setTimeout(() => {
      try {
        const newScrollHeight = container.scrollHeight;
        container.scrollTop = oldScrollTop + (newScrollHeight - oldScrollHeight);
      } catch (err) {}
    }, 0);
  }

  sendMessage(): void {
    const content = this.messageControl.value;
    if (!content || content.trim() === '' || this.isSending) {
      return;
    }

    console.log('Enviando mensagem do usuário:', content);

    // Adiciona a mensagem do usuário localmente para feedback instantâneo
    const userMsg: ChatMessage = {
      sender: 'USER',
      content: content.trim(),
      timestamp: new Date().toISOString()
    };
    
    this.allMessages.push(userMsg);
    this.updateVisibleMessages();
    
    this.messageControl.setValue('');
    this.isSending = true;
    this.messageControl.disable();
    this.cdr.detectChanges();
    this.scrollToBottom();

    this.chatService.sendMessage(content).subscribe({
      next: (tutorResponse) => {
        console.log('Resposta recebida do Tutor:', tutorResponse);
        this.allMessages.push(tutorResponse);
        this.updateVisibleMessages();
        this.isSending = false;
        this.messageControl.enable();
        this.cdr.detectChanges();
        this.scrollToBottom();
      },
      error: (err) => {
        console.error('Erro ao enviar mensagem para o Tutor:', err);
        this.isSending = false;
        this.messageControl.enable();
        
        const errorMsg: ChatMessage = {
          sender: 'TUTOR',
          content: 'Oops, I had a connection glitch. Could you please send that again?',
          timestamp: new Date().toISOString()
        };
        this.allMessages.push(errorMsg);
        this.updateVisibleMessages();
        
        this.cdr.detectChanges();
        this.scrollToBottom();
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      try {
        if (this.scrollContainer) {
          this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
        }
      } catch (err) {}
    }, 50);
  }
}
