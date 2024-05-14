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

  toCreate: CreateApplicationUserDto;

  constructor(private userService: UserService, private router: Router) {

  }

  onSubmit(form: NgForm) {
    if (form.valid) {
      console.log('Form Submitted!', form.value);
    } else {
      console.log('Form not valid!');
    }

    this.toCreate = {
      firstname: form.value.firstname,
      lastname: form.value.lastname,
      matrNumber: form.value.matrNumber,
      email: form.value.email,
      password: form.value.password
    }

    this.userService.createUser(this.toCreate).subscribe({
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
