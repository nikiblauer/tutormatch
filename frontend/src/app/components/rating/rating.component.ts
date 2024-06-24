import {Component, Input, OnInit} from '@angular/core';
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";
import {RatingService} from "../../services/rating.service";
import {FeedbackService} from "../../services/feedback.service";
import {FeedbackDto} from "../../dtos/feedback";

@Component({
  selector: 'app-star-rating',
  templateUrl: './rating.component.html',
  styleUrls: ['./rating.component.scss']
})
export class StarRatingComponent implements OnInit {
  @Input() rating: number = 0;
  @Input() isEditable: boolean = true;
  @Input() amount: number = 0;
  @Input() ratedUserId: number;
  @Input() feedbackActive: boolean = false;
  ratingStars = [];
  public chatExists: boolean = false;
  public postedFeedback: FeedbackDto[] = [];
  submitted = false;
  feedbackText: string = '';

  constructor(private notification: ToastrService, private spinner: NgxSpinnerService, private ratingService: RatingService, private feedbackService: FeedbackService) {
  }

  ngOnInit(): void {
    let temp = (1 + this.rating) * 100
    this.ratingStars = Array(5).fill(0)
    for (let i = 0; i < temp; i++) {
      if (temp <= 0) {
        continue;
      }
      this.ratingStars[i] = Math.max(Math.min(temp -= 100, 100), 0);
    }
    if (this.feedbackActive){
      this.getChatExists();
      this.getPostedFeedback();
    }
  }

  getChatExists() {
    if (!this.isEditable) return;
    this.feedbackService.getChatExists(this.ratedUserId).subscribe({
      next: () => {
        this.chatExists = true;
      },
      error: error => {
        if (error.status == 404) {
          this.chatExists = false;
          return;
        }
        this.notification.error(error.error, "Something went wrong!");
      }
    });
  }

  getPostedFeedback() {
    if (!this.isEditable) return;
    this.feedbackService.getPostedFeedback(this.ratedUserId).subscribe({
      next: (postedFeedback) => {
        this.postedFeedback = postedFeedback;
      },
      error: error => {
        console.error(error);
        if (error.status == 404) {
          this.postedFeedback = [];
          return;
        }
        this.notification.error(error.error, "Something went wrong!");
      }
    });
  }

  setRating(newRating: number): void {
    if (!this.isEditable) return;
    this.rating = newRating;
    this.ratingService.rateUser(this.ratedUserId, newRating).subscribe({
      next: () => {
        this.spinner.hide();
        this.notification.success("Successfully updated ratings!");
      },
      error: error => {
        this.spinner.hide();
        console.error("Error when setting new rating", error);
        this.notification.error(error.error, "Something went wrong!");
      }
    });
  }

  postFeedback(form): void {
    if (!this.isEditable) return;
    this.spinner.show();
    this.submitted = true;
    let feedbackDto = new FeedbackDto();
    feedbackDto.feedback = this.feedbackText;
    feedbackDto.rated = this.ratedUserId;
    this.feedbackService.postFeedback(feedbackDto).subscribe({
        next: () => {
          this.spinner.hide();
          this.notification.success("Feedback submitted. Thank you!");
          this.getPostedFeedback();
          this.feedbackText = '';
        },
        error: error => {
          this.spinner.hide();
          console.error("Error posting feedback", error);
          this.notification.error(error.error, "Something went wrong!");
        }
      }
    );
    this.submitted = false;
    this.spinner.hide();
  }

  deleteFeedback(id): void {
    if (!this.isEditable) return;
    this.spinner.show();
    this.feedbackService.deleteFeedback(id).subscribe({
        next: () => {
          this.notification.success("Feedback successfully deleted.");
          this.spinner.hide();
          this.getPostedFeedback();
          this.feedbackText = '';
        },
        error: error => {
          this.spinner.hide();
          console.error("Error deleting feedback", error);
          this.notification.error(error.error, "Something went wrong!");
        }
      }
    );
  }

  getCharsLeft() {
    return this.feedbackText.length;
  }

}
