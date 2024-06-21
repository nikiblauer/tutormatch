import { Component } from '@angular/core';
import { NgForm } from "@angular/forms";
import { UserService } from "../../services/user.service";
import { CreateStudentDto } from "../../dtos/user";
import { Router } from "@angular/router";
import { ToastrService } from "ngx-toastr";
import { NgxSpinnerService } from "ngx-spinner";
import { FormGroup } from '@angular/forms';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  form: FormGroup;
  created: boolean = false;
  showOrHidePassword: boolean;

  createUser: CreateStudentDto = {
    firstname: "",
    lastname: "",
    matrNumber: null,
    email: "",
    password: "",
    repeatPassword: ""
  }

  constructor(private userService: UserService, private router: Router, private notification: ToastrService, private spinner: NgxSpinnerService) {

  }

  onSubmit(form: NgForm) {
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

  toggleshowOrHidePassword() {
    this.showOrHidePassword = !this.showOrHidePassword;
  }

  passwordsMatch(): boolean {
    return this.createUser.password === this.createUser.repeatPassword;
  }
}
