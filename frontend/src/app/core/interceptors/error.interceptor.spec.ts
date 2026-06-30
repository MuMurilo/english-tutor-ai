import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { errorInterceptor } from './error.interceptor';
import { AuthService } from '../services/auth.service';

class MockAuthService {
  logoutCalled = false;
  logout() {
    this.logoutCalled = true;
  }
}

class MockRouter {
  navigatedUrl: string[] | null = null;
  navigate(commands: string[]) {
    this.navigatedUrl = commands;
    return Promise.resolve(true);
  }
}

describe('errorInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let authService: MockAuthService;
  let router: MockRouter;

  beforeEach(() => {
    authService = new MockAuthService();
    router = new MockRouter();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should logout and redirect to /login on 401 error', () => {
    let errorThrown = false;
    http.get('/api/test').subscribe({
      next: () => {},
      error: (err) => {
        errorThrown = true;
        expect(err.message).toBe('Sessão inválida ou expirada. Faça login novamente.');
      }
    });

    const req = httpMock.expectOne('/api/test');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(errorThrown).toBe(true);
    expect(authService.logoutCalled).toBe(true);
    expect(router.navigatedUrl).toEqual(['/login']);
  });

  it('should map status 0 error to a friendly connection message', () => {
    let errorThrown = false;
    http.get('/api/test').subscribe({
      next: () => {},
      error: (err) => {
        errorThrown = true;
        expect(err.message).toContain('Não foi possível conectar ao servidor');
      }
    });

    const req = httpMock.expectOne('/api/test');
    req.error(new ProgressEvent('error'), { status: 0 });

    expect(errorThrown).toBe(true);
  });
});
