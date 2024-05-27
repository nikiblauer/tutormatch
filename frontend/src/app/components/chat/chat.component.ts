import { Component, OnInit } from '@angular/core';
import {WebSocketService} from "../../services/web-socket.service";
import {ChatMessageDetailDto} from "../../dtos/chat";

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {
  message: string = "Hello, its me";

  constructor(private webSocketService: WebSocketService) {
  }

  ngOnInit() {

  }

  sendMessage(){
    const chatMessage: ChatMessageDetailDto = {
      chatId: 1,
      senderId: 1,
      recipientId: 2,
      content: this.message,
      timestamp: new Date()
    };

    this.webSocketService.sendMessage(chatMessage);
    this.message = '';
  }
}
