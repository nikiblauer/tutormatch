import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-star-rating',
  templateUrl: './rating.component.html',
  styleUrls: ['./rating.component.scss']
})
export class StarRatingComponent implements OnInit{
  @Input() rating: number = 0;
  @Input() isEditable: boolean = true;
  ratingStars = [];

  setRating(newRating: number): void {
    if (!this.isEditable) return;
    this.rating = newRating;
  }

  ngOnInit(): void {
    let temp = (1+this.rating)*100
    this.ratingStars = Array(5).fill(0)
    for (let i = 0; i < temp; i++) {
      if (temp <= 0){
        continue;
      }
      this.ratingStars[i] = Math.max(Math.min(temp-=100,100),0);
    }
  }
}
