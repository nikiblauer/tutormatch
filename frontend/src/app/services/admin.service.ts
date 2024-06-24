import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Globals } from "../global/globals";
import { BannedUserDto, StudentDto, UserProfile } from '../dtos/user';
import { StudentSubjectInfoDto } from '../dtos/user';
import { Page } from '../dtos/page';
import { FeedbackDto } from "../dtos/feedback";
import { SubjectCreateDto, SubjectDetailDto } from "../dtos/subject";
import { SimpleStaticticsDto, ExtendedStatisticsDto, CoverageStatisticsDto } from '../dtos/statistics';

@Injectable({
  providedIn: 'root'
})

export class AdminService {

  private baseUri: string = this.globals.backendUri + '/admin';

  constructor(private http: HttpClient, private globals: Globals) { }

  searchUsers(fullname: string, matrNumber: number, status: string, verifiedFilter: string, page: number, size: number): Observable<Page<StudentDto>> {
    const url = `${this.baseUri}/users`;

    // Define the query parameters
    let params: any = {};
    if (fullname) {
      params.fullname = fullname;
    }
    if (matrNumber) {
      params.matrNumber = matrNumber.toString();
    }
    if (status) {
      params.status = status;
    }
    if (verifiedFilter) {
      params.verified = verifiedFilter === 'verified' ? true : verifiedFilter === 'notVerified' ? false : null;
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

  updateUserDetails(toUpdate: StudentDto): Observable<StudentDto> {
    const url = `${this.baseUri}/users/update`
    return this.http.put<StudentDto>(url, toUpdate, { responseType: 'json' });
  }

  banUser(id: number, reason: string): Observable<void> {
    const url = `${this.baseUri}/users/${id}/ban`;
    return this.http.post<void>(url, {
      reason: reason
    })
  }

  getBannedUser(id: number): Observable<BannedUserDto> {
    const url = `${this.baseUri}/users/${id}/ban`;
    return this.http.get<BannedUserDto>(url)
  }

  createSubject(subject: SubjectCreateDto) {
    return this.http.post<StudentSubjectInfoDto>(this.baseUri + `/subject`, subject, { responseType: 'json' });
  }
  updateSubject(subject: SubjectDetailDto) {
    return this.http.put<StudentSubjectInfoDto>(this.baseUri + `/subject`, subject, { responseType: 'json' });
  }
  deleteSubject(id: number) {
    return this.http.delete<StudentSubjectInfoDto>(this.baseUri + `/${id}`, { responseType: 'json' });
  }

  getStatistics(): Observable<SimpleStaticticsDto> {
    const url = `${this.baseUri}/statistics/simple`;
    return this.http.get<SimpleStaticticsDto>(url);
  }

  getExtendedStatistics(x: number): Observable<ExtendedStatisticsDto> {
    const url = `${this.baseUri}/statistics/extended?x=${x}`;
    return this.http.get<ExtendedStatisticsDto>(url);
  }

  getUserSubjects(id: number) {
    return this.http.get<UserProfile>(this.baseUri + `/users/subjects` + `/${id}`);
  }

  addSubjectToUser(id: number, traineeSubjects: number[], tutorSubjects: number[]): Observable<any> {
    return this.http.put(this.baseUri + `/users/subjects` + `/${id}`, {
      traineeSubjects: traineeSubjects,
      tutorSubjects: tutorSubjects
    })
  }
  getWrittenFeedback(id: number) {
    return this.http.get<FeedbackDto[]>(this.baseUri + `/feedback/out` + `/${id}`);
  }
  deleteFeedbackById(id: number) {
    return this.http.delete(this.baseUri + `/feedback/delete` + `/${id}`);
  }

  getCoverageStatistics(x: number): Observable<CoverageStatisticsDto> {
    const url = `${this.baseUri}/statistics/coverage?x=${x}`;
    return this.http.get<CoverageStatisticsDto>(url);
  }

  getPreviewSubject(courseNr: string, semester: string): Observable<SubjectCreateDto> {
    const url = `${this.baseUri}/subject/courses/${courseNr}/semesters/${semester}/preview`
    return this.http.get<SubjectCreateDto>(url);
  }
}
