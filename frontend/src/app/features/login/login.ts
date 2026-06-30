import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  isLoginMode = true;
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  registerForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    englishLevel: ['', [Validators.required]],
  });

  toggleMode(): void {
    this.isLoginMode = !this.isLoginMode;
    this.errorMessage = '';
    this.successMessage = '';
    this.loginForm.reset();
    this.registerForm.reset();
  }

  selectLevel(level: string): void {
    this.registerForm.get('englishLevel')?.setValue(level);
    this.registerForm.get('englishLevel')?.markAsTouched();
  }

  onSubmitLogin(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    const { email, password } = this.loginForm.value;

    this.authService.login(email, password).subscribe({
      next: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
        this.router.navigate(['/chat']); // Go to chat as US2 conversation is main focus, or dashboard
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Erro no login:', err);
        this.errorMessage = err.message || 'Erro ao realizar login. Tente novamente.';
        this.cdr.detectChanges();
      },
    });
  }

  onSubmitRegister(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    const { email, password, englishLevel } = this.registerForm.value;

    this.authService.register(email, password, englishLevel).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Cadastro realizado com sucesso! Faça seu login.';
        this.isLoginMode = true;
        this.loginForm.patchValue({ email });
        this.registerForm.reset();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Erro no cadastro:', err);
        this.errorMessage = err.message || 'Erro ao realizar cadastro. Tente novamente.';
        this.cdr.detectChanges();
      },
    });
  }
}
