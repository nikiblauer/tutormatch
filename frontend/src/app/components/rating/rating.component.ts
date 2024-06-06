import {Component, Input, OnInit} from '@angular/core';
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";
import {RatingService} from "../../services/rating.service";

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
  ratingStars = [];

  constructor(private notification: ToastrService, private spinner: NgxSpinnerService, private ratingService: RatingService) {
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
  }

  setRating(newRating: number): void {
    if (!this.isEditable) return;
    this.rating = newRating;
    console.log(this.ratedUserId + ", " + newRating)
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
}
