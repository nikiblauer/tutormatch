import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Globals } from "../global/globals";
import {StudentDto, UserProfile} from '../dtos/user';
import { StudentSubjectInfoDto } from '../dtos/user';
import { Page } from '../dtos/page';
import {SubjectCreateDto, SubjectDetailDto} from "../dtos/subject";

@Injectable({
  providedIn: 'root'
})

export class AdminService {

  private baseUri: string = this.globals.backendUri + '/admin';

  constructor(private http: HttpClient, private globals: Globals) { }

  searchUsers(fullname: string, matrNumber: number, page: number, size: number): Observable<Page<StudentDto>> {
    const url = `${this.baseUri}/users`;

    // Define the query parameters
    let params: any = {};
    if (fullname) {
      params.fullname = fullname;
    }
    if (matrNumber) {
      params.matrNumber = matrNumber.toString();
    }
    params.page = page.toString();
    params.size = size.toString();

    // Make the GET request and return the result
    return this.http.get<Page<StudentDto>>(url, { params });
  }

  getUserDetails(id: number): Observable<StudentSubjectInfoDto> {
    const url = `${this.baseUri}/users/${id}`;
    return this.http.get<StudentSubjectInfoDto>(url);
  }

  updateUserDetails(toUpdate: StudentDto) : Observable<StudentDto> {
    const url = `${this.baseUri}/users/edit`
    return this.http.put<StudentDto>(url, toUpdate, { responseType: 'json' });
  }

  createSubject(subject: SubjectCreateDto){
    return this.http.post<StudentSubjectInfoDto>(this.baseUri + `/subject`, subject, { responseType: 'json' });
  }
  updateSubject(subject: SubjectDetailDto){
    return this.http.put<StudentSubjectInfoDto>(this.baseUri + `/subject`, subject, { responseType: 'json' });
  }
  deleteSubject(id: number){
    return this.http.delete<StudentSubjectInfoDto>(this.baseUri + `/${id}`, { responseType: 'json' });
  }

  getUserSubjects(id: number) {
    return this.http.get<UserProfile>(this.baseUri + `/users/subjects` + `/${id}`);
  }

  addSubjectToUser(id: number, traineeSubjects: number[], tutorSubjects: number[]): Observable<any> {
    return this.http.put(this.baseUri + `/users/subjects`+ `/${id}`, {
      traineeSubjects: traineeSubjects,
      tutorSubjects: tutorSubjects
    })
  }
}
