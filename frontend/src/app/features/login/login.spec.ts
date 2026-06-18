import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Login } from './login';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Login, ReactiveFormsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize loginForm and registerForm', () => {
    expect(component.loginForm).toBeDefined();
    expect(component.registerForm).toBeDefined();
  });

  it('should validate loginForm input requirements', () => {
    const emailControl = component.loginForm.get('email')!;
    const passwordControl = component.loginForm.get('password')!;

    emailControl.setValue('');
    passwordControl.setValue('');
    expect(component.loginForm.valid).toBe(false);

    emailControl.setValue('invalid-email');
    expect(emailControl.valid).toBe(false);

    emailControl.setValue('student@test.com');
    passwordControl.setValue('123456');
    expect(component.loginForm.valid).toBe(true);
  });

  it('should validate registerForm level selection', () => {
    const levelControl = component.registerForm.get('englishLevel')!;
    
    levelControl.setValue('');
    expect(levelControl.valid).toBe(false);

    levelControl.setValue('BEGINNER');
    expect(levelControl.valid).toBe(true);
  });
});
