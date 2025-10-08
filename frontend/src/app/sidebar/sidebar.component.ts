import { Component, OnInit, OnDestroy } from '@angular/core';
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
  menuOptions: Array<{ label: string; icon: string; route: string }> = [];
  userRole: 'ADMIN' | 'EMPLEADO' | null = null;

  private roleSub!: Subscription;

  constructor(private authService: AuthService) {}

  ngOnInit() {
    // 🔹 Suscribirse a los cambios del rol (BehaviorSubject del AuthService)
    this.roleSub = this.authService.userRole$.subscribe((role) => {
      this.userRole = role as 'ADMIN' | 'EMPLEADO' | null;
      console.log('[Sidebar] ngOnInit, userRole:', this.userRole);
      this.buildMenu();
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

  toggleSidebar() {
    this.expanded = !this.expanded;
    console.log('[Sidebar] toggleSidebar, expanded:', this.expanded);
  }

  logout() {
    console.log('[Sidebar] logout');
    this.authService.logout();
  }

  ngOnDestroy() {
    if (this.roleSub) this.roleSub.unsubscribe();
  }
}
