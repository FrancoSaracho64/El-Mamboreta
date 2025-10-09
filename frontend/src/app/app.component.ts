import {Component} from '@angular/core';
import {AuthService} from './auth.service';
import {SidebarComponent} from './sidebar';
import {NotificationComponent} from './components/notification/notification.component';
import {RouterOutlet} from "@angular/router";
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, SidebarComponent, NotificationComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontend';

  constructor(private authService: AuthService) {
  }

  get userRole(): 'ADMIN' | 'EMPLEADO' {
    const role = this.authService.getRole();
    return role === 'ADMIN' ? 'ADMIN' : 'EMPLEADO';
  }
}
