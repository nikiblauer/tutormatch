import {AfterViewInit, Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";
import {UserMatchDto} from "../../dtos/user-match";
import {StudentDto, Subject} from "../../dtos/user";
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";
import {RatingService} from "../../services/rating.service";
import {Router} from "@angular/router";
import {CreateChatRoomDto} from "../../dtos/chat";
import {ChatService} from "../../services/chat.service";
import {ReportService} from "../../services/report.service";
import {FeedbackService} from "../../services/feedback.service";


@Component({
  selector: 'app-match',
  templateUrl: './match.component.html',
  styleUrls: ['./match.component.scss']
})
export class MatchComponent implements OnInit, AfterViewInit {
  public placeholderMatches: UserMatchDto[] = [];
  public matches: UserMatchDto[] = [];
  public filteredMatches: UserMatchDto[] = [];
  public selectedMatch: UserMatchDto = new UserMatchDto();
  public selectedUser: StudentDto = new StudentDto();
  public selectedUserRating: number = -2;
  public filter: boolean;
  public filterNeeds: Subject[] = [];
  public filterOffers: Subject[] = [];
  public filterCourseNumNeeds: string[] = [];
  public filterCourseNumOffers: string[] = [];
  public matchNeeds: Subject[];
  public matchOffers: Subject[];
  public reportReason: string;
  public chatExists: boolean = false;
  public isLoaded: boolean;

  constructor(private userService: UserService, private notification: ToastrService,
              private spinner: NgxSpinnerService, private ratingService: RatingService,
              private router: Router, private chatService: ChatService,
              private reportService: ReportService,
              private feedbackService: FeedbackService) {
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
        this.isLoaded = true;
      },
      error: error => {
        clearTimeout(timeout);
        this.spinner.hide();
        console.error("Error when retrieving matches", error);
        this.notification.error(error.error, "Something went wrong!");
      }
    });
    this.getUserSubjects();
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
  ngAfterViewInit() {
    const modalElement = document.getElementById('matchDetailsModal');
    const feedbackModalElement = document.getElementById('feedbackModal');
    if (modalElement) {
      modalElement.addEventListener('hidden.bs.modal', () => {
        if (!feedbackModalElement.classList.contains('show')) {
          this.closeMatch();
        }
      });
    }
    if (feedbackModalElement) {
      feedbackModalElement.addEventListener('hidden.bs.modal', () => {
        this.closeMatch();
      });
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
          next: (value) =>{
            this.spinner.hide();
            this.selectedUserRating = value;
          },
          error: err => {
            clearTimeout(timeout);
            this.spinner.hide();
            console.error("Error when getting match rating", err);
            this.notification.error(err.error, "Something went wrong!");
          }
        });
      this.feedbackService.getChatExists(match.id).subscribe({
        next: (value) => {
          this.chatExists = value;
        },
        error: error => {
          this.notification.error(error.error, "Something went wrong!");
        }
      });
    }

    public closeMatch() {
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
        this.placeholderMatches = matches;
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
              next: () => {
                this.notification.success("Chat successfully created")
                this.router.navigate(["/chat"])
              }, error: error => {
                this.notification.error("Chat Creation Failed")
                console.error(error);
              }
            })
          } else {
            this.router.navigate(["/chat"])
          }

        }, error: err => {
          console.error(err)
        }
    });
  }

  public getSelectedUserAddressAsString(user: StudentDto) {
    return StudentDto.getAddressAsString(user);
  }

  public openFilter() {
    this.filter = true;
  }

  public getUserSubjects() {
    this.userService.getUserSubjects().subscribe({
      next: userProfile => {
        this.spinner.hide();
        this.matchNeeds = userProfile.subjects.filter(item => item.role == "tutor");
        this.matchOffers = userProfile.subjects.filter(item => item.role == "trainee");
        this.selectAll();
      },
      error: (errorMessage) => {
        console.log(errorMessage);
        this.notification.error(errorMessage, "Loading of matches failed.")
      }
    });

  }

  public applyFilter() {
    this.filterMatches();
    this.placeholderMatches = this.filteredMatches;
  }

  selectAll(){
    for(let i = 0; i < this.matchNeeds.length; i++){
      this.filterNeeds.push(this.matchNeeds[i]);
    }
    for(let i = 0; i < this.matchOffers.length; i++){
      this.filterOffers.push(this.matchOffers[i]);
    }
  }

  toggleSelection(course: Subject, isChecked: boolean, filterSubjects: Subject[]) {
    const index = filterSubjects.findIndex(item => item.id === course.id);

    if (isChecked && index === -1) {
      filterSubjects.push(course);
    } else if (!isChecked && index !== -1) {
      filterSubjects.splice(index, 1);
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
      let containsAtLeastOneOffer: boolean;
      for(let i = 0; i < tutorSubjectsOfMatch.length; i++){
        for (let j = 0; j < this.filterCourseNumOffers.length; j++){
          if(tutorSubjectsOfMatch[i].includes(this.filterCourseNumOffers[j])){
            containsAtLeastOneOffer = true;
          }
        }
      }
      let containsAtLeastOneNeed: boolean;
      for(let i = 0; i < traineeSubjectsOfMatch.length; i++){
        for (let j = 0; j < this.filterCourseNumNeeds.length; j++){
          if(traineeSubjectsOfMatch[i].includes(this.filterCourseNumNeeds[j])){
            containsAtLeastOneNeed = true;
          }
        }
      }
      if(containsAtLeastOneOffer || containsAtLeastOneNeed){
        this.filteredMatches.push(match)
      }
    }
  }

  extractCourseNumber(courseString: string): string | null {
    const regex = /\b\d{3}\.\d{3}\b/;
    const match = courseString.match(regex);
    return match ? match[0] : null;
  }
  getCourseNumberArray(filterArray: Subject[], courseNumberArray: String[]): void {
    courseNumberArray.length = 0;
    for (let subject of filterArray) {
      const courseNumber = this.extractCourseNumber(subject.name);
      if (courseNumber) {
        courseNumberArray.push(courseNumber);
      }
    }
  }

  public submitReport(){
    this.reportService.reportUser(this.selectedMatch.id, this.reportReason).subscribe({
        next: () => {
          this.notification.success("Successfully Reported.");
        },
        error: error => {
          console.error("Error reporting", error);
          this.notification.error(error.error, "Something went wrong!");
        }
      }
    );
  }
}
