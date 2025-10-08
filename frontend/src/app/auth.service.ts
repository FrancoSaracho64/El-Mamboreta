import {inject, Injectable, PLATFORM_ID} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {BehaviorSubject, Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {isPlatformBrowser} from '@angular/common';

@Injectable({providedIn: 'root'})
export class AuthService {
  private apiUrl = '/api/auth';
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);

  private loggedIn = new BehaviorSubject<boolean>(
    this.isBrowser ? !!localStorage.getItem('loggedIn') : false
  );

  private userRoleSubject = new BehaviorSubject<string | null>(
    this.isBrowser ? localStorage.getItem('userRole') : null
  );
  userRole$ = this.userRoleSubject.asObservable(); // 🔹 para suscribirse desde componentes

  private userNameSubject = new BehaviorSubject<string | null>(
    this.isBrowser ? localStorage.getItem('username') : null
  );
  userName$ = this.userNameSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
  }

  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, {username, password}).pipe(
      tap((user: any) => {
        this.loggedIn.next(true);
        if (this.isBrowser) {
          localStorage.setItem('loggedIn', 'true');
        }

        // 🔹 Guarda token
        if (user.token && this.isBrowser) {
          localStorage.setItem('token', user.token);
        }

        // 🔹 Guarda rol y emite
        const newRole = user.roles && user.roles.length > 0 ? user.roles[0] : null;
        if (newRole && this.isBrowser) {
          localStorage.setItem('userRole', newRole);
        }
        this.userRoleSubject.next(newRole);

        // 🔹 Guarda username
        if (user.username && this.isBrowser) {
          localStorage.setItem('username', user.username);
        }
        this.userNameSubject.next(user.username || null);

        // 🔹 Redirige según rol
        if (newRole === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else if (newRole === 'EMPLEADO') {
          this.router.navigate(['/empleado']);
        }
      })
    );
  }

  logout(): void {
    this.http.post(`${this.apiUrl}/logout`, {}).subscribe(() => {
      this.loggedIn.next(false);
      this.userRoleSubject.next(null);
      this.userNameSubject.next(null);

      if (this.isBrowser) {
        localStorage.removeItem('loggedIn');
        localStorage.removeItem('userRole');
        localStorage.removeItem('token');
        localStorage.removeItem('username');
      }

      this.router.navigate(['/login']);
    });
  }

  isLoggedIn(): boolean {
    return (
      this.loggedIn.value ||
      (this.isBrowser && localStorage.getItem('loggedIn') === 'true')
    );
  }

  getRole(): string | null {
    return this.userRoleSubject.value;
  }

  getUserName(): string | null {
    return this.userNameSubject.value;
  }
}

