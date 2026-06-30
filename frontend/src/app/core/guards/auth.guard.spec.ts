import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

class MockAuthService {
  authenticated = false;
  isAuthenticated() {
    return this.authenticated;
  }
}

class MockRouter {
  navigatedUrl: string[] | null = null;
  navigate(commands: string[]) {
    this.navigatedUrl = commands;
    return Promise.resolve(true);
  }
}

describe('authGuard', () => {
  let authService: MockAuthService;
  let router: MockRouter;

  beforeEach(() => {
    authService = new MockAuthService();
    router = new MockRouter();

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    });
  });

  it('should return true if user is authenticated', () => {
    authService.authenticated = true;

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );

    expect(result).toBe(true);
    expect(router.navigatedUrl).toBeNull();
  });

  it('should redirect to /login and return false if user is not authenticated', () => {
    authService.authenticated = false;

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );

    expect(result).toBe(false);
    expect(router.navigatedUrl).toEqual(['/login']);
  });
});
