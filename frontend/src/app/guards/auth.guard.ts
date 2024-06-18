import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Router, RouterStateSnapshot} from '@angular/router';
import {AuthService} from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard  {

  constructor(private authService: AuthService,
              private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const isLoggedIn = this.authService.isLoggedIn();
    const role = this.authService.getUserRole();

    if (isLoggedIn && (state.url === '/login' || state.url.startsWith('/register') || (state.url.startsWith('/admin') && role !== 'ADMIN'))) {
      if(role === 'ADMIN'){
        this.router.navigate(['/admin/dashboard']);
      } else {
        this.router.navigate(['/myprofile']);
      }
        return false;
    }   else if (!isLoggedIn && ((state.url !== '/login') && !state.url.startsWith('/register')) ) {
        this.router.navigate(['/login']);
        return false;
    }



    return true;

  }
}
