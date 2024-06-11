import { Component } from '@angular/core';
import {FeedbackDto} from "../../dtos/feedback";
import {ToastrService} from "ngx-toastr";
import {FeedbackService} from "../../services/feedback.service";
import {NgForOf} from "@angular/common";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [
    NgForOf,
    RouterLink
  ],
  templateUrl: './feedback.component.html',
  styleUrl: './feedback.component.scss'
})
export class FeedbackComponent {
  public receivedFeedback: FeedbackDto[] = [];

  constructor(private notification: ToastrService, private feedbackService: FeedbackService) {
  }
  ngOnInit(): void {
  this.getReceivedFeedback();
  }

    getReceivedFeedback() {
    this.feedbackService.getReceivedFeedback().subscribe({
      next: (receivedFeedback) => {
        this.receivedFeedback = receivedFeedback;
      },
      error: error => {
        console.log(error);
        if (error.status == 404) {
          this.receivedFeedback = [];
          return;
        }
        this.notification.error(error.error, "Something went wrong!");
      }
    });
  }
}
