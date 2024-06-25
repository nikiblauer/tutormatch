import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {WebSocketService} from "../../services/web-socket.service";
import {ChatMessageDto, ChatRoomDto} from "../../dtos/chat";
import {ChatService} from "../../services/chat.service";
import {UserService} from "../../services/user.service";
import {RatingService} from "../../services/rating.service";
import {NgxSpinnerService} from "ngx-spinner";
import {ToastrService} from "ngx-toastr";
import {StudentDto} from "../../dtos/user";
import {Subscription} from "rxjs";
import {ReportService} from "../../services/report.service";
import {ReportChatRoomDto} from "../../dtos/report";
import {HttpResponse} from '@angular/common/http';


@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit, AfterViewInit {
  message: string = "";
  user1: number = 1;
  user2: number = 2;
  user1Name: string;
  user2Name: string;
  activeChatRoom: ChatRoomDto;
  chatRooms: ChatRoomDto[];
  filteredChatRooms: ChatRoomDto[];
  searchString: string;
  messages: ChatMessageDto[];
  selectedUserRating: number = -2;
  recipientToGetInfo: StudentDto;
  info: boolean;
  messageReceived: ChatMessageDto;
  messageSubscription: Subscription;
  reportReason: string = "";
  errorSubscription: Subscription;
  // Arrays to store blocked users for the sender and recipient
  blockedUsers: number[] = [];
  senderBlockedUsers: number[] = [];
  recipientBlockedUsers: number[] = [];
  feedbackActive: boolean = false;
  isLoaded: boolean = false;
  @ViewChild('chatHistory') private chatHistoryContainer: ElementRef;

  constructor(private chatService: ChatService,
              private userService: UserService,
              private webSocketService: WebSocketService,
              private ratingService: RatingService,
              private spinner: NgxSpinnerService,
              private notification: ToastrService,
              private reportService: ReportService) {
  }

  ngOnInit() {
    this.getChatRoomsForUser();
    this.scrollToBottom();
    this.fetchBlockedUsers(this.user1, true);
    this.messageSubscription = this.webSocketService.onNewMessage().subscribe(receivedMessage => {
      this.messages.push(receivedMessage);
      this.scrollToBottom();
    });
    this.errorSubscription = this.webSocketService.onNewError().subscribe(error => {
      this.notification.error(error.errorMsg, "Error sending message: ")
    });
    this.webSocketService.connect();
  }

  ngAfterViewInit() {
    this.scrollToBottom();
    const modalElement = document.getElementById('infoModal');
    const feedbackModalElement = document.getElementById('feedbackModal2');
    if (modalElement) {
      modalElement.addEventListener('hidden.bs.modal', () => {
        if (!feedbackModalElement.classList.contains('show')) {
          this.closeInfo();
        }
      });
    }
    if (feedbackModalElement) {
      feedbackModalElement.addEventListener('hidden.bs.modal', () => {
        this.closeInfo();
      });
    }
  }

  onSearch() {
    const inputElement = event.target as HTMLInputElement;
    this.searchString = inputElement.value.toLowerCase();
    this.filteredChatRooms = this.chatRooms.filter(chatRoom =>
      `${chatRoom.recipientFirstName} ${chatRoom.recipientLastName}`.toLowerCase().includes(this.searchString.toLowerCase())
    );
  }

  getChatRoomsForUser() {
    this.chatService.getChatRoomOfUser().subscribe({
      next: chatRooms => {
        this.chatRooms = chatRooms.reverse();
        this.filteredChatRooms = this.chatRooms;
        if (this.chatRooms.length > 0) {
          this.setActiveChatRoom(this.filteredChatRooms[0]);
        }
        this.isLoaded = true;
      }, error: error => {
        console.error(error);
      }
    });
  }

  setActiveChatRoom(chatRoom: ChatRoomDto) {
    this.activeChatRoom = chatRoom;
    this.user1 = chatRoom.senderId;
    this.fetchBlockedUsers(chatRoom.senderId, true); // fetch blocked users for the sender
    this.fetchBlockedUsers(chatRoom.recipientId, false); // fetch blocked users for the recipient
    this.user1Name = chatRoom.senderFirstName + " " + chatRoom.senderLastName;
    this.user2 = chatRoom.recipientId;
    this.user2Name = chatRoom.recipientFirstName + " " + chatRoom.recipientLastName;
    this.loadHistory();
    // Ensure scrollToBottom is called after the view updates
    this.scrollToBottom();
  }

  loadHistory() {
    if (!this.activeChatRoom.chatRoomId) {
      return;
    }
    this.chatService.getMessagesByChatRoomId(this.activeChatRoom.chatRoomId).subscribe({
      next: messages => {
        this.messages = messages;
        // Ensure scrollToBottom is called after messages are loaded
        this.scrollToBottom();
      }, error: error => {
        console.error(error);
        this.notification.error(error.error, "Messages could not be loaded")
      }
    });
  }

  sendMessage() {
    if (this.blockedUsers.includes(this.user2)) {
      return; // prevents sending msg to blocked user
    }

    if (this.message.trim() == "") {
      return; // prevents sending empty msg
    }
    const chatMessage: ChatMessageDto = {
      chatRoomId: this.activeChatRoom.chatRoomId,
      senderId: this.user1,
      recipientId: this.user2,
      content: this.message,
      timestamp: new Date()
    };

    this.webSocketService.sendMessage(chatMessage);
    this.messageReceived = this.webSocketService.onMessageReceived(chatMessage);
    this.message = '';
  }

  scrollToBottom(): void {
    try {
      if (this.chatHistoryContainer) {
        setTimeout(() => {
          this.chatHistoryContainer.nativeElement.scrollTop = this.chatHistoryContainer.nativeElement.scrollHeight;
        }, 50);
      }
    } catch (err) {
      console.error('Error scrolling to bottom:', err);
    }
  }

  getCharsLeft() {
    return this.message.length;
  }

  recipientInfo() {
    this.info = true;
    let timeout = setTimeout(() => {
      this.spinner.show();
    }, 1500);
    this.userService.getUser(this.user2).subscribe({
      next: (user) => {
        this.recipientToGetInfo = user;
      },
      error: error => {
        clearTimeout(timeout);
        this.spinner.hide();
        console.error("Error when user match details", error);
        this.notification.error(error.error, "Something went wrong!");
      }
    });
    this.ratingService.getRatingFromUser(this.user2).subscribe({
      next: (value) => {
        this.selectedUserRating = value;
      },
      error: err => {
        clearTimeout(timeout);
        this.spinner.hide();
        console.error("Error when getting match rating", err);
        this.notification.error(err.error, "Something went wrong!");
      }
    });
  }

  public closeInfo() {
    this.selectedUserRating = -2;
  }

  public getSelectedUserAddressAsString(user: StudentDto) {
    return StudentDto.getAddressAsString(user);
  }

  // method to block or unblock users, if the user is already blocked, unblock the user, if not, block the user
  blockUser(userId: number) {
    if (this.senderBlockedUsers.includes(userId) || this.recipientBlockedUsers.includes(userId)) {
      // Unblock the user
      this.chatService.unblockUser(userId).subscribe((response: HttpResponse<any>) => {
        const indexSender = this.senderBlockedUsers.indexOf(userId);
        const indexRecipient = this.recipientBlockedUsers.indexOf(userId);
        if (indexSender > -1) { // check if the user is blocked by the sender
          this.senderBlockedUsers.splice(indexSender, 1); // remove the user from the blocked users list
        }
        if (indexRecipient > -1) { // check if the user is blocked by the recipient
          this.recipientBlockedUsers.splice(indexRecipient, 1); // remove the user from the blocked users list
        }
      }, error => {
        console.error(`Error unblocking user with ID ${userId}:`, error);
      });
    } else {
      // Block the user
      this.chatService.blockUser(userId).subscribe((response: HttpResponse<any>) => {
        this.senderBlockedUsers.push(userId);
        this.recipientBlockedUsers.push(userId);
      }, error => {
        console.error(`Error blocking user with ID ${userId}:`, error);
      });
    }
  }

  // fetches blocked users for the sender and recipient and saves it in the respective arrays for both users
  fetchBlockedUsers(userId: number, isSender: boolean) {
    this.chatService.getBlockedUsers(userId).subscribe(blockedUsers => {
      if (isSender) {
        this.senderBlockedUsers = blockedUsers;
      } else {
        this.recipientBlockedUsers = blockedUsers;
      }

    }, error => {
      console.error('Error fetching blocked users:', error);
    });
  }

  submitReport() {
    let r = new ReportChatRoomDto();
    r.chatId = this.activeChatRoom.chatRoomId;
    r.reason = this.reportReason;
    this.reportService.reportUserChat(r).subscribe({
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

  openFeedbackModal() {
    this.feedbackActive = true;
  }
}
