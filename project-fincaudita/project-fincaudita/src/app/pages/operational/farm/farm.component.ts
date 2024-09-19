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
  farm: any = { id: 0, name: '', cityId: 0, userId: 0, addres: '', dimension: 0, state: false };
  cities: any[] = [];
  users: any[] = [];
  isModalOpen = false;

  private apiUrl = 'http://localhost:9191/api/Farm';
  private citiesUrl = 'http://localhost:9191/api/City';
  private usersUrl = 'http://localhost:9191/api/User';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  searchCities = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      map(term => term.length < 1 ? [] : this.cities
        .filter(city => city.name?.toLowerCase().includes(term.toLowerCase())).slice(0, 10))
    );

  searchUsers = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      map(term => term.length < 1 ? [] : this.users
        .filter(user => user.username?.toLowerCase().includes(term.toLowerCase())).slice(0, 10))
    );

  formatCity = (city: any) => city.name;
  formatUser = (user: any) => user.username;

  onCitySelect(event: any): void {
    const selectedCity = event.item;
    this.farm.cityId = selectedCity.id;
  }

  onUserSelect(event: any): void {
    const selectedUser = event.item;
    this.farm.userId = selectedUser.id;
  }

  ngOnInit(): void {
    this.getFarms();
    this.getCities();
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

  getCities(): void {
    this.http.get<any[]>(this.citiesUrl).subscribe(
      (cities) => {
        this.cities = cities;
      },
      (error) => {
        console.error('Error fetching cities:', error);
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
    if (!this.farm.cityId || !this.farm.userId) {
      Swal.fire('Error', 'Debe seleccionar una ciudad y un usuario válidos.', 'error');
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

  getCityName(id: number): string {
    const city = this.cities.find(c => c.id === id);
    return city ? city.name : '';
  }

  getUserName(id: number): string {
    const user = this.users.find(u => u.id === id);
    return user ? user.username : '';
  }

  resetForm(): void {
    this.farm = { id: 0, name: '', cityId: 0, userId: 0, addres: '', dimension: 0, state: false };
  }
}
