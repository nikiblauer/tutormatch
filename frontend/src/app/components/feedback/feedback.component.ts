import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FeedbackDto} from "../../dtos/feedback";
import {ToastrService} from "ngx-toastr";
import {FeedbackService} from "../../services/feedback.service";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {NgxSpinnerService} from "ngx-spinner";
import {ReportService} from "../../services/report.service";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [
    NgForOf,
    RouterLink,
    FormsModule,
    DatePipe
  ],
  templateUrl: './feedback.component.html',
  styleUrl: './feedback.component.scss'
})
export class FeedbackComponent implements OnInit{
  public receivedFeedback: FeedbackDto[] = [];
  public writtenFeedback: FeedbackDto[] = [];
  reportReason: string = "";
  reportFeedback: number;

  constructor(private notification: ToastrService,
              private feedbackService: FeedbackService,
              private spinner: NgxSpinnerService,
              private reportService: ReportService) {
  }
  ngOnInit(): void {
  this.getReceivedFeedback();
  this.getWrittenFeedback();
  }

    getReceivedFeedback() {
    this.feedbackService.getReceivedFeedbackSelf().subscribe({
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

  getWrittenFeedback() {
    this.feedbackService.getWrittenFeedbackSelf().subscribe({
      next: (writtenFeedback) => {
        this.writtenFeedback = writtenFeedback;
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
  deleteFeedback(id): void {
    this.spinner.show();
    this.feedbackService.deleteFeedback(id).subscribe({
        next: () => {
          this.notification.success("Feedback successfully deleted.");
          this.spinner.hide();
          this.getReceivedFeedback();
          this.getWrittenFeedback();
        },
        error: error => {
          this.spinner.hide();
          console.error("Error deleting feedback", error);
          this.notification.error(error.error, "Something went wrong!");
        }
      }
    );
  }

  submitReport() {
    console.log(this.reportFeedback);
    this.reportService.reportUserFeedback(this.reportFeedback, this.reportReason).subscribe({
        next: () => {
          this.notification.success("Successfully Reported.");
        },
        error: error => {
          console.error("Error reporting", error);
          this.notification.error(error.error, "Something went wrong!");
        }
      }
    );
  }
}
