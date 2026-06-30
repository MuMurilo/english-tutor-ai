import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let friendlyMessage = 'Ocorreu um erro inesperado. Tente novamente.';

      if (error.status === 0) {
        friendlyMessage = 'Não foi possível conectar ao servidor do Tutor. Verifique se o backend está rodando.';
      } else if (error.status === 401) {
        friendlyMessage = 'Sessão inválida ou expirada. Faça login novamente.';
        authService.logout();
        router.navigate(['/login']);
      } else if (error.status === 403) {
        friendlyMessage = 'Acesso negado para este recurso.';
      } else if (error.error && typeof error.error === 'object' && error.error.message) {
        friendlyMessage = error.error.message;
      }

      // Propaga o erro estendido com a mensagem amigável formatada
      const modifiedError = {
        originalError: error,
        message: friendlyMessage
      };

      return throwError(() => modifiedError);
    })
  );
};
