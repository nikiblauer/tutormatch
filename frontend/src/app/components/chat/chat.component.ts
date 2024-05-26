import { Component, OnInit } from '@angular/core';
import {WebSocketService} from "../../services/web-socket.service";

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
    console.log(this.webSocketService);
    this.webSocketService.connect();
  }

  sendMessage(){
    const chatMessage = {
      senderId: 'user1',
      recipientId: 'user2',
      content: this.message,
      timestamp: new Date()
    };

    this.webSocketService.sendMessage(chatMessage);
    this.message = '';
  }
}
