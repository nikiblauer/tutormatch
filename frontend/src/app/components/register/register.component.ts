import { Component } from '@angular/core';
import {FormControl, FormGroup, FormsModule, NgForm, ReactiveFormsModule, Validators} from "@angular/forms";
import {UserService} from "../../services/user.service";
import {CreateApplicationUserDto} from "../../dtos/user";
import {Router, RouterLink} from "@angular/router";
import {NgIf} from "@angular/common";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  form: FormGroup;
  created: boolean = false;

  createUser: CreateApplicationUserDto = {
      firstname: "",
      lastname: "",
      matrNumber: null,
      email: "",
      password: ""

  }

  constructor(private userService: UserService, private router: Router, private notification: ToastrService) {

  }

  onSubmit(form: NgForm) {
    if (form.valid) {
      console.log('Form Submitted!', form.value);
    } else {
      console.log('Form not valid!');
    }

    this.userService.createUser(this.createUser).subscribe({
        next: () => {
          this.created = true
        },
        error: error => {
          console.error("Error when creating user", error);
          this.notification.error(error.error, "Signup failed");
        }
      }
    );

  }
}
