import { Component } from '@angular/core';
import {NgIf} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-verify',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './verify.component.html',
  styleUrl: './verify.component.scss'
})
export class VerifyComponent {
  verified: boolean = true;
  token: string = "";

  constructor(private route: ActivatedRoute, private router: Router) {

  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.token = params.get('token');
      console.log(this.token);
    });
  }


  onSubmit(): void {
    this.router.navigate(['/login']);
  }
}
