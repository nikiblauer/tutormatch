import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Globals } from "../global/globals";
import { Observable } from "rxjs";
import {
  PasswordResetDto,
  SendPasswordResetDto,
  StudentDto,
  CreateStudentDto,
  UserProfile,
} from "../dtos/user";

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
  requestPasswordReset(emailSendPasswordResetDto: SendPasswordResetDto): Observable<any> {
    console.log(emailSendPasswordResetDto)
    return this.httpClient.post(this.userUri + '/reset_password', emailSendPasswordResetDto);
  }
  changePasswordWithResetToken(token: String, passwordResetDto: PasswordResetDto): Observable<any> {
    return this.httpClient.post(this.userUri + '/reset_password/'+ token, passwordResetDto);
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

  getVisibility():Observable<boolean>{
    return this.httpClient.get<boolean>(this.userUri + '/visibility');
  }

  updateVisibility(visibility: boolean): Observable<void>{
    return this.httpClient.put<void>(this.userUri + '/visibility', visibility);
  }

}
