import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';
import { Router, RouterModule } from '@angular/router'; // Importar Router para redirección

@Component({
  selector: 'app-forgot-your-password',
  standalone: true,
  imports: [HttpClientModule, FormsModule, CommonModule, NgbTypeaheadModule, RouterModule],
  templateUrl: './forgot-your-password.component.html',
  styleUrls: ['./forgot-your-password.component.css']
})
export class ForgotYourPasswordComponent implements OnInit {
  currentStep: number = 1;
  person: any = { 
    first_name: '', 
    last_name: '', 
    type_document: '', 
    document: '', 
    phone: '' 
  };

  showModal: boolean = true;

  private apiUrl = 'http://localhost:9191/api/Person';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    // Inicializa el componente
  }

  nextStep(): void {
    if (this.currentStep < 3) {
      this.currentStep++;
    } else {
      this.onSubmit(); // Enviar datos cuando se llega al último paso
    }
  }

  prevStep(): void {
    if (this.currentStep > 1) {
      Swal.fire({
        title: '¿Estás seguro?',
        text: '¿Quieres regresar al inicio de sesión?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, regresar',
        cancelButtonText: 'Cancelar',
        reverseButtons: true
      }).then((result) => {
        if (result.isConfirmed) {
          this.router.navigate(['/login']); // Redirige al inicio de sesión
        }
      });
    }
  }

  setStep(step: number): void {
    this.currentStep = step;
  }

  onSubmit(): void {
    this.http.post(this.apiUrl, this.person).subscribe({
      next: (response) => {
        Swal.fire('Success', 'Data submitted successfully', 'success');
      },
      error: (err) => {
        Swal.fire('Error', 'Something went wrong', 'error');
      }
    });
  }
}
