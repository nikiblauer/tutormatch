import { Component, OnInit } from "@angular/core";
import { Subject } from "src/app/dtos/user";
import { AdminService } from "src/app/services/admin.service";
import { debounceTime, distinctUntilChanged } from "rxjs/operators";
import { Subject as RxSubject } from 'rxjs';
import { SubjectService } from "../../../services/subject.service";
import { HttpErrorResponse } from "@angular/common/http";
import { SubjectDetailDto } from "../../../dtos/subject";
import {isError} from "lodash";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: "app-subjects",
  templateUrl: "./subjects.component.html",
  styleUrls: ["./subjects.component.scss"],
})
export class SubjectComponent implements OnInit {
  constructor(private subjectService: SubjectService, private adminService: AdminService, private notification: ToastrService) {
  }

  searchSubject$ = new RxSubject<string>();
  loadSubjects = false;
  createdSubject: SubjectDetailDto = new SubjectDetailDto();
  edit: boolean = false;
  info: boolean = false;
  create: boolean = false;
  delete: boolean = false;

  searchQuery: string = '';
  subjects: Subject[];

  selectedSubject: SubjectDetailDto;

  subjectToDelete: SubjectDetailDto = null;

  ngOnInit() {
    this.searchSubjects();
    this.searchSubject$.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe( {
      next: (query) => {
        this.searchSubjects(query);
        },
      error: error => {
        console.error("Error when loading subjects", error);
        this.notification.error(error.error, "Loading subjects failed!");
      }
    });
  }

  onSearchChange(): void {
    this.searchSubject$.next(this.searchQuery);
  }

  searchSubjects(query: string = ''): void {
    this.subjectService.getSubjects(query, 0, 100)
      .subscribe({
        next: subjects => {
          this.subjects = subjects.content;
          this.loadSubjects = true;
        },
        error: error => {
          console.error("Error when loading subjects", error);
          this.notification.error(error.error, "Loading subjects failed!");
        }
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
    this.subjectService.getSubjectById(id).subscribe({
      next: subject =>{
      this.subjectToDelete = subject;
      this.delete = true;
    },
      error: (e =>{
        this.notification.error(e.error,"Maybe it was already deleted?")
      })
    }
  );
  }

  confirmDelete() {
    this.adminService.deleteSubject(this.subjectToDelete.id).subscribe({
      next: _ => {
        this.delete = false;
        this.updateSubjectList();
        this.notification.success("Successfully deleted subject", "Delted subject!");
      },
      error: (e => {
        if (e.status != 404){
          this.handleError(e);
        }
      })
    });
  }

  closeDeleteDialog() {
    this.delete = false;
    this.subjectToDelete = null;
  }

  private handleError(error: HttpErrorResponse) {
    console.log(error.error);
    this.notification.error(error.error, "Something went wrong. Please try again later.")
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
        this.notification.success("Successfully updated subject information", "Updated subject information!")
      },
      error: (e) => this.handleError(e)
    });
  }

  saveNewSubject(subject: SubjectDetailDto, event: Event) {
    this.adminService.createSubject(subject).subscribe({
      next: _ => {
        event.stopPropagation();
        this.create = false;
        this.notification.success("Successfully created subject", "Created subject!")
      },
      error: (e) => this.handleError(e)
    });
  }
}
