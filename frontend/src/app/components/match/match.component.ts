import {Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";
import {UserMatchDto} from "../../dtos/user-match";
import {StudentDto, Subject} from "../../dtos/user";
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";
import {RatingService} from "../../services/rating.service";
import {provideRouter, Router} from "@angular/router";
import {CreateChatRoomDto} from "../../dtos/chat";
import {ChatService} from "../../services/chat.service";
import {timeout} from "rxjs";
import {forEach} from "lodash";
import contains from "@popperjs/core/lib/dom-utils/contains";


@Component({
  selector: 'app-match',
  templateUrl: './match.component.html',
  styleUrls: ['./match.component.scss']
})
export class MatchComponent implements OnInit {
  public placeholderMatches: UserMatchDto[] = [];
  public matches: UserMatchDto[] = [];
  public filteredMatches: UserMatchDto[] = [];
  public selectedMatch: UserMatchDto;
  public selectedUser: StudentDto;
  public selectedUserRating: number = -2;
  public filter: boolean;
  public filterNeeds: Subject[] = [];
  public filterOffers: Subject[] = [];
  public filterCourseNumNeeds: string[] = [];
  public filterCourseNumOffers: string[] = [];
  public matchNeeds: Subject[];
  public matchOffer: Subject[];

  constructor(private userService: UserService, private notification: ToastrService,
              private spinner: NgxSpinnerService, private ratingService: RatingService,
              private router: Router, private chatService: ChatService) {
  }

  ngOnInit() {
    let timeout = setTimeout(() => {
      this.spinner.show();
    }, 1500);
    this.userService.getUserMatcher().subscribe({
      next: (matches) => {
        clearTimeout(timeout);
        this.spinner.hide();
        this.matches = matches;
        this.placeholderMatches = this.matches;
      },
      error: error => {
        clearTimeout(timeout);
        this.spinner.hide();
        console.error("Error when retrieving matches", error);
        this.notification.error(error.error, "Something went wrong!");
      }
    });
  }

  public trimStringByComma(input: string) {
    const parts = input.split(',');
    for (let i = 0; i < parts.length; i++) {
      parts[i] = parts[i].substring(8);
    }
    if (parts.length > 3) {
      return parts.slice(0, 3).join(",\n") + ", ...";
    } else {
      return parts.join(",\n");
    }
  }

  public openMatch(match: UserMatchDto) {
    this.selectedMatch = match;
    let timeout = setTimeout(() => {
      this.spinner.show();
    }, 1500);
    this.userService.getUser(match.id).subscribe({
      next: (user) => {
        clearTimeout(timeout);
        this.spinner.hide();
        this.selectedUser = user;
      },
      error: error => {
        clearTimeout(timeout);
        this.spinner.hide();
        console.error("Error when user match details", error);
        this.notification.error(error.error, "Something went wrong!");
      }
    })
    this.ratingService.getRatingFromUser(match.id).subscribe({
      next: (value) => {
        this.selectedUserRating = value;
      },
      error: err => {
        clearTimeout(timeout);
        this.spinner.hide();
        console.error("Error when getting match rating", err);
        this.notification.error(err.error, "Something went wrong!");
      }
    })
  }

  public closeMatch() {
    this.selectedMatch = null;
    this.selectedUserRating = -2;
    this.reloadAll();
  }

  private reloadAll() {
    let timeout = setTimeout(() => {
      this.spinner.show();
    }, 1500);
    this.userService.getUserMatcher().subscribe({
      next: (matches) => {
        clearTimeout(timeout);
        this.spinner.hide();
        this.matches = matches;
      },
      error: error => {
        clearTimeout(timeout);
        this.spinner.hide();
        console.error("Error when retrieving matches", error);
        this.notification.error(error.error, "Something went wrong!");
      }
    });
  }

  public startChat() {
    let chatRoomToCreate = new CreateChatRoomDto()
    chatRoomToCreate.recipientId = this.selectedMatch.id;
    this.chatService.checkChatRoomExistsByRecipient(chatRoomToCreate.recipientId).subscribe({
        next: exits => {
          if (!exits) {
            this.chatService.createChatRoom(chatRoomToCreate).subscribe({
              next: value => {
                this.notification.success("Chat successfully created")
                this.router.navigate(["/chat"])
              }, error: error => {
                this.notification.error("Chat Creation Failed")
                console.log(error);
              }
            })
          } else {
            this.router.navigate(["/chat"])
          }

        }, error: err => {
          console.log(err)
        }
      }
    )

  }

  public getSelectedUserAddressAsString(user: StudentDto) {
    return StudentDto.getAddressAsString(user);
  }

  public openFilter() {
    this.filter = true;
    this.getUserSubjects();
  }

  public getUserSubjects() {
    this.userService.getUserSubjects().subscribe({
      next: userProfile => {
        this.spinner.hide();
        this.matchNeeds = userProfile.subjects.filter(item => item.role == "tutor");
        this.matchOffer = userProfile.subjects.filter(item => item.role == "trainee");
      },
      error: (e) => {
        console.log(e)
      }
    });
  }

  public applyFilter() {
    this.filterMatches();
    this.placeholderMatches = this.filteredMatches;
  }

  toggleSelection(course: Subject, isChecked: boolean, filterSubjects: Subject[]) {
    const index = filterSubjects.findIndex(item => item.id === course.id);

    if (isChecked && index === -1) {
      filterSubjects.push(course); // Add the course if it's checked and not already in the array
    } else if (!isChecked && index !== -1) {
      filterSubjects.splice(index, 1); // Remove the course if it's unchecked and in the array
    }
  }

  isSelected(item: Subject, collection: Subject[]): boolean {
    return collection.some(selectedItem => selectedItem.id === item.id);
  }

  filterMatches() {
    if(this.filterNeeds.length == 0 && this.filterOffers.length == 0){
      this.placeholderMatches = this.matches;
    }
    this.filteredMatches.length = 0;
    this.getCourseNumberArray(this.filterOffers, this.filterCourseNumOffers)
    this.getCourseNumberArray(this.filterNeeds, this.filterCourseNumNeeds)
    for (let i = 0; i < this.matches.length; i++) {
      const match = this.matches[i];

      const tutorSubjectsOfMatch = match.tutorSubjects.split(', ').map(subject => subject);
      const traineeSubjectsOfMatch = match.traineeSubjects.split(', ').map(subject => subject.trim());
      const containsAllOffers = this.filterCourseNumOffers.every(courseNum => {
        return tutorSubjectsOfMatch.some(tutorSubject => {
          return tutorSubject.includes(courseNum);
        });
      });
      const containsAllNeeds = this.filterCourseNumNeeds.every(courseNum => {
        return traineeSubjectsOfMatch.some(traineeSubject => {
          return traineeSubject.includes(courseNum);
        });
      });
      if(containsAllOffers && containsAllNeeds){
        this.filteredMatches.push(match)
      }
    }
  }

  extractCourseNumber(courseString: string): string | null {
    if (typeof courseString !== 'string') {
      throw new TypeError('Input must be a string');
    }

    const regex = /\b\d{3}\.\d{3}\b/;
    const match = courseString.match(regex);
    return match ? match[0] : null;
  }
  getCourseNumberArray(filterArray: Subject[], courseNumberArray: String[]): void {
    courseNumberArray.length = 0;
    for (let subject of filterArray) {
      const courseNumber = this.extractCourseNumber(subject.name); // Assuming 'name' holds the course string
      if (courseNumber) {
        courseNumberArray.push(courseNumber);
      }
    }
  }
}

