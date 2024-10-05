import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-landig-page',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './landig-page.component.html',
  styleUrls: ['./landig-page.component.css']
})
export class LandigPageComponent {

  // Función para desplazarse suavemente a una sección
  scrollToSection(sectionId: string) {
    const section = document.getElementById(sectionId);
    if (section) {
      section.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
    }
  }
  
}
