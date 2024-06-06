import { Component, OnInit } from '@angular/core';
import {WebSocketService} from "../../services/web-socket.service";
import {ChatMessageDto, ChatRoomDto, CreateChatRoomDto} from "../../dtos/chat";
import {ChatService} from "../../services/chat.service";

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {
  message: string = "Hello, its me";
  user1: number = 1;
  user2: number = 2;
  activeChatRoom: string = "fdbd2a60-4328-47f1-8463-fcdb609810eb";
  chatRooms: ChatRoomDto[];
  messages: ChatMessageDto[];

  constructor(private chatService: ChatService, private webSocketService: WebSocketService) {

  }

  ngOnInit() {
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
      }, error: error => {
        console.log(error);
      }
    })
  }

  loadHistory() {
    if (!this.activeChatRoom) {
      console.log("Active chat room is not set");
      return;
    }
    this.activeChatRoom
    this.chatService.getMessagesByChatRoomId(this.activeChatRoom).subscribe({
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
      chatRoomId: this.activeChatRoom,
      senderId: this.user1,
      recipientId: this.user2,
      content: this.message,
      timestamp: new Date()
    };

    this.webSocketService.sendMessage(chatMessage);
    this.message = '';
  }
}
