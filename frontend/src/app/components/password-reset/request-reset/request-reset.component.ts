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
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerComponent, NgxSpinnerService} from "ngx-spinner";

@Component({
  selector: 'app-request-reset',
  templateUrl: './request-reset.component.html',
  styleUrl: './request-reset.component.scss'
})
export class RequestResetComponent {
  form: FormGroup;
  emailSendPasswordResetDto: SendPasswordResetDto = {
    email: '',
  };
  submitted = false;
  // Error flag
  error = false;

  constructor(private userService: UserService, private router: Router, private route: ActivatedRoute, private notification: ToastrService, private spinner: NgxSpinnerService) {
  }

  onSubmit(form: NgForm) {
    this.spinner.show();
    if (form.valid) {
      this.userService.requestPasswordReset(this.emailSendPasswordResetDto).subscribe({
          next: () => {
            this.spinner.hide();
            this.submitted = true
          },
          error: error => {
            this.spinner.hide();
            this.notification.error(error.error, "Sending password reset email failed");
            this.submitted = false;
            console.error("Error sending password reset email",error.error);
          }
        }
      );
    } else {
      console.error('Invalid input');
    }
  }
}
