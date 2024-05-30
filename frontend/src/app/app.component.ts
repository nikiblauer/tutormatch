import { Component } from '@angular/core';
import {WebSocketService} from "./services/web-socket.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'SE PR Group Phase';

  // To ensure websocket service and connecting is instantiated on loading web application
  constructor(webSocketService: WebSocketService) {
  }
}
