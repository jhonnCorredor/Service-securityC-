import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-farm',
  standalone: true,
  imports: [HttpClientModule, FormsModule, CommonModule, NgbTypeaheadModule],
  templateUrl: './farm.component.html',
  styleUrls: ['./farm.component.css']
})
export class FarmComponent implements OnInit {
  farms: any[] = [];
  farm: any = { id: 0, name: '', departamentId: 0, userId: 0, state: false };
  departments: any[] = [];
  users: any[] = [];
  isModalOpen = false;

  private apiUrl = 'https://localhost:44312/api/Farm';
  private departmentsUrl = 'https://localhost:44312/api/Departament';  
  private usersUrl = 'https://localhost:44312/api/User';  

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  searchDepartments = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      map(term => term.length < 1 ? []
        : this.departments.filter(departament => departament.name?.toLowerCase().includes(term.toLowerCase())).slice(0, 10))
    );

  searchUsers = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      map(term => term.length < 1 ? []
        : this.users.filter(user => user.username?.toLowerCase().includes(term.toLowerCase())).slice(0, 10))
    );

  formatDepartment = (departament: any) => departament.name;
  formatUser = (user: any) => user.username;

  onDepartmentSelect(event: any): void {
    const selectedDepartment = event.item;
    this.farm.departamentId = selectedDepartment.id;
  }

  onUserSelect(event: any): void {
    const selectedUser = event.item;
    this.farm.userId = selectedUser.id;
  }

  ngOnInit(): void {
    this.getFarms();
    this.getDepartments();
    this.getUsers();
  }

  getFarms(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(
      (farms) => {
        this.farms = farms;
        this.cdr.detectChanges();
      },
      (error) => {
        console.error('Error fetching farms:', error);
      }
    );
  }

  getDepartments(): void {
    this.http.get<any[]>(this.departmentsUrl).subscribe(
      (departments) => {
        this.departments = departments;
      },
      (error) => {
        console.error('Error fetching departments:', error);
      }
    );
  }

  getUsers(): void {
    this.http.get<any[]>(this.usersUrl).subscribe(
      (users) => {
        this.users = users;
      },
      (error) => {
        console.error('Error fetching users:', error);
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
    if (!this.farm.departamentId || !this.farm.userId) {
      Swal.fire('Error', 'Debe seleccionar un departamento y un usuario válidos.', 'error');
      return;
    }
  
    if (this.farm.id === 0) {
      this.http.post(this.apiUrl, this.farm).subscribe(() => {
        this.getFarms();
        this.closeModal();
        Swal.fire('Success', 'Farm created successfully!', 'success');
      });
    } else {
      this.http.put(this.apiUrl, this.farm).subscribe(() => {
        this.getFarms();
        this.closeModal();
        Swal.fire('Success', 'Farm updated successfully!', 'success');
      });
    }
  }

  editFarm(farm: any): void {
    this.farm = { ...farm };
    this.openModal();
  }

  deleteFarm(id: number): void {
    Swal.fire({
      title: 'Are you sure?',
      text: 'You won\'t be able to revert this!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'No, cancel!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.http.delete(`${this.apiUrl}/${id}`).subscribe(() => {
          this.getFarms();
          Swal.fire('Deleted!', 'Your file has been deleted.', 'success');
        });
      }
    });
  }

  getDepartmentName(id: number): string {
    const department = this.departments.find(d => d.id === id);
    return department ? department.name : '';
  }

  getUserName(id: number): string {
    const user = this.users.find(u => u.id === id);
    return user ? user.username : '';
  }

  resetForm(): void {
    this.farm = { id: 0, name: '', departamentId: 0, userId: 0, state: false };
  }
}
