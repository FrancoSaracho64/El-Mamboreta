import {Component, OnInit, OnDestroy} from '@angular/core';
import {Router} from '@angular/router';
import {Subscription} from "rxjs";
import {CommonModule} from '@angular/common';
import {AuthService} from "../auth.service";
import {RoleService, MenuOption} from "../services/role.service";
import {NotificationService} from "../services/notification.service";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  userRole: 'ADMIN' | 'EMPLEADO' | null = null;
  menuOptions: MenuOption[] = [];
  private roleSub!: Subscription;
  private menuSub!: Subscription;

  constructor(
    private router: Router, 
    private authService: AuthService,
    private roleService: RoleService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.roleSub = this.authService.userRole$.subscribe((role) => {
      this.userRole = role as 'ADMIN' | 'EMPLEADO' | null;
    });

    this.menuSub = this.roleService.getMenuOptionsForCurrentUser().subscribe((options) => {
      this.menuOptions = options.filter(option => option.route !== '/home'); // Excluir el dashboard del grid
    });
  }

  ngOnDestroy(): void {
    if (this.roleSub) this.roleSub.unsubscribe();
    if (this.menuSub) this.menuSub.unsubscribe();
  }

  navegarA(destino: string) {
    const fullRoute = `/${destino}`;
    
    if (this.roleService.canAccessRoute(fullRoute)) {
      this.router.navigate([fullRoute]);
    } else {
      this.notificationService.error(
        'Acceso Denegado',
        'No tienes permisos para acceder a esta sección'
      );
    }
  }

  getEmojiForRoute(route: string): string {
    switch (route) {
      case '/clientes': return '👥';
      case '/productos': return '📦';
      case '/pedidos': return '📋';
      case '/materia-prima': return '🔧';
      case '/ventas': return '💰';
      case '/stock': return '📈';
      default: return '📊';
    }
  }
}
