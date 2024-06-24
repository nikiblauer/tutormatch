import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {FeedbackDto} from "../dtos/feedback";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {

  private baseUri: string = this.globals.backendUri + '/feedback';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getChatExists(ratedUserId: number): Observable<boolean> {
    return this.httpClient.get<boolean>(this.baseUri + `/valid/${ratedUserId}`)
  }
  getPostedFeedback(ratedUserId: number) {
    return this.httpClient.get<FeedbackDto[]>(this.baseUri + `/get/${ratedUserId}`)
  }
  getReceivedFeedbackSelf() {
    return this.httpClient.get<FeedbackDto[]>(this.baseUri + `/me`)
  }
  getWrittenFeedbackSelf() {
    return this.httpClient.get<FeedbackDto[]>(this.baseUri + `/me/sent`)
  }
  postFeedback(feedbackDto: FeedbackDto) {
    return this.httpClient.post<FeedbackDto[]>(this.baseUri, feedbackDto);
  }
  deleteFeedback(deleteUserId:number){
    return this.httpClient.delete(this.baseUri + `/delete/${deleteUserId}`)
  }
}
