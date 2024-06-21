import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-forbidden',
  standalone: true,
  imports: [],
  templateUrl: './forbidden.component.html',
  styleUrl: './forbidden.component.scss'
})
export class ForbiddenComponent implements OnInit {

  routerLink: string;

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    this.routerLink = this.authService.getUserRole() === 'ADMIN' ? '/admin/dashboard' : '/matches';
  }

}
