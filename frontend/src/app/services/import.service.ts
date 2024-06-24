import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Globals } from "../global/globals";
import { ImportDto, ImportStatusDto } from '../dtos/import';

@Injectable({
  providedIn: 'root'
})

export class ImportService {

  private baseUri: string = this.globals.backendUri + '/import';

  constructor(private http: HttpClient, private globals: Globals) { }

  startImport(): Observable<ImportDto> {
    const url = `${this.baseUri}/start`;
    return this.http.get<ImportDto>(url);
  }

  cancelImport(importId: String): Observable<any> {
    const url = `${this.baseUri}/cancel`
    const importBody: ImportDto = { importId: importId }
    return this.http.post(url, importBody);
  }

  getStatus(importId: String): Observable<ImportStatusDto> {
    const url = `${this.baseUri}/${importId}/status`;
    return this.http.get<ImportStatusDto>(url);
  }

  getImport(): Observable<ImportStatusDto> {
    const url = `${this.baseUri}/status`;
    return this.http.get<ImportStatusDto>(url)
  }
}
