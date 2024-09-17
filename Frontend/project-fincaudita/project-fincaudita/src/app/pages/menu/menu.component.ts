import { Component, ElementRef, OnInit, ViewChildren, QueryList, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SvgsComponent } from "../svgs/svgs.component";
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../guard/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, SvgsComponent, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  @ViewChildren('collapse') collapses!: QueryList<ElementRef>;
  
  menu: any[] = [];
  private activeAccordion: string | undefined;
  authService = inject(AuthService);

  constructor(private router: Router){}

  ngOnInit(): void {
    this.loadMenu();

    setInterval(() => {
      if (!this.authService.isAuthenticated()) {
        this.router.navigate(['/login']); // Redirige al login si no está autenticado
      }
    }, 1000); // Verifica el estado de autenticación cada segundo
  }

  loadMenu() {
    const storedMenu = localStorage.getItem("menu");
    if (storedMenu) {
      const parsedMenu = JSON.parse(storedMenu);
      this.menu = parsedMenu?.menu?.[0]?.listView || [];
    } else {
      this.menu = []; 
    }
  }

 logout(){
    Swal.fire({
      title: '¿Estás seguro?',
      text: '¡Seguro que quieres cerrar sesión!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí',
      cancelButtonText: 'No',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.authService.logout(); // Cambia el estado de autenticación a false
        localStorage.removeItem("menu");
        this.router.navigate(['/login']); 
      }
    });
  }


  toggleAccordion(module: string) {
    const collapseElement = this.collapses.find(collapse => collapse.nativeElement.id === module + 'Collapse');
    if (collapseElement) {
      const isCollapsed = collapseElement.nativeElement.classList.contains('show');
      
      if (this.activeAccordion && this.activeAccordion !== module) {
        const previousCollapse = this.collapses.find(collapse => collapse.nativeElement.id === this.activeAccordion + 'Collapse');
        if (previousCollapse) {
          previousCollapse.nativeElement.classList.remove('show');
        }
      }

      if (isCollapsed) {
        collapseElement.nativeElement.classList.remove('show');
        this.activeAccordion = undefined;
      } else {
        collapseElement.nativeElement.classList.add('show');
        this.activeAccordion = module;
      }
    }
  }

  isAccordionOpen(module: string): boolean {
    return this.activeAccordion === module;
  }

  
}
