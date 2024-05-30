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
  activeChatRoom: string = "";
  chatRooms: ChatRoomDto[];

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

  loadHistory() {
    this.chatService.getMessagesByChatRoomId(this.activeChatRoom).subscribe({
      next: messages => {
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
