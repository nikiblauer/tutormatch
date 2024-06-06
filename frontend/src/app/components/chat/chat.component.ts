import { Component, OnInit } from '@angular/core';
import {WebSocketService} from "../../services/web-socket.service";
import {ChatMessageDto, ChatRoomDto, CreateChatRoomDto} from "../../dtos/chat";
import {ChatService} from "../../services/chat.service";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {
  message: string = "Hello, its me";
  user1: number = 1;
  user2: number = 2;
  activeChatRoom: ChatRoomDto;
  chatRooms: ChatRoomDto[];
  messages: ChatMessageDto[];

  constructor(private chatService: ChatService, private userService: UserService, private webSocketService: WebSocketService) {

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
    this.user2 = chatroom.recipientId;
    this.loadHistory();
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
    const chatMessage: ChatMessageDto = {
      chatRoomId: this.activeChatRoom.chatRoomId,
      senderId: this.user1,
      recipientId: this.user2,
      content: this.message,
      timestamp: new Date()
    };

    this.webSocketService.sendMessage(chatMessage);
    this.message = '';
  }
}
