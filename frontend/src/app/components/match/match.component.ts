import {Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";
import {UserMatchDto} from "../../dtos/user-match";
import {StudentDto} from "../../dtos/user";
import {ToastrService} from "ngx-toastr";
import {NgxSpinnerService} from "ngx-spinner";
import {RatingService} from "../../services/rating.service";
import {provideRouter, Router} from "@angular/router";
import {CreateChatRoomDto} from "../../dtos/chat";
import {ChatService} from "../../services/chat.service";


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
    chatRoomToCreate.recipientId = this.selectedMatch.id
    this.chatService.createChatRoom(chatRoomToCreate).subscribe({
      next: value => {
        console.log(value);
        this.router.navigate(["/chat"])
      }, error: error => {
        console.log(error);
      }
    })
  }

  public getSelectedUserAddressAsString(user: StudentDto) {
    return StudentDto.getAddressAsString(user);
  }
}
