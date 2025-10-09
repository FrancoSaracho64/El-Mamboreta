import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { NotificationService } from '../services/notification.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private notificationService: NotificationService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // Solo mostrar notificaciones para errores que no sean de autenticación inicial
        // (para evitar mostrar errores durante el login)
        if (error.url && !error.url.includes('/auth/login')) {
          this.notificationService.handleBackendError(error);
        }
        
        return throwError(() => error);
      })
    );
  }
}
