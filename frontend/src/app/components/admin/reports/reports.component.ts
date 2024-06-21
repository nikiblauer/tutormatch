import {Component, OnInit} from '@angular/core';
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";
import {ReportDto} from "../../../dtos/report";
import {ReportService} from "../../../services/report.service";
import {ChatService} from "../../../services/chat.service";
import {ChatMessageDto} from "../../../dtos/chat";
import {BannedUserDto} from "../../../dtos/user";
import {Observable} from "rxjs";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {NgbActiveModal, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {AdminService} from "../../../services/admin.service";
import {AuthService} from "../../../services/auth.service";
import {Router} from "@angular/router";
import {finalize, tap} from "rxjs/operators";

interface StudentListing {
  id: number;
  firstname: string;
  lastname: string;
  isBanned: boolean;
}
@Component({
  selector: 'app-report',
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.scss'
})
export class ReportsComponent implements OnInit {
  public reports: ReportDto[] = [];
  public selectedReport: ReportDto = null;
  messages: ChatMessageDto[];

  public reportToDelete: number;
  selectedBanUser: StudentListing = new class implements StudentListing {
    firstname: string;
    id: number;
    isBanned: boolean;
    lastname: string;
  };
  selectedBanReasonUser: BannedUserDto | null = null;
  banReason: string = "";

  banForm: FormGroup;

  constructor(private modalService: NgbModal, private adminService: AdminService,
              private notification: ToastrService, private spinner: NgxSpinnerService,
              private authService: AuthService, private router: Router, private fb: FormBuilder,
              private reportService: ReportService,private chatService: ChatService
  ) {
    this.banForm = this.fb.group({
      banReason: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(){
    this.reportService.getAllReports().subscribe({
      next: reports => {
        this.reports = reports;
      },
      error: err => {
        this.notification.error(err.error.errorMessage, "Something went wrong!");
      }
    });
  }

  public infoButton(r: ReportDto){
    this.selectedReport = r;
    if (r.chatRoomId != ""){
      this.chatService.getMessagesByChatRoomId(r.chatRoomId).subscribe({
        next: messages => {
        this.messages = messages;
        }, error: error => {
        console.error(error);
        this.notification.error(error.error, "Messages could not be loaded")
      }
    })
    }
  }

  withSpinner<T>(observable: Observable<T>, spinner: any): Observable<T> {
    spinner.show();
    return observable.pipe(
      tap(() => {
        spinner.show();
      }),
      finalize(() => {
        spinner.hide();
      })
    );
  }


  banUser(report: ReportDto, content: any): void {
    this.selectedBanUser.lastname = report.lastNameReported;
    this.selectedBanUser.firstname = report.firstnameReported;
    this.selectedBanUser.id = report.reportedId;
    this.modalService.open(content);
  }

  onBanSubmit(modal: NgbActiveModal) {
    if (this.banForm.valid) {
      const reason = this.banForm.value.banReason;

      this.withSpinner(this.adminService.banUser(this.selectedBanUser.id, reason), this.spinner)
        .subscribe({
          next: (_) => {
            this.banForm.reset();
            this.load();
          },
          error: (error: any) => {
            console.error('Error:', error);
            this.notification.error(error.error, "Error while banning student!");
          }
        });

      modal.close('User banned');
    }
  }

  deleteReportFirst(id: number, content: any){
    this.reportToDelete = id;
    this.modalService.open(content);
  }
  deleteReport(id: number, modal: any){
    this.reportService.deleteReport(id).subscribe({
      next: () => {
        this.notification.success( "Removed report!");
        this.load();
      }, error: error => {
        console.error(error);
        this.notification.error(error.error, "Report could not be removed")
      }
    });
    modal.close('Report deleted');
  }
}
