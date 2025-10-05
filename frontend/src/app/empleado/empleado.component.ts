import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-empleado',
  standalone: true,
  imports: [],
  templateUrl: './empleado.component.html',
  styleUrls: ['./empleado.component.css']
})
export class EmpleadoComponent {
  constructor(private router: Router) {}

  irACargarStock() {
    this.router.navigate(['/stock']);
  }
}
