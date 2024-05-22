import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Globals } from "../global/globals";
import { ApplicationUserDto } from '../dtos/user';
import { UserDetailWithSubjectsDto } from '../dtos/user';
import { Page } from '../dtos/page';
import {SubjectCreateDto, SubjectDetailDto} from "../dtos/subject";

@Injectable({
  providedIn: 'root'
})

export class AdminService {

  private baseUri: string = this.globals.backendUri + '/admin';

  constructor(private http: HttpClient, private globals: Globals) { }

  searchUsers(fullname: string, matrNumber: number, page: number, size: number): Observable<Page<ApplicationUserDto>> {
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
    return this.http.get<Page<ApplicationUserDto>>(url, { params });
  }

  getUserDetails(id: number): Observable<UserDetailWithSubjectsDto> {
    const url = `${this.baseUri}/users/${id}`;
    return this.http.get<UserDetailWithSubjectsDto>(url);
  }

  createSubject(subject: SubjectCreateDto){
    return this.http.post<UserDetailWithSubjectsDto>(this.baseUri + `/subject`, subject, { responseType: 'json' });
  }
  updateSubject(subject: SubjectDetailDto){
    return this.http.put<UserDetailWithSubjectsDto>(this.baseUri + `/subject`, subject, { responseType: 'json' });
  }
  deleteSubject(id: number){
    return this.http.delete<UserDetailWithSubjectsDto>(this.baseUri + `/${id}`, { responseType: 'json' });
  }
}
