import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {CreateStudentDto, StudentDto} from "../dtos/user";
import {Observable} from "rxjs";
import {ChatMessageDto, ChatRoomDto, CreateChatRoomDto} from "../dtos/chat";

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private chatUri: string = this.globals.backendUri + '/chat';

  constructor(private httpClient: HttpClient, private globals: Globals) {

  }

  createChatRoom(toCreate: CreateChatRoomDto): Observable<ChatRoomDto> {

    console.log('Create chatroom');
    console.log("---------" + toCreate.recipientId);
    return this.httpClient.post<ChatRoomDto>(this.chatUri + "/room", toCreate, {responseType: "json"});
  }

  getChatRooms() {
    return this.httpClient.get<ChatRoomDto[]>(this.chatUri + "/room/user", {responseType: "json"});
  }

  getMessagesByChatRoomId(chatRoomId: string) {
    console.log("Getting all messages for chatroom: ", chatRoomId);
    return this.httpClient.get<ChatMessageDto[]>(this.chatUri + "/room/" + chatRoomId + "/messages", {responseType: "json"});
  }

  checkChatRoomExistsByRecipient(recipientId: number) {
    return this.httpClient.get<boolean>(this.chatUri + "/room/recipient/" + recipientId)
  }

  getChatRoomOfUser() {
    console.log("Getting all chatrooms of user: ");
    return this.httpClient.get<ChatRoomDto[]>(this.chatUri + "/room/user", {responseType: "json"});
  }
}
