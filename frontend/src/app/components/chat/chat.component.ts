import {AfterViewChecked, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {WebSocketService} from "../../services/web-socket.service";
import {ChatMessageDto, ChatRoomDto, CreateChatRoomDto} from "../../dtos/chat";
import {ChatService} from "../../services/chat.service";
import {UserService} from "../../services/user.service";
import {RatingService} from "../../services/rating.service";
import {NgxSpinnerService} from "ngx-spinner";
import {ToastrService} from "ngx-toastr";
import {StudentDto} from "../../dtos/user";

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})

export class ChatComponent implements OnInit {
  @ViewChild('chatHistory') private chatHistoryContainer: ElementRef;
  message: string;
  user1: number = 1;
  user2: number = 2;
  user1Name: string;
  user2Name: string;
  activeChatRoom: ChatRoomDto;
  chatRooms: ChatRoomDto[];
  messages: ChatMessageDto[];
  selectedUserRating: number;
  recipientToGetInfo: StudentDto;
  info: boolean;
  messageReceived: ChatMessageDto;

  constructor(private chatService: ChatService, private userService: UserService, private webSocketService: WebSocketService ,private ratingService: RatingService, private spinner: NgxSpinnerService, private notification: ToastrService) {

  }

  ngOnInit() {
    this.getChatRoomsForUser();
  }

  createChat() {
    const chatRoom: CreateChatRoomDto = {
      recipientId: this.user2
    }


    this.chatService.createChatRoom(chatRoom).subscribe({
      next: value => {
        console.log(value);
      }, error: error => {
        console.log(error);
      }
    })
  }

  getAllChatRooms() {
    this.chatService.getChatRooms().subscribe({
      next: chatRooms => {
        this.chatRooms = chatRooms;
        console.log(this.chatRooms);
      }, error: error => {
        console.log(error);
      }
    })
  }
  getChatRoomsForUser() {
    this.chatService.getChatRoomByUserId(1).subscribe({
      next: chatRooms => {
        this.chatRooms = chatRooms;
        console.log(this.chatRooms);
        if (this.chatRooms.length > 0) {
          this.setActiveChatRoom(this.chatRooms[0]);
        }
      }, error: error => {
        console.log(error);
      }
    })
  }
  setActiveChatRoom(chatroom : ChatRoomDto) {
    this.activeChatRoom = chatroom;
    this.user1 = chatroom.senderId;
    this.user1Name = chatroom.senderFirstName + " " + chatroom.senderLastName;
    this.user2 = chatroom.recipientId;
    this.user2Name = chatroom.recipientFirstName + " " + chatroom.recipientLastName;
    this.loadHistory();
    this.webSocketService.connect();
    console.log("test: " + this.user1Name)
  }

  loadHistory() {
    if (!this.activeChatRoom.chatRoomId) {
      console.log("Active chat room is not set");
      return;
    }
    this.chatService.getMessagesByChatRoomId(this.activeChatRoom.chatRoomId).subscribe({
      next: messages => {
        this.messages = messages;
        console.log(messages);
      }, error: error => {
        console.log(error);
      }
    })
  }

  sendMessage(){
    if (this.message.trim() == "") {
      return; // Do not send empty messages
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
    this.messages[this.messages.length] = this.messageReceived
    this.message = '';
    this.scrollToBottom();
  }
  receiveMessage(){
    const chatMessage: ChatMessageDto = {
      chatRoomId: this.activeChatRoom.chatRoomId,
      senderId: this.user1,
      recipientId: this.user2,
      content: this.message,
      timestamp: new Date()
    };
    this.webSocketService.onMessageReceived(chatMessage);
    this.messageReceived = this.webSocketService.onMessageReceived(chatMessage);
    this.messages[this.messages.length] = this.messageReceived
  }
  scrollToBottom(): void {
    try {
        this.chatHistoryContainer.nativeElement.scrollTop = this.chatHistoryContainer.nativeElement.scrollHeight;
    } catch (err) {
      console.error('Error scrolling to bottom:', err);
    }
  }
  recipientInfo(){
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
      next: (value) =>{
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
    this.selectedUserRating = -2;
  }
  public getSelectedUserAddressAsString(user: StudentDto) {
    return StudentDto.getAddressAsString(user);
  }
}
