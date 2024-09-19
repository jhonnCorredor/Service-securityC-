import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-userprofile',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, FormsModule, CommonModule],
  templateUrl: './userprofile.component.html',
  styleUrls: ['./userprofile.component.css']
})
export class UserprofileComponent implements OnInit {
  // Imagen de perfil (vacío inicialmente)
  profileImageUrl: string | ArrayBuffer | null = null;

  username: string = '@username123';
  email: string = 'email@domain.com';

  // Al inicializar el componente, carga la imagen del perfil si está disponible
  ngOnInit() {
    this.profileImageUrl = localStorage.getItem('profileImageUrl');
  }

  // Método para abrir el selector de archivos
  triggerFileInput() {
    const fileInput = document.getElementById('fileInput') as HTMLElement;
    fileInput.click();
  }

  // Método para manejar la selección de la imagen
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.profileImageUrl = reader.result;
        localStorage.setItem('profileImageUrl', this.profileImageUrl as string);
      };
      reader.readAsDataURL(file);
    }
  }

  // Método para guardar los cambios
  saveChanges() {
    console.log('Usuario:', this.username);
    console.log('Correo:', this.email);
    console.log('Nueva imagen:', this.profileImageUrl);
    // Aquí podrías agregar lógica para guardar estos datos en el backend si fuera necesario
  }
}
