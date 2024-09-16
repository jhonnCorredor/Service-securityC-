import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';  
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap'; 
import Swal from 'sweetalert2';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [HttpClientModule, FormsModule, CommonModule, NgbTypeaheadModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent implements OnInit {
  users: any[] = [];
  user: any = { id: 0, username: '', password: '',  personId: 0, state: false };
  persons: any[] = [];  // Lista de módulos
  isModalOpen = false;

  private apiUrl = 'http://localhost:9191/api/User';
  private personsUrl = 'http://localhost:9191/api/Person'; 
  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  searchPersons= (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      map(term => term.length < 1 ? []
        : this.persons.filter(person => person.first_name.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10))
    );

  formatPerson = (person: any) => person.first_name;  // Formato de los resultados mostrados en el autocompletado

  onPersonSelect(event: any): void {
    const selectedPerson = event.item;
    this.user.personId = selectedPerson.id;  // Asigna el ID del módulo seleccionado
  }

  ngOnInit(): void {
    this.getUsers();
    this.getPersons();  // Cargar los módulos al iniciar
  }

  getUsers(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(
      (users) => {
        this.users = users;
        this.cdr.detectChanges();
      },
      (error) => {
        console.error('Error fetching users:', error);
      }
    );
  }

  getPersons(): void {
    this.http.get<any[]>(this.personsUrl).subscribe(
      (persons) => {
        this.persons = persons;
      },
      (error) => {
        console.error('Error fetching persons:', error);
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
    if (!this.user.personId) {
      Swal.fire('Error', 'Debe seleccionar un persona válido.', 'error');
      return;
    }

    if (this.user.id === 0) {
      this.http.post(this.apiUrl, this.user).subscribe(() => {
        this.getUsers();
        this.closeModal();
        Swal.fire('Success', 'users created successfully!', 'success');
      });
    } else {
      this.http.put(this.apiUrl, this.user).subscribe(() => {
        this.getUsers();
        this.closeModal();
        Swal.fire('Success', 'users updated successfully!', 'success');
      });
    }
  }

  editUsers(user: any): void {
    this.user = { ...user };
    this.openModal();
  }

  deleteUsers(id: number): void {
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
          this.getUsers();
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
    this.user = { id: 0, username: '', password: '',  personId: 0, state: false };
  }

  getPersonName(personId: number): string {
    const person = this.persons.find(per => per.id === personId);
    return person ? person.first_name : 'Unknown';
  }
}
