import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class RatingService {

  private baseUri: string = this.globals.backendUri + '/rating';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }
  getRatingFromUser(id: number):Observable<number>{
    return this.httpClient.get<number>(this.baseUri + `/${id}`)
  }
  rateUser(ratedUserid: number, rating: number): Observable<void>{
    return this.httpClient.put<void>(this.baseUri, {
      ratedUserid,
      rating
    });
  }

}
