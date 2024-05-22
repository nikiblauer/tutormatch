import {Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";
import {UserMatchDto} from "../../dtos/user-match";
import {ApplicationUserDetailDto} from "../../dtos/user";


@Component({
    selector: 'app-match',
    templateUrl: './match.component.html',
    styleUrls: ['./match.component.scss']
})
export class MatchComponent implements OnInit {
    public matches: UserMatchDto[] = [];
    public selectedMatch: UserMatchDto;
    public selectedUser: ApplicationUserDetailDto;

    constructor(private userService: UserService) {
    }

    ngOnInit() {
        this.userService.getUserMatcher().subscribe(matches => {
            this.matches = matches;
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
        this.userService.getUser(match.id).subscribe(user => {
            this.selectedUser = user;
        });
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
