import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {ChatRoomDto, ReportChatRoomDto} from "../dtos/chat";

@Injectable({
  providedIn: 'root'
})
export class ReportService {

  private baseUri: string = this.globals.backendUri + '/report';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }
  reportUser(id: number, reason: string):Observable<void>{
    return this.httpClient.post<void>(this.baseUri + `/${id}`, reason)
  }
  reportUserFeedback(feedback: number, reason: string):Observable<void>{
    return this.httpClient.post<void>(this.baseUri + `/feedback/${feedback}`, reason);
  }
  reportUserChat(a: ReportChatRoomDto):Observable<void>{
    return this.httpClient.post<void>(this.baseUri + `/chat`, `${a}`);
  }

}
