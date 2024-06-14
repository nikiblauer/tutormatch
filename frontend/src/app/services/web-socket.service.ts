import { Injectable } from '@angular/core';
import {UserService} from "./user.service";
import {Observable, Subject} from "rxjs";
import {ChatMessageDto, WebSocketErrorDto} from "../dtos/chat";


declare var SockJS: any;
declare var Stomp: any;

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: any;
  connected: boolean = false;
  private messageSubject = new Subject<any>();
  private webSocketError = new Subject<any>();

  constructor(private userService: UserService) {

  }

  connect() {
    if (this.connected){
      return;
    }
    let socket = null;
    socket = new SockJS('http://localhost:8080/ws')
    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = () => { };     //removes websocket logs

    const token = localStorage.getItem('authToken');
    if (!token){
      return;
    }

    this.stompClient.connect({ 'Authorization': token}, frame => {
      this.connected = true;
      this.userService.getUserId().subscribe({
        next: id => {
          this.stompClient.subscribe(`/user/${id}/queue/messages`, message=> {
            if (message.body){
              this.onMessageReceived(JSON.parse(message.body));
            }
          });

          this.stompClient.subscribe(`/user/${id}/queue/errors`, error=> {
            if (error.body){
              this.onErrorReceived(JSON.parse(error.body))
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
    let receivedMessage;
    try {
      if (parsedObject && parsedObject.content) {
        receivedMessage = new ChatMessageDto();
        receivedMessage.chatRoomId = parsedObject.chatRoomId;
        receivedMessage.senderId = parsedObject.senderId;
        receivedMessage.recipientId = parsedObject.recipientId;
        receivedMessage.content = parsedObject.content;
        receivedMessage.timestamp = parsedObject.timestamp;
        this.messageSubject.next(parsedObject);
        return receivedMessage;
      } else {
        throw new Error("Error extracting content");
      }
    } catch (error) {
      console.error('Error parsing JSON or extracting content:', error);
      return null;
    }
  }
  onErrorReceived(parsedObject: any) {
    let receivedError;
    try {
      if (parsedObject && parsedObject.errorMsg) {
        receivedError = new WebSocketErrorDto();
        receivedError.errorMsg = parsedObject.errorMsg;
        this.webSocketError.next(parsedObject);
        return receivedError;
      } else {
        throw new Error("Error extracting content");
      }
    } catch (error) {
      console.error('Error parsing JSON or extracting content:', error);
      return null;
    }
  }
  onNewMessage(): Observable<any> {
    return this.messageSubject.asObservable();
  }
  onNewError(): Observable<any>{
    return this.webSocketError.asObservable();
  }

  disconnect() {
    if (this.stompClient && this.connected) {
      this.stompClient.disconnect(() => {
        this.connected = false;
      });
    }
  }
}
