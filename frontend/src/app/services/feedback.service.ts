import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {FeedbackDto} from "../dtos/feedback";

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {

  private baseUri: string = this.globals.backendUri + '/feedback';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getChatExists(ratedUserId: number) {
    return this.httpClient.get<void>(this.baseUri + `/valid/${ratedUserId}`)
  }
  getPostedFeedback(ratedUserId: number) {
    return this.httpClient.get<FeedbackDto[]>(this.baseUri + `/get/${ratedUserId}`)
  }
  postFeedback(feedbackDto: FeedbackDto) {
    console.log(feedbackDto);
    return this.httpClient.post<FeedbackDto[]>(this.baseUri, feedbackDto);
  }
}
