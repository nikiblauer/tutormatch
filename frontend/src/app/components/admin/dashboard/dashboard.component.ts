import { Component, OnInit } from "@angular/core";
import { AdminService } from "src/app/services/admin.service";
import { SimpleStaticticsDto, ExtendedStatisticsDto, CoverageSubjectsStatisticsDto } from "src/app/dtos/statistics";
import { ToastrService } from "ngx-toastr";
import { AuthService } from "src/app/services/auth.service";
import { Router } from '@angular/router';

@Component({
  selector: "app-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrl: "./dashboard.component.scss",
})
export class DashboardComponent implements OnInit {
  statistics: SimpleStaticticsDto;
  statisticsListExtended: ExtendedStatisticsDto;
  coverageStatistics: CoverageSubjectsStatisticsDto[];
  listLimitExtendedStatistics: number = 5;
  listLimitCoverageStatistics: number = 5;

  constructor(private adminService: AdminService, private notification: ToastrService,
    private authService: AuthService, private router: Router
  ) { }

  ngOnInit() {
    if (this.authService.getUserRole() !== 'ADMIN' || !this.authService.isLoggedIn) {
      this.router.navigate(['/']);
      return;
    }

    this.adminService.getStatistics().subscribe(
      data => {
        this.statistics = data
      },
      error => {
        console.error(error);
        this.notification.error(error.error, "Error in fetching statistics!");
      }
    );

    this.adminService.getExtendedStatistics(this.listLimitExtendedStatistics).subscribe(
      data => {
        this.statisticsListExtended = { ...this.statisticsListExtended, ...data };
      },
      error => {
        console.error(error);
        this.notification.error(error.error, "Error in fetching extended statistics!");
      }
    );

    //get coverage statistics
    this.adminService.getCoverageStatistics(this.listLimitCoverageStatistics).subscribe(
      data => {
        this.coverageStatistics = data; 
      },
      error => {
        console.error(error);
        this.notification.error(error.error, "Error in fetching coverage statistics!");
      }
    )
  }

  getRatioClass(): string {
    const ratio = this.statistics?.ratioOfferedNeededSubjects;
    if (ratio >= 0.80 && ratio < 1 || ratio >= 1) {
      return 'text-good';
    } else if (ratio >= 0.65 && ratio < 0.80) {
      return 'text-warning';
    } else if (ratio < 0.65) {
      return 'text-danger';
    }
    return '';
  }

  shouldMarkRed(subject: CoverageSubjectsStatisticsDto): boolean {
    return subject.diff > 30;
  }
}
