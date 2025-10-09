import { Injectable } from '@angular/core';
import { RoleService } from './role.service';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class ValidationService {

  constructor(
    private roleService: RoleService,
    private notificationService: NotificationService
  ) {}

  /**
   * Valida si el usuario puede realizar una operación CRUD
   */
  canPerformOperation(operation: 'create' | 'update' | 'delete', entityName: string): boolean {
    if (!this.roleService.isAdmin()) {
      const operationText = {
        'create': 'crear',
        'update': 'editar',
        'delete': 'eliminar'
      };

      this.notificationService.error(
        'Acceso Denegado',
        `Solo los administradores pueden ${operationText[operation]} ${entityName}`
      );
      return false;
    }
    return true;
  }

  /**
   * Valida si el usuario puede acceder a una funcionalidad específica
   */
  canAccessFeature(featureName: string, requiredRole: 'ADMIN' | 'EMPLEADO' = 'ADMIN'): boolean {
    const userRole = this.roleService.getCurrentRole();
    
    if (!this.roleService.hasPermission(userRole as any, requiredRole)) {
      this.notificationService.error(
        'Acceso Denegado',
        `No tienes permisos para acceder a ${featureName}`
      );
      return false;
    }
    return true;
  }

  /**
   * Muestra notificación de operación exitosa
   */
  showSuccessMessage(operation: 'create' | 'update' | 'delete', entityName: string, itemName?: string): void {
    const operationText = {
      'create': 'creó',
      'update': 'actualizó',
      'delete': 'eliminó'
    };

    const title = {
      'create': 'Elemento Creado',
      'update': 'Elemento Actualizado',
      'delete': 'Elemento Eliminado'
    };

    const message = itemName 
      ? `${entityName} "${itemName}" se ${operationText[operation]} correctamente`
      : `${entityName} se ${operationText[operation]} correctamente`;

    this.notificationService.success(title[operation], message);
  }

  /**
   * Valida campos requeridos de un formulario
   */
  validateRequiredFields(data: any, requiredFields: string[]): boolean {
    const missingFields = requiredFields.filter(field => {
      const value = data[field];
      return value === null || value === undefined || value === '' || 
             (typeof value === 'string' && value.trim() === '');
    });

    if (missingFields.length > 0) {
      this.notificationService.warning(
        'Campos Requeridos',
        `Por favor completa los siguientes campos: ${missingFields.join(', ')}`
      );
      return false;
    }
    return true;
  }

  /**
   * Valida formato de email
   */
  validateEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      this.notificationService.warning(
        'Email Inválido',
        'Por favor ingresa un email válido'
      );
      return false;
    }
    return true;
  }

  /**
   * Valida que un número sea positivo
   */
  validatePositiveNumber(value: number, fieldName: string): boolean {
    if (value <= 0) {
      this.notificationService.warning(
        'Valor Inválido',
        `${fieldName} debe ser un número positivo`
      );
      return false;
    }
    return true;
  }
}
