import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {ApplicationUserDto, CreateApplicationUserDto} from "../dtos/user";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userUri: string = this.globals.backendUri + '/user';

  constructor(private httpClient: HttpClient, private globals: Globals) {

  }

  createUser(toCreate: CreateApplicationUserDto): Observable<ApplicationUserDto> {
    console.log('Create user');
    return this.httpClient.post<ApplicationUserDto>(this.userUri, toCreate, {responseType: 'json'});
  }

  verifyUser(token: string){
    return this.httpClient.get((this.userUri+"/verify/" + token));
  }
  getUserMatcher(id: number):Observable<any>{
    return this.httpClient.get(this.userUri + `/${id}` + "/matches");
  }
  getUser(id:number):Observable<any>{
    return this.httpClient.get(this.userUri+`/${id}`);
  }
}
