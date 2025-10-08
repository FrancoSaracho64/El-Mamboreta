import {Component} from '@angular/core';
import {AuthService} from './auth.service';
import {SidebarComponent} from './sidebar';
import {RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
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
