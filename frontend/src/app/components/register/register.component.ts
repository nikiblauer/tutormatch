import { Component } from '@angular/core';
import {FormControl, FormGroup, FormsModule, NgForm, ReactiveFormsModule, Validators} from "@angular/forms";
import {UserService} from "../../services/user.service";
import {CreateApplicationUserDto} from "../../dtos/user";
import {Router} from "@angular/router";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule
  ],
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

  constructor(private userService: UserService, private router: Router) {

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
          console.log("Error when creating user");
        }
      }
    );

  }
}
