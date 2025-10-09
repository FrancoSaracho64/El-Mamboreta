import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { RoleService } from '../services/role.service';
import { NotificationService } from '../services/notification.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(
    private roleService: RoleService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    const url = state.url;
    
    if (this.roleService.canAccessRoute(url)) {
      return true;
    }

    // Mostrar notificación de acceso denegado
    this.notificationService.error(
      'Acceso Denegado',
      'No tienes permisos para acceder a esta página'
    );

    // Redirigir al home
    this.router.navigate(['/home']);
    return false;
  }
}
