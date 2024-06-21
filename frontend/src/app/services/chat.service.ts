import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from "@angular/common/http";
import { Globals } from "../global/globals";
import { Observable } from "rxjs";
import { ChatMessageDto, ChatRoomDto, CreateChatRoomDto } from "../dtos/chat";

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private chatUri: string = this.globals.backendUri + '/chat';

  constructor(private httpClient: HttpClient, private globals: Globals) {

  }

  createChatRoom(toCreate: CreateChatRoomDto): Observable<ChatRoomDto> {
    return this.httpClient.post<ChatRoomDto>(this.chatUri + "/room", toCreate, { responseType: "json" });
  }

  getMessagesByChatRoomId(chatRoomId: string) {
    return this.httpClient.get<ChatMessageDto[]>(this.chatUri + "/room/" + chatRoomId + "/messages", { responseType: "json" });
  }

  checkChatRoomExistsByRecipient(recipientId: number) {
    return this.httpClient.get<boolean>(this.chatUri + "/room/recipient/" + recipientId)
  }

  getChatRoomOfUser(): Observable<ChatRoomDto[]>{
    return this.httpClient.get<ChatRoomDto[]>(this.chatUri + "/room/user", { responseType: "json" });
  }

  blockUser(userId: number): Observable<HttpResponse<any>> {
    return this.httpClient.post(this.chatUri + "/block/" + userId, null, { observe: 'response' });
  }

  getBlockedUsers(userId: number): Observable<number[]> {
    return this.httpClient.get<number[]>(`${this.chatUri}/block/${userId}`);
  }

  unblockUser(userId: number): Observable<HttpResponse<any>> {
    return this.httpClient.delete(this.chatUri + "/unblock/" + userId, { observe: 'response' });
  }
}
