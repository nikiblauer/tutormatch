import { Component } from '@angular/core';
import { FeedbackDto } from "../../../dtos/feedback";
import { ToastrService } from "ngx-toastr";
import { NgxSpinnerService } from "ngx-spinner";
import { AdminService } from "../../../services/admin.service";
import { StudentSubjectInfoDto } from "../../../dtos/user";
import { ActivatedRoute, Router } from "@angular/router";
import { FormGroup } from "@angular/forms";
import { NgbActiveModal, NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { FormBuilder, Validators } from '@angular/forms';
@Component({
  selector: 'app-admin-feedback',
  templateUrl: './admin-feedback.component.html',
  styleUrls: ['./admin-feedback.component.scss']
})
export class AdminFeedbackComponent {
  public writtenFeedback: FeedbackDto[] = [];
  selectedStudentDetails: StudentSubjectInfoDto;
  userId: number;
  banReason: string = "";

  banForm: FormGroup;

  constructor(private modalService: NgbModal, private notification: ToastrService, private adminService: AdminService, private route: ActivatedRoute, private router: Router, private spinner: NgxSpinnerService,
    private fb: FormBuilder) {
  }
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.userId = Number(params.get('id'));
    });
    this.getUserDetails(this.userId);
    this.getWrittenFeedback(this.userId); 

    this.banForm = this.fb.group({
      banReason: ['', Validators.required]
    });
  }

  getUserDetails(id: number) {
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
  getWrittenFeedback(id: number) {
    this.adminService.getWrittenFeedback(this.userId).subscribe({
      next: (writtenFeedback) => {
        this.writtenFeedback = writtenFeedback;
      },
      error: error => {
        console.error(error);
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
  openBanModal(content: any) {
    this.modalService.open(content);
  }
  onBanSubmit(modal: NgbActiveModal) {
    if (this.banForm.valid) {
      const reason = this.banForm.value.banReason;
      this.adminService.banUser(this.userId, reason).subscribe({
        next: (_) => {
          this.notification.success(`User  ${this.selectedStudentDetails} banned`);
        },
        error: (error: any) => {
          console.error('Error:', error);
          this.notification.error(error.error, "Error while banning student!");
        }
      });

      modal.close('User banned');
    }
  }
}
