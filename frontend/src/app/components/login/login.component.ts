import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthRequest } from '../../dtos/auth-request';
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";

export enum LoginMode {
  admin,
  user,
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  mode: LoginMode = LoginMode.user;
  loginForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag

  constructor(private formBuilder: UntypedFormBuilder, private authService: AuthService, private router: Router, private route: ActivatedRoute, private notification: ToastrService, private spinner: NgxSpinnerService) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  loginUser() {
    this.submitted = true;
    if (this.loginForm.valid) {
      const authRequest: AuthRequest = new AuthRequest(this.loginForm.controls.username.value, this.loginForm.controls.password.value);
      this.authenticateUser(authRequest);
    } else {
      console.log('Invalid input');
    }
  }

  /**
   * Send authentication data to the authService. If the authentication was successfully, the user will be forwarded to the message page
   *
   * @param authRequest authentication data from the user login form
   */
  authenticateUser(authRequest: AuthRequest) {
    console.log('Try to authenticate user: ' + authRequest.email);
  
    this.spinner.show();
    this.authService.loginUser(authRequest).subscribe({
      next: () => {
        this.spinner.hide();
        if (this.mode === LoginMode.admin && this.isAdmin()) {
          console.log('Successfully logged in admin: ' + authRequest.email);
          this.router.navigate(['/admin/dashboard']);
        } else if (this.mode === LoginMode.admin){
          this.handleError({ error: 'Invalid admin email' });
        } else if (this.mode === LoginMode.user && this.isAdmin()) {
          console.log('Successfully logged in admin: ' + authRequest.email);
          this.router.navigate(['/admin/dashboard']);
        } else if (this.mode === LoginMode.user) {
          console.log('Successfully logged in user: ' + authRequest.email);
          this.router.navigate(['/matches']);
        }
      },
      error: error => {
        this.spinner.hide();
        this.handleError(error);
      }
    });
  }

  private handleError(error: any): void {
    console.log('Could not log in due to:');
    console.log(error);
    let errorMessage = ""
    if (typeof error.error === 'object') {
      errorMessage = error.error.error;
    } else {
      errorMessage = error.error;
    }

    this.notification.error(errorMessage, "Sign in failed!")
  }
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  ngOnInit() {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });
  }
  public get description(): string {
    switch (this.mode) {
      case LoginMode.admin:
        return 'Login as admin to view user data!';
      case LoginMode.user:
        return 'Login to view your latest matches!'
      default:
        return '?';
    }
  }
  public get registerLink(): string {
    switch (this.mode) {
      case LoginMode.admin:
        return null;
      case LoginMode.user:
        return 'Register';
      default:
        return '#';
    }
  }
  public get resetPasswordLink(): string {
    switch (this.mode) {
      case LoginMode.admin:
        return null;
      case LoginMode.user:
        return 'Forgot password?';
      default:
        return '#';
    }
  }

}
