import { Injectable } from '@angular/core';
import {AuthService} from "./auth.service";


declare var SockJS: any;
declare var Stomp: any;

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: any;
  connected: boolean = false;

  constructor(private auth: AuthService) {
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

      this.stompClient.subscribe('/user/2/queue/messages', message=> {
        if (message.body){
          this.onMessageReceived(JSON.parse(message.body));
        }
      })
    })
  }

  sendMessage(chatMessage: any){
    this.stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
  }

  onMessageReceived(message: any) {
    console.log('Received: ', message);
  }
}
