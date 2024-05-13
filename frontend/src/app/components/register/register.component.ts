import { Component } from '@angular/core';
import {FormsModule, NgForm} from "@angular/forms";
import {UserService} from "../../services/user.service";
import {CreateApplicationUserDto} from "../../dtos/user";
import {Router} from "@angular/router";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule,
    NgIf
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  firstName: string = '';
  lastName: string = '';
  email: string = '';
  password: string = '';
  matrNumber: number;
  submitted: boolean = false;

  toCreate: CreateApplicationUserDto;

  constructor(private userService: UserService, private router: Router) {

  }

  onSubmit() {
    console.log(this.firstName);
    console.log(this.lastName);
    console.log(this.email);
    console.log(this.matrNumber)
    console.log(this.password);

    this.toCreate = new CreateApplicationUserDto();
    this.toCreate.firstname = this.firstName;
    this.toCreate.lastname = this.lastName;
    this.toCreate.email = this.email;
    this.toCreate.matrNumber = this.matrNumber;
    this.toCreate.password = this.password;

    this.userService.createUser(this.toCreate).subscribe({
        next: () => {
          this.submitted = true
        },
        error: error => {
          console.log("Error when creating user");
        }
      }
    );

  }
}
