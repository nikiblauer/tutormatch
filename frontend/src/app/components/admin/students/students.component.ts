import { Component, OnInit } from "@angular/core";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { StudentDto } from "src/app/dtos/user";
import { AdminService } from "src/app/services/admin.service";
import { StudentSubjectInfoDto } from "src/app/dtos/user";
import { Subject } from "rxjs";
import { debounceTime } from "rxjs/operators";
import { Page } from "src/app/dtos/page";
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";

interface StudentListing {
  id: number;
  firstname: string;
  lastname: string;
  email: string;
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

  constructor(private modalService: NgbModal, private adminService: AdminService, private notification: ToastrService, private spinner: NgxSpinnerService) {
    this.searchTerm$.pipe(
      debounceTime(400)
    ).subscribe(() => this.search());
  }

  ngOnInit(): void {
    this.search(false);
  }

  page = 0; // Current page number (0-indexed)

  search(newSearch: boolean = true): void {
    // Reset the page count and clear the list of filtered students only if it's a new search
    if (newSearch) {
      this.page = 0;
      this.filteredStudents = []; // Clear the list of filtered students
    }

    let timeout = setTimeout(() => {
      this.spinner.show();
    }, 1500);
    this.adminService.searchUsers(this.searchName, Number(this.matrNumber), this.page, 5).subscribe({
      next: (response: Page<StudentDto>) => {
        clearTimeout(timeout);
        this.spinner.hide();
        if (response.content.length === 0) {
          this.noMoreResults = true; // Set noMoreResults to true when there are no more results
        } else {
          this.noMoreResults = false;
          this.filteredStudents = [...this.filteredStudents, ...response.content.map(user => ({
            firstname: user.firstname,
            lastname: user.lastname,
            email: user.email,
            id: user.id
          }))];
        }
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
}
