import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RoleViewService {
  private apiUrl = 'http://localhost:9191/api/RoleView';

  constructor(private http: HttpClient) {}

  getAllRoleViews(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
}
