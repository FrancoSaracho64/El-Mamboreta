import {Component} from '@angular/core';
import {AuthService} from '../auth.service';
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {HttpClientModule} from "@angular/common/http";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';

  constructor(private authService: AuthService) {
  }

  login() {
    this.authService.login(this.username, this.password).subscribe({
      next: (user: any) => {
        // Esperar a que el estado se actualice antes de redirigir
        setTimeout(() => {
          const role = this.authService.getRole();
          if (role === 'ADMIN') {
            // Redirige automáticamente desde el servicio
          } else if (role === 'EMPLEADO') {
            // Redirige automáticamente desde el servicio
          }
        }, 100);
      },
      error: () => {
        this.error = 'Credenciales incorrectas';
      }
    });
  }
}
