import { Component, OnInit } from "@angular/core";
import { NgbModal, NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { BannedUserDto, StudentDto } from "src/app/dtos/user";
import { AdminService } from "src/app/services/admin.service";
import { StudentSubjectInfoDto } from "src/app/dtos/user";
import { Observable, Subject } from "rxjs";
import { debounceTime, finalize, tap } from "rxjs/operators";
import { Page } from "src/app/dtos/page";
import { ToastrService } from "ngx-toastr";
import { NgxSpinnerService } from "ngx-spinner";
import { AuthService } from "src/app/services/auth.service";
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';



interface StudentListing {
  id: number;
  firstname: string;
  lastname: string;
  email: string;
  isBanned: boolean;
}

@Component({
  selector: "app-students",
  templateUrl: "./students.component.html",
  styleUrls: ["./students.component.scss"],
})

export class StudentsComponent implements OnInit {
  students: StudentListing[] = [];
  filteredStudents: StudentListing[] = [];
  searchName: string = '';
  matrNumber: string = '';
  selectedStudent: StudentListing | null = null;
  selectedStudentDetails: StudentSubjectInfoDto | null = null;
  searchTerm$ = new Subject<string>();
  noMoreResults: boolean = false;

  selectedBanUser: StudentListing | null = null;
  selectedBanReasonUser: BannedUserDto | null = null;
  showFilter: boolean = false;

  tmpFilterStatus: string = "";
  filterStatus: string = "";
  banReason: string = "";

  banForm: FormGroup;


  constructor(private modalService: NgbModal, private adminService: AdminService,
    private notification: ToastrService, private spinner: NgxSpinnerService,
    private authService: AuthService, private router: Router, private fb: FormBuilder) {
    this.searchTerm$.pipe(
      debounceTime(400)
    ).subscribe(() => this.search());

    this.banForm = this.fb.group({
      banReason: ['', Validators.required]
    });
  }

  ngOnInit(): void {

    if (this.authService.getUserRole() !== 'ADMIN' || !this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
      return;
    }
    this.search(false);
  }

  page = 0; // Current page number (0-indexed)

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


  search(newSearch: boolean = true): void {
    // Reset the page count and clear the list of filtered students only if it's a new search
    if (newSearch) {
      this.page = 0;
      this.filteredStudents = []; // Clear the list of filtered students
    }

    this.withSpinner(
      this.adminService.searchUsers(this.searchName, Number(this.matrNumber), this.filterStatus, this.page, 5), this.spinner).subscribe({
        next: (response: Page<StudentDto>) => {
          this.spinner.hide();
          if (response.content.length === 0) {
            this.noMoreResults = true; // Set noMoreResults to true when there are no more results
          } else {
            this.noMoreResults = false;
            this.filteredStudents = [...this.filteredStudents, ...response.content.map(user => ({
              firstname: user.firstname,
              lastname: user.lastname,
              email: user.email,
              id: user.id,
              isBanned: user.isBanned
            }))];
          }
        },
        error: (error: any) => {
          this.spinner.hide();
          console.error('Error:', error);
          this.page -= 0; // Reset the page count if there was an error
          this.notification.error(error.error, "Error in fetching student details!");
        }
      });
  }

  viewDetails(student: StudentListing, content: any): void {
    this.selectedStudent = student;
    let timeout = setTimeout(() => {
      this.spinner.show();
    }, 1500);
    this.adminService.getUserDetails(student.id).subscribe({ //call getUserDetails endpoint with selected User ID
      next: (response: StudentSubjectInfoDto) => {
        clearTimeout(timeout);
        this.spinner.hide();
        this.selectedStudentDetails = response;
        this.modalService.open(content);
      },
      error: (error: any) => {
        clearTimeout(timeout);
        this.spinner.hide();
        console.error('Error:', error);
        this.page -= 0; // Reset the page count if there was an error
        this.notification.error(error.error, "Error in fetching student details!");
      }
    });
  }

  loadMore(): void {
    this.page += 1;
    this.search(false); // Don't clear the list of filtered students
  }


  banUser(student: StudentListing, content: any): void {
    this.selectedBanUser = student;
    this.modalService.open(content);
  }

  onBanSubmit(modal: NgbActiveModal) {
    if (this.banForm.valid) {
      const reason = this.banForm.value.banReason;
      // Your ban logic here
      console.log(`Banning user ${this.selectedBanUser} for reason: ${reason}`);

      this.withSpinner(this.adminService.banUser(this.selectedBanUser.id, reason), this.spinner)
        .subscribe({
          next: (_) => {
            this.search(true);
          },
          error: (error: any) => {
            console.error('Error:', error);
            this.notification.error(error.error, "Error while banning student!");
          }
        });

      // Close the modal here
      modal.close('User banned');
    }
  }


  applyFilters() {
    this.filterStatus = this.tmpFilterStatus;
    this.showFilter = !this.showFilter;
    this.search(true);
  }

  updateTmpFilterStatus(selectedValue: string) {
    this.tmpFilterStatus = selectedValue;
  }

  showBan(student: StudentListing, content: any) {
    this.selectedBanUser = student;
    this.modalService.open(content);
  }

  showBanReason(student: StudentListing, content: any) {
    this.withSpinner(this.adminService.getBannedUser(student.id), this.spinner)
      .subscribe({
        next: (response) => {
          this.selectedBanReasonUser = response
        },
        error: (error: any) => {
          console.error('Error:', error);
          this.notification.error(error.error, "Error while getting information of banned student!");
        }
      });
    this.modalService.open(content);
    // Implement your show ban reason logic
  }

  toggleFilter() {
    this.tmpFilterStatus = this.filterStatus;
    this.showFilter = !this.showFilter;
  }
}
