import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';
import * as XLSX from 'xlsx';
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import { NgSelectModule } from '@ng-select/ng-select';

@Component({
  selector: 'app-view',
  standalone: true,
  imports: [HttpClientModule, FormsModule, CommonModule, NgbTypeaheadModule, NgSelectModule],
  templateUrl: './view.component.html',
  styleUrls: ['./view.component.css']
})
export class ViewComponent implements OnInit {
  views: any[] = [];
  view: any = { id: 0, name: '', description: '', route: '', moduloId: 0, state: false };
  modulos: any[] = [];  // Lista de módulos
  isModalOpen = false;
  filteredViews: any[] = [];
  currentPage = 1;
  itemsPerPage = 5;
  searchTerm = '';
  itemsPerPageOptions = [5, 10, 20, 50];
  isDropdownOpen = false;

  private apiUrl = 'http://localhost:9191/api/View';
  private modulosUrl = 'http://localhost:9191/api/Modulo';  // Endpoint para obtener módulos

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  searchModulos = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      map(term => term.length < 1 ? []
        : this.modulos.filter(modulo => modulo.name.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10))
    );

  formatModulo = (modulo: any) => modulo.name;  // Formato de los resultados mostrados en el autocompletado

  onModuloSelect(event: any): void {
    const selectedModulo = event.item;
    this.view.moduloId = selectedModulo.id;  // Asigna el ID del módulo seleccionado
  }

  ngOnInit(): void {
    this.getViews();
    this.getModulos();  // Cargar los módulos al iniciar
  }

  getViews(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(
      (views) => {
        this.views = views;
        this.filterViews(); // Asegúrate de filtrar después de obtener las vistas
        this.cdr.detectChanges();
      },
      (error) => {
        console.error('Error fetching views:', error);
      }
    );
  }
  

  getModulos(): void {
    this.http.get<any[]>(this.modulosUrl).subscribe(
      (modulos) => {
        this.modulos = modulos;
      },
      (error) => {
        console.error('Error fetching modulos:', error);
      }
    );
  }
  filterViews(): void {
    console.log('Filtrando vistas con el término:', this.searchTerm);
    const search = this.searchTerm.toLowerCase().trim();
    this.filteredViews = this.views.filter(view =>
      view.name.toLowerCase().includes(search) ||
      view.description.toLowerCase().includes(search) ||
      view.route.toLowerCase().includes(search) ||
      (view.state ? 'activo' : 'inactivo').includes(search) ||
      this.getModuloName(view.moduloId).toLowerCase().includes(search)
    );
    this.currentPage = 1; // Reiniciar a la primera página
    console.log('Vistas filtradas:', this.filteredViews);
  }
  
  

  paginatedViews(): any[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredViews.slice(start, end);
  }

  exportToExcel(): void {
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.filteredViews);
    const workbook: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Vistas');
    XLSX.writeFile(workbook, 'Listado Vistas.xlsx');
  }

  exportToPDF(): void {
    const doc = new jsPDF();
    autoTable(doc, {
      head: [['ID', 'Nombre', 'Descripción', 'Ruta', 'Módulo', 'Estado']],
      body: this.filteredViews.map(view => [
        view.id,  // ID de la vista
        view.name,  // Nombre de la vista
        view.description,  // Descripción de la vista
        view.route,  // Ruta de la vista
        this.getModuloName(view.moduloId),  // Nombre del módulo asociado
        view.state ? 'Activo' : 'Inactivo'  // Estado de la vista
      ]),
    });
    doc.save('listado de vistas.pdf');
  }
  

  onSearchChange(): void {
    this.filterViews();
  }

  handleExport(event: any): void {
    const value = event;
  
    if (value === 'pdf') {
      this.exportToPDF();
    } else if (value === 'excel') {
      this.exportToExcel();
    }
  
    // Resetear el select después de la exportación
    this.searchTerm = ''; // Esto restablece el valor
  }
  updatePagination(): void {
    this.currentPage = 1;
    this.filterViews();
  }

  onItemsPerPageChange(): void {
    this.updatePagination();
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  get totalPages(): number {
    return Math.ceil(this.filteredViews.length / this.itemsPerPage);
  }

  getPageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  goToPage(page: number): void {
    this.currentPage = page;
  }
  hasSelected(): boolean {
    return this.views.some(view => view.selected);
  }
  deleteSelected(): void {
    const selectedIds = this.views.filter(view => view.selected).map(view => view.id);
  
    if (selectedIds.length > 0) {
      Swal.fire({
        title: '¿Estás seguro?',
        text: '¡No podrás revertir esto!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, eliminarlo',
        cancelButtonText: 'No, cancelar',
        reverseButtons: true
      }).then((result) => {
        if (result.isConfirmed) {
          const deleteRequests = selectedIds.map(id => this.http.delete(`${this.apiUrl}/${id}`).toPromise());
  
          Promise.all(deleteRequests)
            .then(() => {
              this.views = this.views.filter(view => !selectedIds.includes(view.id));
              this.filterViews(); // Llama al método de filtrado para actualizar la lista de vistas
              Swal.fire('¡Eliminados!', 'Las vistas seleccionadas han sido eliminadas.', 'success');
            })
            .catch((error) => {
              console.error('Error eliminando vistas seleccionadas:', error);
              Swal.fire('Error', 'Hubo un problema al eliminar las vistas seleccionadas.', 'error');
            });
        }
      });
    } else {
      Swal.fire('Error', 'No hay vistas seleccionadas para eliminar.', 'error');
    }
  }
  
  

  openModal(): void {
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.resetForm();
  }

  onSubmit(form: NgForm): void {
    if (!this.view.moduloId) {
      Swal.fire('Error', 'Debe seleccionar un módulo válido.', 'error');
      return;
    }
  
    if (this.view.id === 0) {
      this.http.post(this.apiUrl, this.view).subscribe(() => {
        this.getViews();
        this.closeModal();
        Swal.fire('Éxito', 'Vista creada exitosamente.', 'success');
      });
    } else {
      this.http.put(this.apiUrl, this.view).subscribe(() => {
        this.getViews();
        this.closeModal();
        Swal.fire('Éxito', 'Vista actualizada exitosamente.', 'success');
      });
    }
  }
  
  editView(view: any): void {
    this.view = { ...view };
    this.openModal();
  }

  deleteView(id: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: '¡No podrás revertir esto!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, elimínalo',
      cancelButtonText: 'No, cancelar',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.http.delete(`${this.apiUrl}/${id}`).subscribe(() => {
          this.getViews();
          Swal.fire(
            '¡Eliminado!',
            'La vista ha sido eliminada.',
            'success'
          );
        });
      }
    });
  }

  resetForm(): void {
    this.view = { id: 0, name: '', description: '', route: '', moduloId: 0, state: false };
  }

  getModuloName(moduloId: number): string {
    const modulo = this.modulos.find(mod => mod.id === moduloId);
    return modulo ? modulo.name : 'Desconocido';
  }
}
