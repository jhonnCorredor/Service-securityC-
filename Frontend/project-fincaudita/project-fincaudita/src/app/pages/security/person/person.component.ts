import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';  // Importa el módulo aquí
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-person',
  standalone: true,
  imports: [HttpClientModule, FormsModule, CommonModule, NgbTypeaheadModule],
  templateUrl: './person.component.html',
  styleUrl: './person.component.css'
})
export class PersonComponent implements OnInit {
  persons: any[] = [];
  person: any = { id: 0, first_name: '', last_name: '', email: '', type_document: '', document: '', addres: '', phone: 0, birth_of_date: new Date().toISOString().slice(0, 10), cityId: 0, state: false };
  citys: any[] = [];  // Lista de módulos
  isModalOpen = false;

  private apiUrl = 'http://localhost:9191/api/Person';
  private citysUrl = 'http://localhost:9191/api/City'; 

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  searchCitys = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      map(term => term.length < 1? []
        : this.citys.filter(city => city.name.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10))
    );

    formatCity = (city: any) => city.name;

    onCitySelect(event: any): void {
      const selectedCity = event.item;
      this.person.cityId = selectedCity.id;  // Asigna el ID del módulo seleccionado
    }


  ngOnInit(): void {
    this.getPersons();
    this.getCitys();  // Cargar los módulos al iniciar
  }

  getPersons(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(
      (persons) => {
        this.persons = persons;
        this.cdr.detectChanges();
      },
      (error) => {
        console.error('Error fetching views:', error);
      }
    );
  }

  getCitys(): void {
    this.http.get<any[]>(this.citysUrl).subscribe(
      (citys) => {
        this.citys = citys;
      },
      (error) => {
        console.error('Error fetching modulos:', error);
      }
    );
  }

  openModal(): void {
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.resetForm();
  }

  onSubmit(form: NgForm): void {
    if (!this.person.cityId) {
      Swal.fire('Error', 'Debe seleccionar un ciudad válido.', 'error');
      return;
    }

      // Convert birth_of_date to ISO format before sending it to the server
    this.person.birth_of_date = new Date(this.person.birth_of_date).toISOString();

    if (this.person.id === 0) {
      this.http.post(this.apiUrl, this.person).subscribe(() => {
        this.getPersons();
        this.closeModal();
        Swal.fire('Success', 'person created successfully!', 'success');
      });
    } else {
      this.http.put(this.apiUrl, this.person).subscribe(() => {
        this.getPersons();
        this.closeModal();
        Swal.fire('Success', 'person updated successfully!', 'success');
      });
    }
  }

  editPersons(person: any): void {
    this.person = { ...person,  birth_of_date: new Date(person.birth_of_date).toISOString().slice(0, 10) };
    this.openModal();
  }

  deletePersons(id: number): void {
    Swal.fire({
      title: 'Are you sure?',
      text: 'You won\'t be able to revert this!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'No, cancel!',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.http.delete(`${this.apiUrl}/${id}`).subscribe(() => {
          this.getPersons();
          Swal.fire(
            'Deleted!',
            'Your view has been deleted.',
            'success'
          );
        });
      }
    });
  }

  resetForm(): void {
    this.person = { id: 0, first_name: '', last_name: '', email: '', type_document: '', document: '', addres: '', phone: 0, birth_of_date: new Date().toISOString().slice(0, 10), cityId: 0, state: false };
  }
  getCityName(cityId: number): string {
    const city = this.citys.find(cit => cit.id === cityId);
    return city ? city.name : 'Unknown';
  }
}

