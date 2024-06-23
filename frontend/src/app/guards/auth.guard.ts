import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const isLoggedIn = this.authService.isLoggedIn();
    const role = this.authService.getUserRole();
    const url = state.url;

    if (!isLoggedIn) {
      // Allow unauthenticated users to access login, register, and password reset pages
      if (url === '/login' || url.startsWith('/register') || url.startsWith('/password_reset')) {
        return true;
      }
      // Redirect unauthenticated users to login page for all other routes
      this.router.navigate(['/login']);
      return false;
    } else {
      // Prevent authenticated users from accessing login, register, and password reset pages
      if (url === '/login' || url.startsWith('/register') || url.startsWith('/password_reset')) {
        if (role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else {
          this.router.navigate(['/myprofile']);
        }
        return false;
      }
    }

    // Handle role-based access for authenticated users
    if (role === 'USER' && (url.startsWith('/admin'))) {
      this.router.navigate(['/forbidden']);
      return false;
    } else if (role === 'ADMIN' && (url.startsWith('/myprofile') || url.startsWith('/matches') || url.startsWith('/chat') || url.startsWith('/feedback'))) {
      this.router.navigate(['/forbidden']);
      return false;
    }

    // Allow access if all checks pass
    return true;
  }
}
