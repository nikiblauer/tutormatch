import { Component, OnInit } from "@angular/core";
import { Subject } from "src/app/dtos/user";
import { AdminService } from "src/app/services/admin.service";
import { debounceTime, distinctUntilChanged } from "rxjs/operators";
import { Subject as RxSubject } from 'rxjs';
import { SubjectService } from "../../../services/subject.service";
import { HttpErrorResponse } from "@angular/common/http";
import { SubjectDetailDto } from "../../../dtos/subject";

@Component({
  selector: "app-subjects",
  templateUrl: "./subjects.component.html",
  styleUrls: ["./subjects.component.scss"],
})
export class SubjectComponent implements OnInit {
  constructor(private subjectService: SubjectService, private adminService: AdminService) {}

  searchSubject$ = new RxSubject<string>();
  loadSubjects = false;
  createdSubject: SubjectDetailDto = new SubjectDetailDto();
  edit: boolean = false;
  info: boolean = false;
  create: boolean = false;
  delete: boolean = false;

  searchQuery: string = '';
  subjects: Subject[];
  message: string = '';
  showErrorMessage: boolean;
  showSuccessMessage: boolean;

  selectedSubject: SubjectDetailDto;
  subject: Subject = null;

  subjectToDelete: SubjectDetailDto = null;

  ngOnInit() {
    this.searchSubjects();
    this.searchSubject$.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(query => {
      this.searchSubjects(query);
    });
  }

  onSearchChange(): void {
    this.searchSubject$.next(this.searchQuery);
  }

  searchSubjects(query: string = ''): void {
    this.subjectService.getSubjects(query, 0, 100)
      .subscribe(subjects => {
        this.subjects = subjects.content;
        this.loadSubjects = true;
      });
  }

  closeSubjectInfo() {
    this.info = false;
    this.selectedSubject = null;
  }

  closeCreate() {
    this.create = false;
    this.createdSubject = new SubjectDetailDto();
  }

  onInfo(subject: Subject) {
    if (!this.selectedSubject) {
      this.subjectService.getSubjectById(subject.id).subscribe(s => {
        this.selectedSubject = s;
      });
    }
    this.info = true;
  }

  onEdit(subject: Subject) {
    if (this.info != true) {
      this.subjectService.getSubjectById(subject.id).subscribe(s => {
        this.selectedSubject = s;
      });
    }
    this.info = false;
    this.edit = true;
  }

  onDelete(id: number, event: Event) {
    event.stopPropagation();
    this.subjectService.getSubjectById(id).subscribe(subject => {
      this.subjectToDelete = subject;
      this.delete = true;
    });
  }

  confirmDelete() {
    this.adminService.deleteSubject(this.subjectToDelete.id).subscribe({
      next: _ => {
        this.delete = false;
        this.updateSubjectList();
        this.showMessageWithTimeout("Successfully deleted subject!", false);
      },
      error: (e) => this.handleError(e)
    });
  }

  closeDeleteDialog() {
    this.delete = false;
    this.subjectToDelete = null;
  }

  closeErrorMessage() {
    this.showErrorMessage = false;
  }

  closeSuccessMessage() {
    this.showSuccessMessage = false;
  }

  private handleError(error: HttpErrorResponse) {
    console.log(error);
    if (error.status === 400 || error.status === 422) {
      const errorString = error.error;
      const startIndex = errorString.indexOf("[");
      const endIndex = errorString.lastIndexOf("]");
      const contents = errorString.substring(startIndex + 1, endIndex);
      this.showMessageWithTimeout(contents, true);
    } else {
      this.showMessageWithTimeout(error.error, true);
      "Something went wrong. Please try again later.";
    }
  }

  private showMessageWithTimeout(message: string, isError: boolean) {
    this.message = message;
    if (isError) {
      this.showErrorMessage = true;
    } else {
      this.showSuccessMessage = true;
    }
    setTimeout(() => {
      this.showErrorMessage = false;
      this.showSuccessMessage = false;
    }, 1000); // Hide the message after 1 seconds
    this.updateSubjectList();
  }

  private updateSubjectList() {
    this.searchSubjects();
    this.searchSubject$.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(query => {
      this.searchSubjects(query);
    });
  }

  closeSubjectEdit() {
    this.edit = false;
    this.selectedSubject = null;
  }

  closeSubjectCreate() {
    this.create = false;
    this.createdSubject = new SubjectDetailDto();
  }

  saveSelectedSubject(event: Event) {
    this.adminService.updateSubject(this.selectedSubject).subscribe({
      next: _ => {
        event.stopPropagation();
        this.edit = false;
        this.showMessageWithTimeout("Successfully updated subject information!", false);
      },
      error: (e) => this.handleError(e)
    });
  }
  saveNewSubject(subject: SubjectDetailDto, event: Event){
    this.adminService.createSubject(subject).subscribe({
      next: _ => {
        event.stopPropagation();
        this.create = false;
        this.showMessageWithTimeout("Successfully created subject!", false);
      },
      error: (e) => this.handleError(e)
    });
  }
}
