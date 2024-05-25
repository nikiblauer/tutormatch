import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Globals } from "../global/globals";
import { Observable } from "rxjs";
import { StudentDto, CreateStudentDto, Subject, UserProfile, UserSubject } from "../dtos/user";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userUri: string = this.globals.backendUri + '/user';

  constructor(private httpClient: HttpClient, private globals: Globals) {

  }

  createUser(toCreate: CreateStudentDto): Observable<StudentDto> {
    console.log('Create user');
    return this.httpClient.post<StudentDto>(this.userUri, toCreate, { responseType: 'json' });
  }
  resendVerification(email: string) {
    return this.httpClient.post(this.userUri + "/verify/resend", {email});
  }
  verifyUser(token: string) {
    return this.httpClient.get((this.userUri + "/verify/" + token));
  }
  getUserMatcher(): Observable<any> {
    return this.httpClient.get(this.userUri + "/matches");
  }

  getUserSubjects(): Observable<UserProfile> {
    return this.httpClient.get<UserProfile>(this.userUri + `/subjects`);
  }

  getUser(id: number): Observable<any> {
    return this.httpClient.get(this.userUri + `/${id}`);
  }

  updateUser(toUpdate: StudentDto) : Observable<StudentDto> {
    return this.httpClient.put<StudentDto>(this.userUri, toUpdate, { responseType: 'json' });
  }

  addSubjectToUser(traineeSubjects: number[], tutorSubjects: number[]): Observable<any> {
    return this.httpClient.put(this.userUri + `/subjects`, {
      traineeSubjects: traineeSubjects,
      tutorSubjects: tutorSubjects
    })
  }

}
