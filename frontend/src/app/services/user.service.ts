import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Globals } from "../global/globals";
import { Observable } from "rxjs";
import { ApplicationUserDto, CreateApplicationUserDto, Subject, UserProfile, UserSubject } from "../dtos/user";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userUri: string = this.globals.backendUri + '/user';

  constructor(private httpClient: HttpClient, private globals: Globals) {

  }

  createUser(toCreate: CreateApplicationUserDto): Observable<ApplicationUserDto> {
    console.log('Create user');
    return this.httpClient.post<ApplicationUserDto>(this.userUri, toCreate, { responseType: 'json' });
  }

  verifyUser(token: string) {
    return this.httpClient.get((this.userUri + "/verify/" + token));
  }
  getUserMatcher(id: number): Observable<any> {
    return this.httpClient.get(this.userUri + `/${id}` + "/matches");
  }

  getUserSubjects(id: number): Observable<UserProfile> {
    return this.httpClient.get<UserProfile>(this.userUri + `/${id}/subjects`);
  }

  getUser(id: number): Observable<any> {
    return this.httpClient.get(this.userUri + `/${id}`);
  }

  updateUser(id: number, toUpdate: ApplicationUserDto) : Observable<ApplicationUserDto> {
    return this.httpClient.put<ApplicationUserDto>(this.userUri +  `/${id}`, toUpdate, { responseType: 'json' });
  }

  addSubjectToUser(id: number, traineeSubjects: number[], tutorSubjects: number[]): Observable<any> {
    return this.httpClient.put(this.userUri + `/${id}/subjects`, {
      traineeSubjects: traineeSubjects,
      tutorSubjects: tutorSubjects
    })
  }

}
