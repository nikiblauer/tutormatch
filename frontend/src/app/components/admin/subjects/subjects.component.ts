import {Component, OnInit} from "@angular/core";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {ApplicationUserDetailDto, ApplicationUserDto, Subject, UserProfile} from "src/app/dtos/user";
import {AdminService} from "src/app/services/admin.service";
import {debounceTime, distinctUntilChanged} from "rxjs/operators";
import {Subject as RxSubject} from 'rxjs';
import {UserService} from "../../../services/user.service";
import {SubjectService} from "../../../services/subject.service";
import {HttpErrorResponse} from "@angular/common/http";
import {SubjectDetailDto} from "../../../dtos/subject";

@Component({
  selector: "app-subjects",
  templateUrl: "./subjects.component.html",
  styleUrls: ["./subjects.component.scss"],
})

export class SubjectComponent implements OnInit {
  constructor(private userService: UserService, private subjectService: SubjectService, private adminService: AdminService) {
  }

  searchSubject$ = new RxSubject<string>();

  // view ist loaded it this variables is true
  loadUser = false;
  loadSubjects = false

  edit: boolean = false;
  info: boolean = false;

  //profile information
  user: UserProfile;

  // user for edit function
  editedUser: UserProfile;

  //search variables for query and loaded subjects
  searchQuery: string = '';
  subjects: Subject[];

  //flag to check if profile was edited
  userInfoChanged = false;

  // message to display error or success messages
  message: string = '';
  showErrorMessage: boolean;
  showSuccessMessage: boolean;


  //Subject for info modal
  selectedSubject: SubjectDetailDto;

  ngOnInit() {
    this.searchSubjects();
    this.searchSubject$.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(query => {
      this.searchSubjects(query);
    });

    // this.userNeed = this.user.subjects.filter(item => item.role == "trainee").map(item => item.name);
    // this.userOffer = this.user.subjects.filter(item => item.role == "tutor").map(item => item.name);
    // this.userAddress = this.user.getAddressAsString();
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

  onInfo(subject: Subject) {
    if (!this.selectedSubject) {
      this.subjectService.getSubjectById(subject.id).subscribe(s => {
        this.selectedSubject = s;
      });
    }
    this.info = true;
  }

  onEdit(subject: Subject) {
    if (this.info != true){
        this.subjectService.getSubjectById(subject.id).subscribe(s => {
          this.selectedSubject = s;
        });
    }
    this.info = false;
    this.edit = true;
  }

  onDelete(id: number, event:Event) {
    this.adminService.deleteSubject(id).subscribe({
      next: _ => {
        event.stopPropagation();
        this.edit = false;
        this.info = false;
      },
      error: (e) => this.handleError(e),
      complete: () => this.showMessageWithTimeout("Successfully deleted subject!", false)
    });

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

  private updateSubjectList(){
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

  saveSelectedSubject(event: Event) {
    this.adminService.updateSubject(this.selectedSubject).subscribe({
      next: _ => {
        event.stopPropagation();
        this.edit = false;
      },
      error: (e) => this.handleError(e),
      complete: () => this.showMessageWithTimeout("Successfully updated subject information!", false)
    });
  }
}
