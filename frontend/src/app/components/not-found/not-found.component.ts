import { Component } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-not-found',
  template: `<p>Page not found. Redirecting...</p>`
})
export class NotFoundComponent {
  constructor(private router: Router, private authService: AuthService) {
    const role = this.authService.getUserRole();
    if (role === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
    } else {
      this.router.navigate(['/myprofile']);
    }
  }
}
