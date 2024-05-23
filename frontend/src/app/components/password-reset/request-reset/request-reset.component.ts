import { Component } from '@angular/core';
import {NgIf} from "@angular/common";
import {
  FormGroup,
  FormsModule,
  NgForm,
  ReactiveFormsModule,
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators
} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../services/user.service";
import {SendPasswordResetDto} from "../../../dtos/user";

@Component({
  selector: 'app-request-reset',
  standalone: true,
  imports: [
    NgIf,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './request-reset.component.html',
  styleUrl: './request-reset.component.scss'
})
export class RequestResetComponent {
  form: FormGroup;
  emailSendPasswordResetDto = {
    email: '',
  };
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag
  error = false;
  errorMessage = '';

  constructor(private userService: UserService, private router: Router, private route: ActivatedRoute) {
  }

  onSubmit(form: NgForm) {
    if (form.valid) {
      this.userService.requestPasswordReset(this.emailSendPasswordResetDto).subscribe({
          next: () => {
            this.submitted = true
          },
          error: error => {
            error = true;
            console.log("Error when creating user");
          }
        }
      );
    } else {
      console.log('Invalid input');
    }
  }
}
