import { Component } from '@angular/core';
import {FormControl, FormGroup, FormsModule, NgForm, ReactiveFormsModule, Validators} from "@angular/forms";
import {UserService} from "../../services/user.service";
import {CreateStudentDto} from "../../dtos/user";
import {Router, RouterLink} from "@angular/router";
import {NgIf} from "@angular/common";
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  form: FormGroup;
  created: boolean = false;

  createUser: CreateStudentDto = {
      firstname: "",
      lastname: "",
      matrNumber: null,
      email: "",
      password: ""

  }

  constructor(private userService: UserService, private router: Router, private notification: ToastrService, private spinner: NgxSpinnerService) {

  }

  onSubmit(form: NgForm) {
    if (form.valid) {
      console.log('Form Submitted!', form.value);
    } else {
      console.log('Form not valid!');
    }

    this.spinner.show();
    this.userService.createUser(this.createUser).subscribe({
        next: () => {
          this.spinner.hide();
          this.created = true
        },
        error: error => {
          this.spinner.hide();
          console.error("Error when creating user", error);
          this.notification.error(error.error, "Signup failed");
        }
      }
    );

  }

  resendEmail() {
    this.spinner.show();
    console.log(this.createUser.email)

    this.userService.resendVerification(this.createUser.email).subscribe({
        next: () => {
          this.spinner.hide();
          this.notification.success("Verification email resent.");
        },
        error: error => {
          this.spinner.hide();
          console.error("Error when creating user", error);
          this.notification.error(error.error, "Could not resend verification email");
        }
      }
    );

  }
}
