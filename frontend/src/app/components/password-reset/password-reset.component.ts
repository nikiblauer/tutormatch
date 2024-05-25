import { Component } from '@angular/core';
import {FormGroup, FormsModule, NgForm} from "@angular/forms";
import {PasswordResetDto, SendPasswordResetDto} from "../../dtos/user";
import {UserService} from "../../services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerComponent, NgxSpinnerService} from "ngx-spinner";
import {NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'app-password-reset',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    NgxSpinnerComponent,
    NgClass
  ],
  templateUrl: './password-reset.component.html',
  styleUrl: './password-reset.component.scss'
})
export class PasswordResetComponent {
  form: FormGroup;
  passwordResetDto: PasswordResetDto = {
    password: '',
    repeatPassword: '',
  };
  submitted = false;
  // Error flag
  error = false;
  token = '';
  fieldTextType: boolean;


  constructor(private userService: UserService, private router: Router, private route: ActivatedRoute, private notification: ToastrService, private spinner: NgxSpinnerService) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.token = params.get('token');
    });
  }

  toggleFieldTextType() {
    this.fieldTextType = !this.fieldTextType;
  }

  onSubmit(form: NgForm) {
    this.spinner.show();
    if (this.passwordResetDto.password != '' && this.passwordResetDto.password != this.passwordResetDto.repeatPassword) {
      this.spinner.hide();
      this.notification.error( "Passwords don't match");
      return;
    }
    if (form.valid) {
      console.log(this.token);
      console.log(this.passwordResetDto);
      this.userService.changePasswordWithResetToken(this.token, this.passwordResetDto).subscribe({
          next: () => {
            this.spinner.hide();
            this.submitted = true
          },
          error: error => {
            this.spinner.hide();
            this.notification.error(error.error, "Password change failed");
            this.submitted = false;
            console.log("Error changing password",error.error);
          }
        }
      );
    } else {
      console.log('Invalid input');
    }
  }
}
