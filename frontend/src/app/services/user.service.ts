import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Message} from "../dtos/message";
import {Observable} from "rxjs";
import {ApplicationUserDto, CreateApplicationUserDto} from "../dtos/User";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private registerUri: string = this.globals.backendUri + '/user';

  constructor(private httpClient: HttpClient, private globals: Globals) {

  }

  createUser(toCreate: CreateApplicationUserDto): Observable<ApplicationUserDto> {
    console.log('Create user');
    return this.httpClient.post<ApplicationUserDto>(this.registerUri, toCreate, {responseType: 'json'});
  }

  getMatching(): Observable<string> {
    return this.httpClient.get<string>(this.registerUri + "1");
  }

  /*createUser(toCreate: CreateApplicationUserDto): Observable<string> {
    console.log('Create user');
    return this.httpClient.post<string>(this.globals.backendUri + "/authentication", toCreate);
  }

   */
}
