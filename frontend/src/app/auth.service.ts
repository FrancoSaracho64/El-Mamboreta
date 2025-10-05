import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {BehaviorSubject, Observable} from 'rxjs';
import {tap} from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class AuthService {
  private apiUrl = '/api/auth';
  private isBrowser = typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  private loggedIn = new BehaviorSubject<boolean>(this.isBrowser ? !!localStorage.getItem('loggedIn') : false);
  private userRole: string | null = this.isBrowser ? localStorage.getItem('userRole') : null;

  constructor(private http: HttpClient, private router: Router) {
  }

  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, {username, password}).pipe(
      tap((user: any) => {
        this.loggedIn.next(true);
        // Persistir en localStorage solo si está en browser
        if (this.isBrowser) {
          localStorage.setItem('loggedIn', 'true');
        }
        this.userRole = user.roles && user.roles.length > 0 ? user.roles[0] : null;
        if (this.userRole && this.isBrowser) {
          localStorage.setItem('userRole', this.userRole);
        }
        if (this.userRole === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else if (this.userRole === 'EMPLEADO') {
          this.router.navigate(['/empleado']);
        }
      })
    );
  }

  logout(): void {
    this.http.post(`${this.apiUrl}/logout`, {}).subscribe(() => {
      this.loggedIn.next(false);
      this.userRole = null;
      if (this.isBrowser) {
        localStorage.removeItem('loggedIn');
        localStorage.removeItem('userRole');
      }
      this.router.navigate(['/login']);
    });
  }

  isLoggedIn(): boolean {
    return this.loggedIn.value || (this.isBrowser && localStorage.getItem('loggedIn') === 'true');
  }

  getCurrentUser(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/me`);
  }

  getRole(): string | null {
    return this.userRole || (this.isBrowser ? localStorage.getItem('userRole') : null);
  }
}
