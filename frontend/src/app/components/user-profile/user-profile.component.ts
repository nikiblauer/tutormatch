import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { SubjectService } from 'src/app/services/subject.service';
import { ApplicationUserDetailDto, UserProfile, UserSubject, Subject, ApplicationUserDto } from 'src/app/dtos/user';
import { HttpErrorResponse } from '@angular/common/http';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Subject as RxSubject } from 'rxjs';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent implements OnInit {

  constructor(private userService: UserService, private subjectService: SubjectService, private notification: ToastrService) {
  }

  searchSubject$ = new RxSubject<string>();

  // view ist loaded it this variables is true
  loadUser = false;
  loadSubjects = false

  //profile information
  userAddress = '';
  user: UserProfile;


  // user for edit function
  editedUser: UserProfile;

  //tracking changes in user assigned subjects
  userNeed: Subject[];
  userOffer: Subject[];

  //search variables for query and loaded subjects
  searchQuery: string = '';
  subjects: Subject[];
  filteredSubjects: Subject[];

  //flag to check if profile was edited
  userInfoChanged = false;


  //Subject for info modal
  selectedSubject: Subject;

  ngOnInit() {
    this.updateUser()
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

  removeSubject(subject: Subject): void {
    this.userNeed = this.userNeed.filter(us => us.id !== subject.id);
    this.userOffer = this.userOffer.filter(us => us.id !== subject.id);
    this.updateFilterSubjects()
  }

  // Use the function to add a subject as a trainee
  addNeed(subject: Subject): void {
    if (!this.isSubjectInList(subject)) {
      this.userNeed.push(subject);
      this.updateFilterSubjects();
    }
  }

  // Use the function to add a subject as a tutor
  addOffer(subject: Subject): void {
    if (!this.isSubjectInList(subject)) {
      this.userOffer.push(subject);
      this.updateFilterSubjects()
    }
  }

  isSubjectInList(subject: Subject): boolean {
    return this.userOffer.some(offer => offer.id === subject.id) || this.userNeed.some(need => need.id === subject.id);
  }

  searchSubjects(query: string = ''): void {
    this.subjectService.getSubjects(query, 0, 100)
      .subscribe(subjects => {
        this.subjects = subjects.content;
        this.loadSubjects = true;
        this.filteredSubjects = this.subjects;
        this.updateFilterSubjects();
      });
  }

  updateFilterSubjects() {
    let ids: number[] = [];

    if (this.userNeed) {
      ids = ids.concat(this.userNeed.map(item => item.id));
    }

    if (this.userOffer) {
      ids = ids.concat(this.userOffer.map(item => item.id));
    }

    if (ids.length > 0 && this.subjects) {
      this.filteredSubjects = this.subjects.filter(item => !ids.includes(item.id));
    }

  }

  saveProfile(): void {
    this.userService.addSubjectToUser(this.userOffer.map(item => item.id), this.userNeed.map(item => item.id))
      .subscribe({
        next: _ => this.updateUser(),
        error: (e) => this.handleError(e),
        complete: () => this.notification.success("Successfully updated user subjects", "Updated user subjects!")
      });
  }

  updateInfo(): void {
    this.userInfoChanged = true;
    this.userService.updateUser(this.editedUser)
      .subscribe({
        next: _ => {
          this.updateUser()
          this.userInfoChanged = false;
        },
        error: (e) => this.handleError(e),
        complete: () => this.notification.success("Successfully updated user information!", "Updated user information!")
      });
  }

  updateUser() {
    this.userService.getUserSubjects()
      .subscribe({
        next: userProfile => {
          this.loadUser = true;
          this.user = userProfile;
          this.userOffer = userProfile.subjects.filter(item => item.role == "trainee");
          this.userNeed = userProfile.subjects.filter(item => item.role == "tutor");
          this.userAddress = ApplicationUserDetailDto.getAddressAsString(userProfile);
          this.editedUser = { ...this.user };
          this.updateFilterSubjects();
        },
        error: (e) => this.handleError(e)
      });
  }

  editUser() {
    this.editedUser = { ...this.user };
  }


  closeSubjectInfo() {
    this.selectedSubject = null;
  }

  openInfo(subject: Subject) {
    this.selectedSubject = subject;
  }


  private handleError(error: HttpErrorResponse) {
    console.log(error);
    if (error.status === 400 || error.status === 422) {
      const errorString = error.error;
      const startIndex = errorString.indexOf("[");
      const endIndex = errorString.lastIndexOf("]");
      const contents = errorString.substring(startIndex + 1, endIndex);

      this.notification.error(contents, "Error");
    } else {
      this.notification.error(error.error, "Error")
    }
  }

}
