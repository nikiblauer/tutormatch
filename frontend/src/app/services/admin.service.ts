import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Globals } from "../global/globals";
import { ApplicationUserDto } from '../dtos/user';
import { UserDetailWithSubjectsDto } from '../dtos/user';
import { Page } from '../dtos/page';

@Injectable({
  providedIn: 'root'
})

export class AdminService {

  private baseUri: string = this.globals.backendUri;

  constructor(private http: HttpClient, private globals: Globals) { }

  searchUsers(fullname: string, matrNumber: number, page: number, size: number): Observable<Page<ApplicationUserDto>> {
    const url = `${this.baseUri}/admin/users`;

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
    const url = `${this.baseUri}/admin/users/${id}`;
    return this.http.get<UserDetailWithSubjectsDto>(url);
  }
}
