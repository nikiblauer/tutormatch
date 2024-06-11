import { Component } from '@angular/core';
import {FeedbackDto} from "../../../dtos/feedback";
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";
import {AdminService} from "../../../services/admin.service";
import {StudentSubjectInfoDto} from "../../../dtos/user";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {DatePipe, NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [
    DatePipe,
    NgIf,
    NgForOf,
    RouterLink
  ],
  templateUrl: './admin-feedback.component.html',
  styleUrl: './admin-feedback.component.scss'
})
export class AdminFeedbackComponent {
  public writtenFeedback: FeedbackDto[] = [];
  selectedStudentDetails: StudentSubjectInfoDto;
  userId: number;

  constructor(private notification: ToastrService, private adminService: AdminService, private route: ActivatedRoute, private router: Router, private spinner: NgxSpinnerService) {
  }
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.userId = Number(params.get('id'));
    });
    this.getUserDetails(this.userId);
    this.getWrittenFeedback(this.userId);
  }

  getUserDetails(id:number) {
    this.adminService.getUserDetails(id).subscribe({
      next: (response: StudentSubjectInfoDto) => {
        this.selectedStudentDetails = response;
      },
      error: (error: any) => {
        console.error('Error:', error);
        this.notification.error(error.error, "Error in fetching student details!");
      }
    });
  }
  getWrittenFeedback(id:number) {
    this.adminService.getWrittenFeedback(this.userId).subscribe({
      next: (writtenFeedback) => {
        this.writtenFeedback = writtenFeedback;
      },
      error: error => {
        console.log(error);
        if (error.status == 404) {
          this.writtenFeedback = [];
          return;
        }
        this.notification.error(error.error, "Something went wrong!");
      }
    });
  }
  deleteFeedback(feedbackId): void {
    this.spinner.show();
    this.adminService.deleteFeedbackById(feedbackId).subscribe({
        next: () => {
          this.spinner.hide();
          this.getWrittenFeedback(this.userId);
        },
        error: error => {
          this.spinner.hide();
          console.error("Error deleting feedback", error);
          this.notification.error(error.error, "Something went wrong!");
        }
      }
    );
  }

}
