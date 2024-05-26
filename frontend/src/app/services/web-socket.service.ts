import { Injectable } from '@angular/core';


declare var SockJS: any;
declare var Stomp: any;

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: any;

  constructor() {
    console.log(SockJS);
    let socket = null;
    socket = new SockJS('http://localhost:8080/ws')
    this.stompClient = Stomp.over(socket);
  }

  connect() {
    this.stompClient.connect({}, frame => {
      console.log('Connected: ' + frame);

      this.stompClient.subscribe('/chatUser/queue/messages', message=> {
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
