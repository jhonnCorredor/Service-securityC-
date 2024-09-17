import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';
import Swal from 'sweetalert2';
import { Router, RouterModule } from '@angular/router'; // Router para redirección

@Component({
  selector: 'app-creat-account',
  standalone: true,
  imports: [HttpClientModule, FormsModule, CommonModule, NgbTypeaheadModule, RouterModule],
  templateUrl: './creat-account.component.html',
  styleUrls: ['./creat-account.component.css']
})
export class CreatAccountComponent implements OnInit {
  currentStep: number = 1;

  person: any = {
    first_name: '', 
    last_name: '', 
    type_document: '', 
    document: '', 
    phone: '', 
    email: '', 
    birth_date: '',
    cityId: 0
  };

  user: any = {
    username: '',
    password: '',
    roles: []
  };

  citys: any[] = [];
  showModal: boolean = true;  

  private personApiUrl = 'http://localhost:9191/api/Person';
  private userApiUrl = 'http://localhost:9191/api/User';
  private citysUrl = 'http://localhost:9191/api/City';
  
  // Variable para guardar el ID de la persona
  private personId: number | null = null;
  
  constructor(private http: HttpClient, private cdr: ChangeDetectorRef, private router: Router) {}

  searchCitys = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      map(term => term.length < 1 ? []
        : this.citys.filter(city => city.name.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10))
    );

  formatCity = (city: any) => city.name;

  onCitySelect(event: any): void {
    const selectedCity = event.item;
    this.person.cityId = selectedCity.id;  // Asigna el ID de la ciudad seleccionada
  }

  ngOnInit(): void {
    this.getCitys();  // Cargar las ciudades
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

  getCityName(cityId: number): string {
    const city = this.citys.find(c => c.id === cityId);
    return city ? city.name : 'Unknown';
  }

  nextStep(): void {
    if (this.currentStep < 3) {
      this.currentStep++;
    } else {
      this.onSubmit();
    }
  }
  
  prevStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }
  
  setStep(step: number): void {
    this.currentStep = step;
  }
  
  confirmExit(): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'Si sales, perderás todos los datos ingresados.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, salir',
      cancelButtonText: 'Cancelar',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.router.navigate(['/login']);  // Redirige al inicio de sesión si se confirma
      }
    });
  }

  onSubmit(): void {
    if (!this.person.cityId) {
      Swal.fire('Error', 'Debe seleccionar una ciudad válida.', 'error');
      return;
    }

    // Convertir la fecha de nacimiento a formato ISO
    this.person.birth_date = new Date(this.person.birth_date).toISOString();
    this.person.document = this.person.document.toString()

    this.http.post<any>(this.personApiUrl, this.person).subscribe({
      next: (response) => {
        // Suponiendo que la respuesta incluye el ID de la persona creada
        this.personId = response.id; // Guarda el ID en la variable

        console.log('Datos de la persona enviados con éxito, ID:', this.personId);
        this.submitUserData();  // Enviar datos de usuario si los de persona son exitosos
      },
      error: () => {
        Swal.fire('Error', 'Hubo un problema al enviar los datos de la persona', 'error');
      }
    });
  }

  private submitUserData(): void {
    if (this.personId === null) {
      Swal.fire('Error', 'No se pudo obtener el ID de la persona.', 'error');
      return;
    }

    const userData = {
      username: this.user.username,
      password: this.user.password,
      roles: [ { id: 1 } ],
      personId: this.personId
  
    };

    this.http.post(this.userApiUrl, userData).subscribe({
      next: () => {
        Swal.fire('Éxito', 'Cuenta creada con éxito', 'success');
        this.router.navigate(['/login']);  // Redirige al inicio de sesión después de crear la cuenta
      },
      error: () => {
        Swal.fire('Error', 'Hubo un problema al enviar los datos del usuario', 'error');
      }
    });
  }
}
