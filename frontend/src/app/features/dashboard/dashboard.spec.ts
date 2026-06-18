import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { Dashboard } from './dashboard';
import { DashboardService, Feedback } from '../../core/services/dashboard.service';
import { AuthService } from '../../core/services/auth.service';

class MockDashboardService {
  feedbacks: Feedback[] = [
    { id: 1, userId: 42, type: 'ERROR', originalPhrase: 'He don\'t care', content: 'He doesn\'t care', explanation: 'Use doesn\'t.', timestamp: new Date().toISOString() },
    { id: 2, userId: 42, type: 'CONSOLIDATED', originalPhrase: 'Elaborate', content: 'Could you elaborate?', explanation: 'Good use.', timestamp: new Date().toISOString() }
  ];
  getFeedbackCalled = false;
  getReportCalled = false;

  getFeedback() {
    this.getFeedbackCalled = true;
    return of(this.feedbacks);
  }

  getReport() {
    this.getReportCalled = true;
    return of({
      summary: 'Report summary',
      strengths: ['strength 1'],
      weaknesses: ['weakness 1'],
      actionPlan: 'Action plan text'
    });
  }
}

class MockAuthService {
  isAuthenticated() {
    return true;
  }
  getToken() {
    return "header.eyJ1cG4iOiJzdHVkZW50QHRlc3QuY29tIiwiZW5nbGlzaExldmVsIjoiQkVHSU5ORVIifQ==.signature";
  }
  logout() {}
}

class MockRouter {
  navigate(commands: any[]) {
    return Promise.resolve(true);
  }
}

describe('Dashboard', () => {
  let component: Dashboard;
  let fixture: ComponentFixture<Dashboard>;
  let mockDashboardService: MockDashboardService;

  beforeEach(async () => {
    mockDashboardService = new MockDashboardService();

    await TestBed.configureTestingModule({
      imports: [Dashboard, HttpClientTestingModule],
      providers: [
        { provide: DashboardService, useValue: mockDashboardService },
        { provide: AuthService, useClass: MockAuthService },
        { provide: Router, useClass: MockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Dashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load feedback on init and separate by type', () => {
    expect(mockDashboardService.getFeedbackCalled).toBe(true);
    expect(component.errors.length).toBe(1);
    expect(component.errors[0].originalPhrase).toBe('He don\'t care');
    expect(component.consolidated.length).toBe(1);
    expect(component.consolidated[0].originalPhrase).toBe('Elaborate');
  });
});
