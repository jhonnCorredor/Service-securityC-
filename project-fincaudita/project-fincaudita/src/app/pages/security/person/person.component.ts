import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit, HostListener } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';
import Swal from 'sweetalert2';
import * as XLSX from 'xlsx';
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import { NgSelectModule } from '@ng-select/ng-select';

@Component({
  selector: 'app-person',
  standalone: true,
  imports: [HttpClientModule, FormsModule, CommonModule, NgbTypeaheadModule, NgSelectModule],
  templateUrl: './person.component.html',
  styleUrls: ['./person.component.css']
})
export class PersonComponent implements OnInit {
  persons: any[] = [];
  person: any = {
    id: 0,
    first_name: '',
    last_name: '',
    email: '',
    type_document: '',
    document: '',
    addres: '',
    phone: 0,
    birth_of_date: new Date().toISOString().slice(0, 10),
    cityId: 0,
    state: false,
    selected: false
  };
  citys: any[] = [];
  isModalOpen = false;
  filteredPersons: any[] = [];
  currentPage = 1;
  itemsPerPage = 5;
  searchTerm = '';

  private apiUrl = 'http://localhost:9191/api/Person';
  private citysUrl = 'http://localhost:9191/api/City';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}
  
  isDropdownOpen = false;
  itemsPerPageOptions = [5, 10, 20, 50];
  
  

  exportToExcel(): void {
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.persons);
    const workbook: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Persons');
    XLSX.writeFile(workbook, 'listado de personas.xlsx');
  }

  exportToPDF(): void {
    const doc = new jsPDF();
    autoTable(doc, {
      head: [['ID', 'Primer Nombre', 'Segundo Nombre', 'Email', 'Tipo de Documento', 'Documento', 'Dirección', 'Teléfono']],
      body: this.filteredPersons.map(person => [
        person.id,
        person.first_name,
        person.last_name,
        person.email,
        person.type_document,
        person.document,
        person.addres,
        person.phone
      ]),
    });
    doc.save('listado de personas.pdf');
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
    this.currentPage = 1; // Resetear a la primera página cuando se cambia la cantidad de elementos por página
    this.filterPersons(); // Filtrar de nuevo para aplicar la nueva cantidad de elementos
  }


  paginatedPersons(): any[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredPersons.slice(start, end); // Devuelve la porción filtrada
  }

  // Typeahead for city search
  searchCitys = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      map(term => term.length < 1 ? []
        : this.citys.filter(city => city.name.toLowerCase().includes(term.toLowerCase())).slice(0, 10))
    );

  formatCity = (city: any) => city.name;

  onCitySelect(event: any): void {
    const selectedCity = event.item;
    this.person.cityId = selectedCity.id;
  }

  ngOnInit(): void {
    this.getPersons();
    this.getCitys();
  }

  getPersons(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(
      (persons) => { 
        this.persons = persons.map(person => ({
          ...person,
          birth_of_date: new Date(person.birth_of_date).toISOString().slice(0, 10),
          selected: false
        }));
        this.filterPersons();
        this.cdr.detectChanges();
      },
      (error) => {
        console.error('Error fetching persons:', error);
      }
    );
  }
  

  getCitys(): void {
    this.http.get<any[]>(this.citysUrl).subscribe(
      (citys) => {
        this.citys = citys;
      },
      (error) => {
        console.error('Error fetching cities:', error);
      }
    );
  }

  filterPersons(): void {
    const search = this.searchTerm.toLowerCase().trim(); // Asegúrate de que no haya espacios extra
    
    this.filteredPersons = this.persons.filter(person =>
      person.first_name.toLowerCase().includes(search) ||
      person.last_name.toLowerCase().includes(search) ||
      person.email.toLowerCase().includes(search) ||
      person.type_document.toLowerCase().includes(search) ||
      person.document.toString().includes(search) || // Filtrar por documento numérico convertido a string
      person.addres.toLowerCase().includes(search) ||
      person.phone.toString().includes(search) // Filtrar por teléfono numérico convertido a string
    );
    this.currentPage = 1;
  }
  

  openModal(): void {
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.resetForm();
  }

  resetForm(): void {
    this.person = {
      id: 0,
      first_name: '',
      last_name: '',
      email: '',
      type_document: '',
      document: '',
      addres: '',
      phone: 0,
      birth_of_date: new Date().toISOString().slice(0, 10),
      cityId: 0,
      state: false,
      selected: false
    };
  }

  onSubmit(form: NgForm): void {
    if (!this.person.cityId) {
      Swal.fire('Error', 'Debe seleccionar una ciudad válida.', 'error');
      return;
    }

    this.person.birth_of_date = new Date(this.person.birth_of_date).toISOString();

    if (this.person.id === 0) {
      this.http.post(this.apiUrl, this.person).subscribe(() => {
        this.getPersons();
        this.closeModal();
        Swal.fire('Éxito', 'Persona creada exitosamente.', 'success');
      });
    } else {
      this.http.put(this.apiUrl, this.person).subscribe(() => {
        this.getPersons();
        this.closeModal();
        Swal.fire('Éxito', 'Persona actualizada exitosamente.', 'success');
      });
    }
  }

  editPersons(person: any): void {
    this.person = { ...person, birth_of_date: new Date(person.birth_of_date).toISOString().slice(0, 10) };
    this.openModal();
  }

  deletePersons(id: number): void {
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
        this.http.delete(`${this.apiUrl}/${id}`).subscribe(() => {
          this.getPersons();
          Swal.fire('¡Eliminado!', 'La persona ha sido eliminada.', 'success');
        });
      }
    });
  }

  selectAll(event: any): void {
    const checked = event.target.checked;
    this.persons.forEach(person => (person.selected = checked));
  }

  areAllSelected(): boolean {
    return this.persons.length > 0 && this.persons.every(person => person.selected);
  }

  hasSelected(): boolean {
    return this.persons.some(person => person.selected);
  }

  deleteSelected(): void {
    const selectedIds = this.persons.filter(person => person.selected).map(person => person.id);

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
          this.http.delete(`${this.apiUrl}/deleteMany`, { body: selectedIds }).subscribe(() => {
            this.getPersons();
            Swal.fire('¡Eliminado!', 'Las personas seleccionadas han sido eliminadas.', 'success');
          });
        }
      });
    } else {
      Swal.fire('Error', 'No hay personas seleccionadas para eliminar.', 'error');
    }
  }

  // Pagination logic
  nextPage(): void {
    if ((this.currentPage * this.itemsPerPage) < this.filteredPersons.length) {
      this.currentPage++;
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }


  get totalPages(): number {
    return Math.ceil(this.filteredPersons.length / this.itemsPerPage);
  }

  getPageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  goToPage(page: number): void {
    this.currentPage = page;
  }

  updateRecords(): void {
    this.currentPage = 1; // Reinicia a la primera página al cambiar el número de registros
  }

  getCityName(cityId: number): string {
    const city = this.citys.find(city => city.id === cityId);
    return city ? city.name : '';
  }
}
