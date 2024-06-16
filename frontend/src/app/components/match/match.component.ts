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
  public matches: UserMatchDto[] = [];
  public selectedMatch: UserMatchDto;
  public selectedUser: StudentDto;
  public selectedUserRating: number = -2;
  public filter: boolean;
  public filterSubjectsNeeds: string[] = [];
  public filterSubjectsOffers: string[] = [];
  userNeed: Subject[];
  userOffer: Subject[];
  filteredMatches: UserMatchDto[] = [];

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

  public saveSelectedCourses() {

    //console.log('Selected courses:', this.filterSubjectsNeeds);
    //console.log('Selected courses:', this.filterSubjectsOffers);
    this.filterMatches();
    //console.log(this.filteredMatches);
    // Here you can also send the selectedCourses array to your server using your service
  }

  public getUserSubjects() {
    this.userService.getUserSubjects().subscribe({
      next: userProfile => {
        this.spinner.hide();
        this.userOffer = userProfile.subjects.filter(item => item.role == "tutor");
        this.userNeed = userProfile.subjects.filter(item => item.role == "trainee");
      },
      error: (e) => {
      }
    });
  }

  toggleSelection(course: any, isChecked: boolean, filterSubjects: string[]) {
    if (isChecked) {
      filterSubjects.push(course);
    } else {
      const index = filterSubjects.indexOf(course);
      if (index !== -1) {
        filterSubjects.splice(index, 1);
      }
    }
  }

  filterMatches() {
    for (let i = 0; i < this.matches.length; i++) {
      const match = this.matches[i];

      const tutorSubjectsOfMatch = match.tutorSubjects.split(', ').map(subject => subject);
      const traineeSubjectsOfMatch = match.traineeSubjects.split(', ').map(subject => subject.trim());
      console.log(tutorSubjectsOfMatch)// stimmt für needs
      //console.log(traineeSubjectsOfMatch)
      console.log(this.filterSubjectsOffers)
      //console.log(this.filterSubjectsNeeds)
      this.containsAllOffers(tutorSubjectsOfMatch, this.filterSubjectsOffers)
      const containsAllNeeds = this.filterSubjectsNeeds.every(subject => {
        return traineeSubjectsOfMatch.includes(subject);
      });
      console.log("contains all offers for match " + this.matches[i].firstname + ": " + this.containsAllOffers(tutorSubjectsOfMatch, this.filterSubjectsOffers))
      //console.log("contains all needs: " + containsAllNeeds)
      if (this.containsAllOffers(tutorSubjectsOfMatch, this.filterSubjectsOffers)) {
        this.filteredMatches.push(match);
      }
    }
  }
   containsAllOffers(tutorSubjectsOfMatch, filterSubjectsOffers) {
    // Iterate over each subject in filterSubjectsOffers
    for (let i = 0; i < filterSubjectsOffers.length; i++) {
      let found = false;
      const subjectToFind = filterSubjectsOffers[i];

      // Check if subjectToFind exists in tutorSubjectsOfMatch
      for (let j = 0; j < tutorSubjectsOfMatch.length; j++) {
        if (tutorSubjectsOfMatch[j] === subjectToFind) {
          found = true;
          break;
        }
      }

      // If subjectToFind was not found in tutorSubjectsOfMatch, return false
      if (!found) {
        return false;
      }
    }

    // If all subjects in filterSubjectsOffers were found in tutorSubjectsOfMatch, return true
    return true;
  }

}
