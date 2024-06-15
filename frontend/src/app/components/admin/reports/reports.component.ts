import {Component, OnInit} from '@angular/core';
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";
import {ReportDto} from "../../../dtos/report";
import {ReportService} from "../../../services/report.service";

@Component({
  selector: 'app-report',
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.scss'
})
export class ReportsComponent implements OnInit {
  public reports: ReportDto[] = [];

  constructor(private notification: ToastrService,
              private spinner: NgxSpinnerService,
              private reportService: ReportService) {
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
}
