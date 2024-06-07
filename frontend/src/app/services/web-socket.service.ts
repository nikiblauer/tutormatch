import { Injectable } from '@angular/core';
import {AuthService} from "./auth.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "./user.service";
import {Subject} from "rxjs";
import {ChatMessageDto} from "../dtos/chat";


declare var SockJS: any;
declare var Stomp: any;

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: any;
  connected: boolean = false;
  private messageSubject = new Subject<any>();

  constructor(private userService: UserService, private auth: AuthService) {
    console.log(SockJS);
    let socket = null;
    socket = new SockJS('http://localhost:8080/ws')
    this.stompClient = Stomp.over(socket);
    console.log("HELLO");
    this.connect(); // Connects the first time this class is iniated
  }

  connect() {
    const token = this.auth.getToken(); // Assume token is stored in localStorage

    this.stompClient.connect({ 'Authorization': token}, frame => {
      console.log('Connected: ' + frame);
      this.connected = true;


      this.userService.getUserId().subscribe({
        next: id => {
          this.stompClient.subscribe(`/user/${id}/queue/messages`, message=> {
            if (message.body){
              this.onMessageReceived(JSON.parse(message.body));
            }
          })
        }, error: err => {
          console.log(err);
        }
      })
    })
  }

  sendMessage(chatMessage: any){
    this.stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
  }

  onMessageReceived(parsedObject: any) {
    console.log(parsedObject);
    let receivedMessage;
    try {
      if (parsedObject && parsedObject.content) {
        receivedMessage = new ChatMessageDto();
        receivedMessage.chatRoomId = parsedObject.chatRoomId;
        receivedMessage.senderId = parsedObject.senderId;
        receivedMessage.recipientId = parsedObject.recipientId;
        receivedMessage.content = parsedObject.content;
        receivedMessage.timestamp = parsedObject.timestamp;
        return receivedMessage;
      } else {
        throw new Error("Error extracting content");
      }
    } catch (error) {
      console.error('Error parsing JSON or extracting content:', error);
      return null;
    }
  }
}
