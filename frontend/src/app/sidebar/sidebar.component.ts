import {Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';
import {RoleService, MenuOption} from '../services/role.service';
import {NgFor, NgIf} from '@angular/common';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {Subscription} from 'rxjs';

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
  menuOptions: MenuOption[] = [];
  userRole: 'ADMIN' | 'EMPLEADO' | null = null;
  userName: string | null = null;

  private roleSub!: Subscription;
  private nameSub!: Subscription;
  private menuSub!: Subscription;

  constructor(
    private authService: AuthService,
    private roleService: RoleService
  ) {}

  ngOnInit() {
    this.checkIfMobile();
    if (!this.isMobile) {
      this.expanded = true;
    }

    // 🔹 Suscribirse a los cambios del rol (BehaviorSubject del AuthService)
    this.roleSub = this.authService.userRole$.subscribe((role) => {
      this.userRole = role as 'ADMIN' | 'EMPLEADO' | null;
    });
    
    // 🔹 Suscribirse al nombre de usuario
    this.nameSub = this.authService.userName$.subscribe((name) => {
      this.userName = name;
    });

    // 🔹 Suscribirse a las opciones de menú basadas en el rol
    this.menuSub = this.roleService.getMenuOptionsForCurrentUser().subscribe((options) => {
      this.menuOptions = options;
    });
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
  }

  onMenuItemClick() {
    // En móviles, cerrar la sidebar cuando se hace clic en un elemento del menú
    if (this.isMobile) {
      this.expanded = false;
    }
  }

  logout() {
    this.authService.logout();
  }

  ngOnDestroy() {
    if (this.roleSub) this.roleSub.unsubscribe();
    if (this.nameSub) this.nameSub.unsubscribe();
    if (this.menuSub) this.menuSub.unsubscribe();
  }
}

