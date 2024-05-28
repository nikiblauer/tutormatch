import { Component, OnInit } from "@angular/core";
import { AdminService } from "src/app/services/admin.service";
import { SimpleStaticticsDto, ExtendedStatisticsDto } from "src/app/dtos/statistics";
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
  listLimitExtendedStatistics: number = 5; //change here to get more or less statistical results 

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
}
