import {AfterViewInit, Component, OnInit} from "@angular/core";
import {Subject} from "src/app/dtos/user";
import {AdminService} from "src/app/services/admin.service";
import {debounceTime, distinctUntilChanged} from "rxjs/operators";
import {Subject as RxSubject} from 'rxjs';
import {SubjectService} from "../../../services/subject.service";
import {HttpErrorResponse} from "@angular/common/http";
import {SubjectDetailDto} from "../../../dtos/subject";
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";
import {Router} from '@angular/router';
import {AuthService} from "src/app/services/auth.service";

@Component({
  selector: "app-subjects",
  templateUrl: "./subjects.component.html",
  styleUrls: ["./subjects.component.scss"],
})
export class SubjectComponent implements OnInit, AfterViewInit {
  constructor(private subjectService: SubjectService, private adminService: AdminService,
              private notification: ToastrService, private spinner: NgxSpinnerService,
              private authService: AuthService, private router: Router) {
  }

  searchSubject$ = new RxSubject<string>();
  loadSubjects = false;
  createdSubject: SubjectDetailDto = new SubjectDetailDto();

  searchQuery: string = '';
  subjects: Subject[];

  selectedSubject: SubjectDetailDto;

  subjectToDelete: SubjectDetailDto = new SubjectDetailDto();
  public autofillUrlInput: string = '';

  ngOnInit() {

    if (this.authService.getUserRole() !== 'ADMIN' || !this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
      return;
    }

    this.searchSubjects();
    this.searchSubject$.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe({
      next: (query) => {
        this.searchSubjects(query);
      },
      error: error => {
        console.error("Error when loading subjects", error);
        this.notification.error(error.error, "Loading subjects failed!");
      }
    });
  }

  ngAfterViewInit(): void {
    const deleteModalElement = document.getElementById('openDeleteDialog');
    const infoModalElement = document.getElementById('openSubjectInfo1');
    const editModalElement = document.getElementById('openSubjectEdit');
    const createModalElement = document.getElementById('openSubjectCreate');

    if (deleteModalElement) {
      deleteModalElement.addEventListener('hidden.bs.modal', () => {
        this.closeDeleteDialog();
      });
    }
    if (infoModalElement) {
      infoModalElement.addEventListener('hidden.bs.modal', () => {
        if (!editModalElement) {
          this.closeSubjectInfo();
        }
      });
    }
    if (editModalElement) {
      editModalElement.addEventListener('hidden.bs.modal', () => {
        if (!infoModalElement.classList.contains('show')) {
          this.closeSubjectEdit();
        }
      });
    }
    if (createModalElement) {
      deleteModalElement.addEventListener('show.bs.modal', () => {
        this.closeSubjectCreate();
      });
    }
  }

  onSearchChange(): void {
    this.searchSubject$.next(this.searchQuery);
  }

  searchSubjects(query: string = ''): void {
    let timeout = setTimeout(() => {
      this.spinner.show();
    }, 1500);
    this.subjectService.getSubjects(query, 0, 100)
      .subscribe({
        next: subjects => {
          clearTimeout(timeout);
          this.spinner.hide();
          this.subjects = subjects.content;
          this.loadSubjects = true;
        },
        error: error => {
          clearTimeout(timeout);
          this.spinner.hide();
          console.error("Error when loading subjects", error);
          this.notification.error(error.error, "Loading subjects failed!");
        }
      });
  }

  closeSubjectInfo() {
    this.selectedSubject = new SubjectDetailDto();
  }

  onInfo(subject: Subject) {
    let timeout = setTimeout(() => {
    }, 1500);
    this.subjectService.getSubjectById(subject.id).subscribe(s => {
      clearTimeout(timeout);
      this.selectedSubject = s;
    });
  }

  onEdit(subject: Subject) {
      let timeout = setTimeout(() => {
      }, 1500);
      this.subjectService.getSubjectById(subject.id).subscribe(s => {
        clearTimeout(timeout);
        this.selectedSubject = s;
      });

  }

  onDelete(id: number, event: Event) {
    event.stopPropagation();
    let timeout = setTimeout(() => {
      this.spinner.show();
    }, 1500);
    this.subjectService.getSubjectById(id).subscribe({
        next: subject => {
          clearTimeout(timeout);
          this.spinner.hide();
          this.subjectToDelete = subject;
        },
        error: (e => {
          clearTimeout(timeout);
          this.spinner.hide();
          this.notification.error(e.error, "Maybe it was already deleted?")
        })
      }
    );
  }


  confirmDelete() {
    this.spinner.show();
    this.adminService.deleteSubject(this.subjectToDelete.id).subscribe({
      next: _ => {
        this.spinner.hide();
        this.updateSubjectList();
        this.notification.success("Successfully deleted subject", "Deleted subject!");
      },
      error: (e => {
        this.spinner.hide();
        if (e.status != 404) {
          this.handleError(e);
        }
      })
    });
  }

  closeDeleteDialog() {
    this.subjectToDelete = new SubjectDetailDto();
  }

  private handleError(error: HttpErrorResponse) {
    if (error.error) {
      let errorMessage = error.error;
      errorMessage = errorMessage.replace(/[\[\]]/g, ''); // remove brackets
      const errorMessages = errorMessage.split(', ');
      for (const message of errorMessages) {
        this.notification.error(message.trim(), "Validation Error");
      }
    } else {
      this.notification.error("Something went wrong. Please try again later.")
    }
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

  }

  closeSubjectCreate() {
    this.createdSubject = new SubjectDetailDto();
  }

  saveSelectedSubject(event: Event) {
    this.spinner.show();
    this.adminService.updateSubject(this.selectedSubject).subscribe({
      next: _ => {
        this.spinner.hide();
        event.stopPropagation();
        this.notification.success("Successfully updated subject information", "Updated subject information!")
      },
      error: (e) => {
        this.spinner.hide();
        this.handleError(e)
      }
    });
  }

  saveNewSubject(subject: SubjectDetailDto, event: Event) {
    this.spinner.show();
    this.adminService.createSubject(subject).subscribe({
      next: _ => {
        this.spinner.hide();
        event.stopPropagation();
        this.notification.success("Successfully created subject", "Created subject!")
        this.autofillUrlInput = "";
      },
      error: (e) => {
        this.spinner.hide();
        this.handleError(e)
        this.autofillUrlInput = "";
      }
    });
  }

  autofillUrl() {
    const urlObj = new URL(this.autofillUrlInput);
    const params = new URLSearchParams(urlObj.search);

    this.adminService.getPreviewSubject(params.get("courseNr"), params.get("semester"))
      .subscribe({
        next: previewSubject => {
          this.createdSubject = {
            ...previewSubject,
            id: null
          };
        },
        error: (e) => {
          this.handleError(e)
        }
      });
  }
}
