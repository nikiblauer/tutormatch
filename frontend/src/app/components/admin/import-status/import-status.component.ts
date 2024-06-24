import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subscription, interval, switchMap } from 'rxjs';
import { ImportStatusDto } from 'src/app/dtos/import';
import { ImportService } from 'src/app/services/import.service';

@Component({
  selector: 'app-import-status',
  templateUrl: './import-status.component.html',
  styleUrl: './import-status.component.scss'
})
export class ImportStatusComponent implements OnInit {
  importStatus: ImportStatusDto = {
    importDate: null,
    importId: null,
    progress: null,
    status: null
  };
  private statusSubscription: Subscription;

  loading: boolean = false;

  constructor(private importService: ImportService, private notification: ToastrService) { }

  ngOnInit(): void {
    this.startFetchingStatus();
  }

  ngOnDestroy(): void {
    this.stopFetchingStatus();
  }

  stopFetchingStatus(): void {
    if (this.statusSubscription) {
      this.statusSubscription.unsubscribe();
    }
  }

  fetchImportStatus(): void {
    this.importService.getImport().subscribe(
      {
        next: (data) => {
          this.updateImportStatus(data);
        },
        error: error => {
          console.error('Error fetching import status:', error);
          this.notification.error(error.error, "Error fetching import status!");
        }
      }
    );
  }

  startFetchingStatus(): void {
    // Fetch status immediately on component load
    this.fetchImportStatus();

    this.statusSubscription = interval(5000)
      .pipe(
        switchMap(() => this.importService.getImport())
      )
      .subscribe({
        next: data => this.updateImportStatus(data),
        error: error => {
          console.error('Error fetching import status:', error);
          this.notification.error(error.error, "Error fetching import status!");
        }
      });
  }

  updateImportStatus(data: ImportStatusDto): void {
    if (!data) {
      return;
    }
    this.importStatus = data;
    console.log("the status: " + this.importStatus.status + " the boolean: " + this.importStatus.status !== 'RUNNING')
    if (this.importStatus.status != 'RUNNING') {
      this.stopFetchingStatus(); //Stop fetching if status is not running initially
    }
  }

  startImport(): void {
    this.importService.startImport().subscribe(
      {
        next: (data) => {
          this.startFetchingStatus();
        },
        error: error => {
          console.error('Error starting import:', error);
          this.notification.error(error.error, "Error starting import!");
        }
      });
  }

  cancelImport(): void {
    if (this.importStatus && this.importStatus.importId) {
      this.importService.cancelImport(this.importStatus.importId).subscribe(
        {
          next: (data) => {
            this.startFetchingStatus(); // Refresh status after canceling import
          },
          error: error => {
            console.error('Error canceling import:', error);
            this.notification.error(error.error, "Error canceling import!");
          }
        }
      );
    }
  }
}
