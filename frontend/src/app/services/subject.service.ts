import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import { Globals } from "../global/globals";
import { Observable } from 'rxjs';
import { Subject } from '../dtos/user';
import { Page } from '../dtos/page'


@Injectable({
  providedIn: 'root'
})
export class SubjectService {

  private subjectUri: string = this.globals.backendUri + '/subject';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getSubjects(q: string, page: number, size: number ): Observable<Page<Subject>> {
    let params = new HttpParams()
    .set('q', q)
    .set('page', page)
    .set('size', size);
    return this.httpClient.get<Page<Subject>>(this.subjectUri, { params })
  }
}
