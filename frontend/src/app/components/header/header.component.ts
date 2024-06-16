import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  isCollapsed = true;

  constructor(public authService: AuthService) { }

  ngOnInit() {
  }

  isAdmin() {
    return this.authService.getUserRole() === 'ADMIN';
  }

  getLink() {
    if (!this.authService.isLoggedIn()) {
      return '/login';
    }
    return this.isAdmin() ? '/admin/dashboard' : '/matches';
  }
}
