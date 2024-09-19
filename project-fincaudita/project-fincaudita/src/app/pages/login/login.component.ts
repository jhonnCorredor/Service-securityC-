import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import {Route, RouterModule } from '@angular/router';  // Import RouterModule
import Swal from 'sweetalert2';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../../guard/auth.service'; // Asegúrate de importar AuthService
import { CommonModule } from '@angular/common';
import { SvgsComponent } from '../svgs/svgs.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, HttpClientModule, CommonModule, RouterModule, SvgsComponent], // Add RouterModule
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class LoginComponent {

  username: string = '';
  password: string = '';

  private apiUrl = 'http://localhost:9191/login'; 

  constructor(private http: HttpClient, private router: Router, private authService: AuthService) {}
  
  togglePasswordVisibility() {
    const passwordInput = document.getElementById('password') as HTMLInputElement;
    const icon = document.getElementById('togglePassword');
    
    if (passwordInput.type === 'password') {
      passwordInput.type = 'text';
      icon?.classList.remove('fa-eye');
      icon?.classList.add('fa-eye-slash');
    } else {
      passwordInput.type = 'password';
      icon?.classList.remove('fa-eye-slash');
      icon?.classList.add('fa-eye');
    }
  }

  onSubmit(form: NgForm): void {
    const loginData = { username: this.username, password: this.password };
    
    this.http.post(this.apiUrl, loginData).subscribe(
      (response: any) => {
        if (response) {
          localStorage.setItem("menu", JSON.stringify(response));
          
          this.authService.login(); // Cambia el estado de autenticación a true

          Swal.fire({
            title: 'Bienvenido',
            text: '¡Inicio de sesión exitoso!',
            icon: 'success',
            confirmButtonText: 'Aceptar'
          }).then(() => {
            this.router.navigate(['/dashboard/home']); 
          });
        } else {
          Swal.fire({
            title: 'Error',
            text: 'Credenciales incorrectas.',
            icon: 'error',
            confirmButtonText: 'Intentar de nuevo'
          });
        }
      },
      (error) => {
        Swal.fire({
          title: 'Error',
          text: 'Error: ' + error.error,
          icon: 'error',
          confirmButtonText: 'Intentar de nuevo'
        });
      }
    );
  }
}
