import { Component, ElementRef, OnInit, ViewChild} from '@angular/core';
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
import { Modal } from 'bootstrap';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})

export class ChatComponent implements OnInit {
  @ViewChild('chatHistory') private chatHistoryContainer: ElementRef;
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
  selectedUserRating: number;
  recipientToGetInfo: StudentDto;
  info: boolean;
  messageReceived: ChatMessageDto;
  messageSubscription: Subscription;
  errorSubscription: Subscription;
  reportReason: string = "";

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
    this.messageSubscription = this.webSocketService.onNewMessage().subscribe(receivedMessage => {
      this.messages.push(receivedMessage);
      this.scrollToBottom();
    });
    this.errorSubscription = this.webSocketService.onNewError().subscribe(error => {
      this.notification.error(error.errorMsg, "Error sending message: ")
    })
    this.webSocketService.connect();
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
      }, error: error => {
        console.log(error);
      }
    })
  }

  setActiveChatRoom(chatRoom: ChatRoomDto) {
    this.activeChatRoom = chatRoom;
    this.user1 = chatRoom.senderId;
    this.user1Name = chatRoom.senderFirstName + " " + chatRoom.senderLastName;
    this.user2 = chatRoom.recipientId;
    this.user2Name = chatRoom.recipientFirstName + " " + chatRoom.recipientLastName;
    this.loadHistory();
    this.scrollToBottom();
  }

  loadHistory() {
    if (!this.activeChatRoom.chatRoomId) {
      return;
    }
    this.chatService.getMessagesByChatRoomId(this.activeChatRoom.chatRoomId).subscribe({
      next: messages => {
        this.messages = messages;
      }, error: error => {
        console.log(error);
        this.notification.error(error.error, "Messages could not be loaded")
      }
    })
  }

  sendMessage() {
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
      setTimeout(() => {
        this.chatHistoryContainer.nativeElement.scrollTop = this.chatHistoryContainer.nativeElement.scrollHeight;
      }, 50);
    } catch (err) {
      console.error('Error scrolling to bottom:', err);
    }
  }

  getCharsLeft(){
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
    })
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
    })
  }

  public closeInfo() {
    this.selectedUserRating = -2;
  }

  public getSelectedUserAddressAsString(user: StudentDto) {
    return StudentDto.getAddressAsString(user);
  }

  submitReport() {
    let r = new ReportChatRoomDto();
    r.chatId = this.activeChatRoom.chatRoomId;
    r.reason = this.reportReason;
    console.log(r)
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
}
