import { Injectable } from '@angular/core';
import { AuthService } from '../auth.service';
import { Observable, map } from 'rxjs';

export interface MenuOption {
  label: string;
  icon: string;
  route: string;
  description?: string;
  requiredRole?: 'ADMIN' | 'EMPLEADO';
}

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  private readonly menuOptions: MenuOption[] = [
    {
      label: 'Dashboard',
      icon: 'dashboard',
      route: '/home',
      description: 'Panel principal del sistema',
      requiredRole: 'EMPLEADO' // Tanto ADMIN como EMPLEADO pueden acceder
    },
    {
      label: 'Clientes',
      icon: 'group',
      route: '/clientes',
      description: 'Gestionar información de clientes',
      requiredRole: 'ADMIN'
    },
    {
      label: 'Productos',
      icon: 'inventory',
      route: '/productos',
      description: 'Administrar catálogo de productos',
      requiredRole: 'ADMIN'
    },
    {
      label: 'Stock',
      icon: 'store',
      route: '/stock',
      description: 'Gestionar y cargar stock de productos',
      requiredRole: 'EMPLEADO' // Tanto ADMIN como EMPLEADO pueden acceder
    },
    {
      label: 'Ventas',
      icon: 'shopping_cart',
      route: '/ventas',
      description: 'Administrar ventas realizadas',
      requiredRole: 'ADMIN'
    },
    {
      label: 'Pedidos',
      icon: 'assignment',
      route: '/pedidos',
      description: 'Gestionar pedidos de clientes',
      requiredRole: 'EMPLEADO' // Tanto ADMIN como EMPLEADO pueden acceder
    },
    {
      label: 'Materia Prima',
      icon: 'category',
      route: '/materia-prima',
      description: 'Controlar inventario de materiales',
      requiredRole: 'ADMIN'
    }
  ];

  constructor(private authService: AuthService) {}

  /**
   * Verifica si el usuario actual tiene permisos para acceder a una ruta específica
   */
  canAccessRoute(route: string): boolean {
    const userRole = this.authService.getRole();
    const menuOption = this.menuOptions.find(option => option.route === route);
    
    if (!menuOption || !userRole) {
      return false;
    }

    return this.hasPermission(userRole as 'ADMIN' | 'EMPLEADO', menuOption.requiredRole);
  }

  /**
   * Verifica si un rol tiene permisos para acceder a una funcionalidad
   */
  hasPermission(userRole: 'ADMIN' | 'EMPLEADO', requiredRole?: 'ADMIN' | 'EMPLEADO'): boolean {
    if (!requiredRole) return true;
    
    // ADMIN tiene acceso a todo
    if (userRole === 'ADMIN') return true;
    
    // EMPLEADO solo tiene acceso a funcionalidades que requieren EMPLEADO o menos
    if (userRole === 'EMPLEADO' && requiredRole === 'EMPLEADO') return true;
    
    return false;
  }

  /**
   * Obtiene las opciones de menú filtradas según el rol del usuario actual
   */
  getMenuOptionsForCurrentUser(): Observable<MenuOption[]> {
    return this.authService.userRole$.pipe(
      map(role => {
        if (!role) return [];
        
        return this.menuOptions.filter(option => 
          this.hasPermission(role as 'ADMIN' | 'EMPLEADO', option.requiredRole)
        );
      })
    );
  }

  /**
   * Obtiene las opciones de menú para un rol específico
   */
  getMenuOptionsForRole(role: 'ADMIN' | 'EMPLEADO'): MenuOption[] {
    return this.menuOptions.filter(option => 
      this.hasPermission(role, option.requiredRole)
    );
  }

  /**
   * Verifica si el usuario actual es ADMIN
   */
  isAdmin(): boolean {
    return this.authService.getRole() === 'ADMIN';
  }

  /**
   * Verifica si el usuario actual es EMPLEADO
   */
  isEmpleado(): boolean {
    return this.authService.getRole() === 'EMPLEADO';
  }

  /**
   * Obtiene el rol del usuario actual como Observable
   */
  getCurrentRole(): Observable<string | null> {
    return this.authService.userRole$;
  }
}
