import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { AuthService } from '../auth.service';
import { NgIf, NgFor } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [NgIf, NgFor, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit, OnDestroy {
  expanded = false;
  isMobile = false;
  menuOptions: Array<{ label: string; icon: string; route: string }> = [];
  userRole: 'ADMIN' | 'EMPLEADO' | null = null;
  userName: string | null = null;

  private roleSub!: Subscription;
  private nameSub!: Subscription;

  constructor(private authService: AuthService) {}

  ngOnInit() {
    // 🔹 Detectar si es móvil al inicializar
    this.checkIfMobile();
    // 🔹 En desktop, expandir por defecto
    if (!this.isMobile) {
      this.expanded = true;
    }
    
    // 🔹 Suscribirse a los cambios del rol (BehaviorSubject del AuthService)
    this.roleSub = this.authService.userRole$.subscribe((role) => {
      this.userRole = role as 'ADMIN' | 'EMPLEADO' | null;
      this.buildMenu();
    });
    // 🔹 Suscribirse al nombre de usuario
    this.nameSub = this.authService.userName$.subscribe((name) => {
      this.userName = name;
    });
  }

  buildMenu() {
    if (this.userRole === 'ADMIN') {
      this.menuOptions = [
        { label: 'Dashboard', icon: 'dashboard', route: '/home' },
        { label: 'Clientes', icon: 'group', route: '/clientes' },
        { label: 'Empleados', icon: 'person', route: '/empleado' },
        { label: 'Productos', icon: 'inventory', route: '/productos' },
        { label: 'Stock', icon: 'store', route: '/stock' },
        { label: 'Ventas', icon: 'shopping_cart', route: '/ventas' },
        { label: 'Pedidos', icon: 'assignment', route: '/pedidos' },
        { label: 'Materia Prima', icon: 'category', route: '/materia-prima' }
      ];
    } else if (this.userRole === 'EMPLEADO') {
      this.menuOptions = [
        { label: 'Dashboard', icon: 'dashboard', route: '/home' },
        { label: 'Pedidos', icon: 'assignment', route: '/pedidos' },
        { label: 'Stock', icon: 'store', route: '/stock' }
      ];
    } else {
      this.menuOptions = []; // Sin rol (no logueado)
    }
    console.log('[Sidebar] menuOptions:', this.menuOptions);
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.checkIfMobile();
    // Si cambiamos de móvil a desktop, expandir automáticamente
    if (!this.isMobile && !this.expanded) {
      this.expanded = true;
    }
    // Si cambiamos de desktop a móvil, colapsar automáticamente
    if (this.isMobile && this.expanded) {
      this.expanded = false;
    }
  }

  checkIfMobile() {
    this.isMobile = window.innerWidth <= 768;
  }

  toggleSidebar() {
    this.expanded = !this.expanded;
    console.log('[Sidebar] toggleSidebar, expanded:', this.expanded);
  }

  onMenuItemClick() {
    // En móviles, cerrar la sidebar cuando se hace clic en un elemento del menú
    if (this.isMobile) {
      this.expanded = false;
    }
  }

  logout() {
    console.log('[Sidebar] logout');
    this.authService.logout();
  }

  ngOnDestroy() {
    if (this.roleSub) this.roleSub.unsubscribe();
    if (this.nameSub) this.nameSub.unsubscribe();
  }
}

