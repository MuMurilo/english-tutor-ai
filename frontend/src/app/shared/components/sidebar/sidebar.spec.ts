import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { SidebarComponent } from './sidebar';
import { AuthService } from '../../../core/services/auth.service';

class MockAuthService {
  logoutCalled = false;
  getUserInfo() {
    return { email: 'student@test.com', englishLevel: 'BEGINNER' };
  }
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

describe('SidebarComponent', () => {
  let component: SidebarComponent;
  let fixture: ComponentFixture<SidebarComponent>;
  let authService: MockAuthService;
  let router: MockRouter;

  beforeEach(async () => {
    authService = new MockAuthService();
    router = new MockRouter();

    await TestBed.configureTestingModule({
      imports: [SidebarComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load profile on init', () => {
    expect(component).toBeTruthy();
    expect(component.userEmail).toBe('student@test.com');
    expect(component.englishLevel).toBe('BEGINNER');
  });

  it('should navigate to chat', () => {
    component.goToChat();
    expect(router.navigatedUrl).toEqual(['/chat']);
  });

  it('should navigate to dashboard', () => {
    component.goToDashboard();
    expect(router.navigatedUrl).toEqual(['/dashboard']);
  });

  it('should logout and redirect', () => {
    component.logout();
    expect(authService.logoutCalled).toBe(true);
    expect(router.navigatedUrl).toEqual(['/login']);
  });
});
