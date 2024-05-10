import { Component } from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-verify',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './verify.component.html',
  styleUrl: './verify.component.scss'
})
export class VerifyComponent {
  verified: boolean = false;
}
