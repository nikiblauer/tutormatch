import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { SubjectService } from 'src/app/services/subject.service';
import { UserProfile, Subject, StudentDto } from 'src/app/dtos/user';
import { HttpErrorResponse } from '@angular/common/http';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Subject as RxSubject } from 'rxjs';
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";
import {AdminService} from "../../services/admin.service";
import {ActivatedRoute, Router} from "@angular/router";

export enum UserMode {
  admin,
  user,
}

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent implements OnInit {

  constructor(private userService: UserService, private adminService: AdminService, private router: Router, private route: ActivatedRoute, private subjectService: SubjectService, private notification: ToastrService, private spinner: NgxSpinnerService) {
  }

  searchSubject$ = new RxSubject<string>();

  // view ist loaded it this variables is true
  loadUser = false;
  loadSubjects = false
  mode: UserMode = UserMode.user;

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
  id: number;

  //Subject for info modal
  selectedSubject: Subject;

  ngOnInit() {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });
    this.route.paramMap.subscribe(params => {
      this.id = Number(params.get('id'));
    });
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
      this.spinner.show();
      if (this.mode == UserMode.admin){
        this.adminService.addSubjectToUser(this.id, this.userOffer.map(item => item.id), this.userNeed.map(item => item.id))
          .subscribe({
            next: _ => {
              this.spinner.hide();
              this.updateUser()
            },
            error: (e) => {
              this.spinner.hide();
              this.handleError(e)
            },
            complete: () => this.notification.success("Successfully updated user subjects for user " + this.user.id, "Updated user subjects!")
          });
      } else {
        this.userService.addSubjectToUser(this.userOffer.map(item => item.id), this.userNeed.map(item => item.id))
          .subscribe({
            next: _ => {
              this.spinner.hide();
              this.updateUser()
            },
            error: (e) => {
              this.spinner.hide();
              this.handleError(e)
            },
            complete: () => this.notification.success("Successfully updated user subjects", "Updated user subjects!")
        });
      }
  }

  updateInfo(): void {
    this.userInfoChanged = true;
    let timeout = setTimeout(() => {
      this.spinner.show();
    }, 1500);
    if (this.mode == UserMode.admin) {
      this.adminService.updateUserDetails(this.editedUser)
        .subscribe({
          next: _ => {
            clearTimeout(timeout);
            this.spinner.hide();
            this.updateUser()
            this.userInfoChanged = false;
          },
          error: (e) => {
            clearTimeout(timeout);
            this.spinner.hide();
            this.handleError(e);
          },
          complete: () => this.notification.success("Successfully updated user information!", "Updated user information!")
        });

    } else {
      this.userService.updateUser(this.editedUser)
      .subscribe({
        next: _ => {
          clearTimeout(timeout);
          this.spinner.hide();
          this.updateUser()
          this.userInfoChanged = false;
        },
        error: (e) => {
          clearTimeout(timeout);
          this.spinner.hide();
          this.handleError(e);
        },
        complete: () => this.notification.success("Successfully updated user information!", "Updated user information!")
      });
    }
  }

  updateUser() {
    let timeout = setTimeout(() => {
      this.spinner.show();
    }, 1500);
    if (this.mode == UserMode.admin) {
      this.adminService.getUserSubjects(this.id)
        .subscribe({
          next: userProfile => {
            clearTimeout(timeout);
            this.spinner.hide();
            this.loadUser = true;
            this.user = userProfile;
            this.userOffer = userProfile.subjects.filter(item => item.role == "trainee");
            this.userNeed = userProfile.subjects.filter(item => item.role == "tutor");
            this.userAddress = StudentDto.getAddressAsString(userProfile);
            this.editedUser = { ...this.user };
            this.user.id  = this.id;
            this.updateFilterSubjects();
          },
          error: (e) => {
            clearTimeout(timeout);
            this.spinner.hide();
            this.handleError(e)
          }
        });
    } else {
      this.userService.getUserSubjects()
      .subscribe({
        next: userProfile => {
          clearTimeout(timeout);
          this.spinner.hide();
          this.loadUser = true;
          this.user = userProfile;
          this.userOffer = userProfile.subjects.filter(item => item.role == "trainee");
          this.userNeed = userProfile.subjects.filter(item => item.role == "tutor");
          this.userAddress = StudentDto.getAddressAsString(userProfile);
          this.editedUser = { ...this.user };
          this.user.id  = this.id;
          this.updateFilterSubjects();
        },
        error: (e) => {
          clearTimeout(timeout);
          this.spinner.hide();
          this.handleError(e)
        }
      });
    }
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
    console.log(error.error);
    if (error.status === 400 || error.status === 422) {
      const errorString = error.error;
      const startIndex = errorString.indexOf("[");
      const endIndex = errorString.lastIndexOf("]");
      const contents = errorString.substring(startIndex + 1, endIndex);
      const errorMessages = contents.split(', ');

      for (const message of errorMessages) {
        this.notification.error(message.trim(), "Error"); // trim to remove leading/trailing whitespaces
      }
    } else {
      this.notification.error(error.error, "Error")
    }
  }

  public get getHeadline() {
    switch (this.mode) {
      case UserMode.admin:
        return 'Profile of ' + this.user.firstname + ' ' + this.user.lastname;
      case UserMode.user:
        return 'My Profile'
      default:
        return '?';
    }
  }

  public get admin(){
    switch (this.mode) {
      case UserMode.admin:
        return 1;
      case UserMode.user:
        return 0;
      default:
        return 0;
    }
  }
}
