import { Component } from '@angular/core';
import { ActivatedRoute, Router } from "@angular/router";
import { UserService } from "../../../services/user.service";

@Component({
  selector: 'app-verify',
  templateUrl: './verify.component.html',
  styleUrls: ['./verify.component.scss']
})
export class VerifyComponent {
  verified: boolean = false;
  token: string = "";

  constructor(private userService: UserService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.token = params.get('token');
    });

    this.userService.verifyUser(this.token).subscribe({
        next: () => {
          console.log("verified");
          this.verified = true;
        },
        error: error => {
          console.log("Error when verifying user");
        }
      }
    );
  }

  onSubmit(): void {
    this.router.navigate(['/login']);
  }
}