import {Component, OnInit} from '@angular/core';
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";
import {ReportDto} from "../../../dtos/report";
import {ReportService} from "../../../services/report.service";
import {ChatService} from "../../../services/chat.service";
import {ChatMessageDto} from "../../../dtos/chat";

@Component({
  selector: 'app-report',
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.scss'
})
export class ReportsComponent implements OnInit {
  public reports: ReportDto[] = [];
  public selectedReport: ReportDto = null;
  messages: ChatMessageDto[];


  constructor(private notification: ToastrService,
              private spinner: NgxSpinnerService,
              private reportService: ReportService,
              private chatService: ChatService) {
  }

  ngOnInit(): void {
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
        console.log(error);
        this.notification.error(error.error, "Messages could not be loaded")
      }
    })
    }
  }
}
