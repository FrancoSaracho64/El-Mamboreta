import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message: string;
  duration?: number; // en milisegundos, 0 = no auto-close
  timestamp: Date;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notifications = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notifications.asObservable();

  private defaultDuration = 5000; // 5 segundos

  constructor() {}

  /**
   * Muestra una notificación de éxito
   */
  success(title: string, message: string, duration?: number): void {
    this.addNotification('success', title, message, duration);
  }

  /**
   * Muestra una notificación de error
   */
  error(title: string, message: string, duration?: number): void {
    this.addNotification('error', title, message, duration || 0); // Los errores no se cierran automáticamente
  }

  /**
   * Muestra una notificación de advertencia
   */
  warning(title: string, message: string, duration?: number): void {
    this.addNotification('warning', title, message, duration);
  }

  /**
   * Muestra una notificación informativa
   */
  info(title: string, message: string, duration?: number): void {
    this.addNotification('info', title, message, duration);
  }

  /**
   * Maneja errores del backend y muestra notificaciones apropiadas
   */
  handleBackendError(error: any): void {
    let title = 'Error del Sistema';
    let message = 'Ha ocurrido un error inesperado';

    if (error.error) {
      // Error estructurado del backend
      if (error.error.message) {
        message = error.error.message;
      }
      if (error.error.title) {
        title = error.error.title;
      }
    } else if (error.message) {
      message = error.message;
    }

    // Personalizar según el código de estado
    switch (error.status) {
      case 401:
        title = 'Acceso Denegado';
        message = 'No tienes permisos para realizar esta acción';
        break;
      case 403:
        title = 'Permisos Insuficientes';
        message = 'Tu rol de usuario no permite esta operación';
        break;
      case 404:
        title = 'Recurso No Encontrado';
        message = 'El elemento solicitado no existe';
        break;
      case 500:
        title = 'Error del Servidor';
        message = 'Error interno del servidor. Contacta al administrador';
        break;
    }

    this.error(title, message);
  }

  /**
   * Maneja respuestas exitosas del backend
   */
  handleBackendSuccess(response: any, defaultMessage?: string): void {
    let title = 'Operación Exitosa';
    let message = defaultMessage || 'La operación se completó correctamente';

    if (response && response.message) {
      message = response.message;
    }
    if (response && response.title) {
      title = response.title;
    }

    this.success(title, message);
  }

  /**
   * Elimina una notificación específica
   */
  removeNotification(id: string): void {
    const currentNotifications = this.notifications.value;
    const updatedNotifications = currentNotifications.filter(n => n.id !== id);
    this.notifications.next(updatedNotifications);
  }

  /**
   * Elimina todas las notificaciones
   */
  clearAll(): void {
    this.notifications.next([]);
  }

  private addNotification(type: Notification['type'], title: string, message: string, duration?: number): void {
    const notification: Notification = {
      id: this.generateId(),
      type,
      title,
      message,
      duration: duration !== undefined ? duration : this.defaultDuration,
      timestamp: new Date()
    };

    const currentNotifications = this.notifications.value;
    this.notifications.next([...currentNotifications, notification]);

    // Auto-remove si tiene duración
    if (notification.duration && notification.duration > 0) {
      setTimeout(() => {
        this.removeNotification(notification.id);
      }, notification.duration);
    }
  }

  private generateId(): string {
    return Math.random().toString(36).substring(2) + Date.now().toString(36);
  }
}
