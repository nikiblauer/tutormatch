import {Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";
import {UserMatchDto} from "../../dtos/user-match";
import {ApplicationUserDetailDto} from "../../dtos/user";
import {ToastrService} from "ngx-toastr";


@Component({
    selector: 'app-match',
    templateUrl: './match.component.html',
    styleUrls: ['./match.component.scss']
})
export class MatchComponent implements OnInit {
    public matches: UserMatchDto[] = [];
    public selectedMatch: UserMatchDto;
    public selectedUser: ApplicationUserDetailDto;

    constructor(private userService: UserService, private notification: ToastrService) {
    }

    ngOnInit() {
        this.userService.getUserMatcher().subscribe({
          next: (matches) => {
            this.matches = matches;
          },
          error: error => {
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

        this.userService.getUser(match.id).subscribe({
          next: (user) => {
            this.selectedUser = user;
          },
          error: error => {
            console.error("Error when user match details", error);
            this.notification.error(error.error, "Something went wrong!");
          }
        })
    }

    public closeMatch() {
        this.selectedMatch = null;
    }

    public startChat() {
        //TODO later Sprint
    }

    public getSelectedUserAddressAsString(user: ApplicationUserDetailDto) {
        return ApplicationUserDetailDto.getAddressAsString(user);
    }
}
