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
      next: () => {
      },
      error: () => {
        this.error = 'Credenciales incorrectas';
      }
    });
  }
}
