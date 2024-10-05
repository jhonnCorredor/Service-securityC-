import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit, ElementRef, ChangeDetectorRef, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import SignaturePad from 'signature_pad';
import Swal from 'sweetalert2';
import { AfterViewInit } from '@angular/core';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';

@Component({
  selector: 'app-review-technical',
  standalone: true,
  imports: [HttpClientModule, FormsModule, CommonModule, NgbTypeaheadModule, MatInputModule,
    MatAutocompleteModule],
  templateUrl: './review-technical.component.html',
  styleUrls: ['./review-technical.component.css']
})
export class ReviewTechnicalComponent implements OnInit, AfterViewInit {

  review: any = {
    id: 0,
    date_review: '',
    code: '',
    observation: '',
    tecnicoId: 0,
    tecnico: '',
    state: true,
    lotId: 0,
    lot: '',
    evidences: [{ code: '', document: '' }],
    checklists: {
      id: 0,
      code: '',
      calification_total: 0,
      qualifications: [{ observation: '', qualification_criteria: 0, assessmentCriteriaId: 0 }]
    }
  };
  reviews: any[] = [];
  farms: any[] = [];
  crops: any[] = [];
  users: any[] = [];
  filteredUsers: any[] = [];
  filteredFarms: any[] = [];
  assessmentCriterias: any[] = [];
  califications: any[] = [];
  isModalOpen = false;
  isEditMode = false;


  @ViewChild('signaturePadTech') signaturePadTechRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('signaturePadProd') signaturePadProdRef!: ElementRef<HTMLCanvasElement>;


  signaturePadTech!: SignaturePad;
  signaturePadProd!: SignaturePad;

  private apiUrl = 'http://localhost:9191/api/ReviewTechnical';
  private usersUrl = 'http://localhost:9191/api/User';
  private farmsUrl = 'http://localhost:9191/api/Farm';
  private assessmentCriteriaUrl = 'http://localhost:9191/api/Assesmentcriteria';

  private drawing = false; // Track drawing state
  private context: CanvasRenderingContext2D | null = null;

  signatureTechDrawn = false;
  signatureProdDrawn = false;
  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.getReviews();
    this.getUsers();
    this.getFarms();
    this.getAssessmentCriterias();
  }

  ngAfterViewInit(): void {
    this.initializeSignaturePads();
  }

  initializeSignaturePads(): void {
    this.signaturePadTech = new SignaturePad(this.signaturePadTechRef.nativeElement);
    this.signaturePadProd = new SignaturePad(this.signaturePadProdRef.nativeElement);
  }

  searchusers(event: any): void {
    const term = event.target.value.toLowerCase();
    this.filteredUsers = this.users.filter(user =>
      user.username.toLowerCase().includes(term)
    );
  }

  searchFarms(event: any): void {
    const term = event.target.value.toLowerCase();
    this.filteredFarms = this.farms.filter(farm =>
      farm.name.toLowerCase().includes(term) ||
      farm.lots.some((lot: { cultivo: string; }) => lot.cultivo.toLowerCase().includes(term))
    );
  }

  getReviews(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(
      (data) => {
        this.reviews = data;
        console.log('Revisiones cargadas:', this.reviews);
      },
      (error) => {
        console.error('Error al cargar las revisiones:', error);
        this.cdr.detectChanges();
      }
    );
  }

  getUsers(): void {
    this.http.get<any[]>(this.usersUrl).subscribe(
      (users) => {
        this.users = users;
        console.log('Users loaded:', this.users);
      },
      (error) => {
        console.error('Error fetching users:', error);
      }
    );
  }

  getAssessmentCriterias(): void {
    this.http.get<any[]>(this.assessmentCriteriaUrl).subscribe(
      (criteria) => {
        // Filtrar solo los campos id y name
        this.assessmentCriterias = criteria.map(criterion => ({
          id: criterion.id,
          name: criterion.name
        }));
        console.log('assessmentCriterias loaded:', this.assessmentCriterias);
      },
      (error) => {
        console.error('Error fetching assessment criteria:', error);
      }
    );
}

  getFarms(): void {
    this.http.get<any[]>(this.farmsUrl).subscribe(
      (farms) => {
        this.farms = farms;
        console.log('Granjas cargadas:', this.farms);
        this.cdr.detectChanges();
      },
      (error) => {
        console.error('Error al cargar las granjas:', error);
      }
    );
  }

  onusersSelect(event: any): void {
    const selectedUser = this.users.find(user => user.username === event.option.value);
    if (selectedUser) {
      this.review.tecnicoId = selectedUser.id;
      this.review.tecnico = selectedUser.username;
      this.filteredUsers = [];
    }
  }

  onFarmSelect(event: any): void {
    const selectedFarm = this.farms.find(farm => farm.name === event.option.value);

    if (selectedFarm && selectedFarm.lots.length > 0) {
      const selectedLot = selectedFarm.lots[0];
      this.review.lotId = selectedLot.id;
      this.review.lot = `${selectedFarm.name}: ${selectedLot.cultivo}`;
      this.filteredFarms = [];
    }
  }

  getUserName(id: number): string {
    const user = this.users.find(u => u.id === id);
    return user ? user.username : 'Desconocido';
  }

  getFarmName(id: number): string {
    const farm = this.farms.find(f => f.id === id);
    return farm ? farm.name : 'Desconocido';
  }

  openModal(): void {
    this.isModalOpen = true;

  }

  closeModal(): void {
    this.isModalOpen = false;
    this.resetForm();
    this.filteredUsers = [];
  }

    onFileChange(event: any, type: 'technician' | 'producer' | 'evidence'): void {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          const base64String = e.target.result;
          if (type === 'technician') {
            // Guardar firma del técnico como base64
            this.review.signature_technician = base64String;
          } else if (type === 'producer') {
            // Guardar firma del productor como base64
            this.review.signature_producer = base64String;
          } else if (type === 'evidence') {
            // Guardar evidencia del cultivo como base64, nombre de archivo y code
            this.review.evidences[0] = {
              document: base64String,  // Base64 del archivo
              name: file.name,         // Nombre del archivo
              code: this.review.code   // Usar el código que el usuario ingresó en el formulario
            };
          }
        };
        reader.readAsDataURL(file); // Leer archivo como base64
      }
    }
   

// Esta función recalcula la calificación total cada vez que cambie cualquier calificación individual.
onQualificationChange(): void {
  this.review.checklists.calification_total = this.calculateTotalQualification(this.review.checklists.qualifications);
}

// Función que suma todas las calificaciones y devuelve la calificación total
calculateTotalQualification(qualifications: any[]): number {
  if (!qualifications || !Array.isArray(qualifications)) {
      return 0;
  }

  return qualifications.reduce((total, qualification) => {
      // Asegúrate de que 'qualification_criteria' es el campo correcto
      return total + (qualification.qualification_criteria || 0); 
  }, 0);
}



  startDrawing(type: 'technician' | 'producer', canvas: HTMLCanvasElement): void {
    this.drawing = true;
    this.context = canvas.getContext('2d');
    this.context?.beginPath();
  }

  stopDrawing(): void {
    this.drawing = false;
    this.context?.closePath();
  }

  draw(event: MouseEvent, canvas: HTMLCanvasElement): void {
    if (!this.drawing || !this.context) return;

    const rect = canvas.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;

    this.context.lineTo(x, y);
    this.context.stroke();
  }

  clearSignature(type: 'technician' | 'producer', canvas: HTMLCanvasElement): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'Perderás los datos de la firma.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, limpiar',
      cancelButtonText: 'Cancelar',
      allowOutsideClick: false,
      customClass: {
        popup: 'modal-popup'
      }
    }).then((result) => {
      if (result.isConfirmed) {

        if (type === 'technician') {
          this.review.signature_technician = '';
        } else {
          this.review.signature_producer = '';
        }
        const context = canvas.getContext('2d');
        context?.clearRect(0, 0, canvas.width, canvas.height);

        Swal.fire({
          title: '¡Limpiado!',
          text: 'La firma ha sido limpiada.',
          icon: 'success',
          confirmButtonText: 'Aceptar',
          allowOutsideClick: false,
          customClass: {
            popup: 'modal-popup'
          }
        });
      }
    });
  }

  saveSignature(type: 'technician' | 'producer', canvas: HTMLCanvasElement): void {
    const dataURL = canvas.toDataURL();
    if (type === 'technician') {
      this.review.signature_technician = dataURL;
    } else {
      this.review.signature_producer = dataURL;
    }
    console.log('Saved signature:', dataURL);
  }

  onSubmit(form: NgForm): void {
    if (!this.review.tecnicoId) {
      Swal.fire('Error', 'Debe seleccionar un usuario válido.', 'error');
      return;
    }

// Asegurar que el código del checklist esté asignado
if (!this.review.checklists.code || this.review.checklists.code === '') {
  Swal.fire('Error', 'Debe asignar un código válido al checklist.', 'error');
  return;
}

    this.review.checklists.calification_total = this.calculateTotalQualification(this.review.checklists.qualifications);

    console.log('Saving revision técnica :', this.review);
    if (this.review.id === 0) {
      this.http.post(this.apiUrl, this.review).subscribe({
        next: () => {
          this.getReviews();
          this.closeModal();
          Swal.fire('Éxito', 'Revision técnica  creado exitosamente!', 'success');
        },
        error: (error) => {
          console.error('Error al crear revision técnica :', error);
          Swal.fire('Error', 'Hubo un problema al crear el revision técnica .', 'error');
        }
      });
    } else {
      this.http.put(this.apiUrl, this.review).subscribe({
        next: () => {
          this.getReviews();
          this.closeModal();
          Swal.fire('Éxito', 'Revision técnica actualizado correctamente!', 'success');
        },
        error: (error) => {
          console.error('Error al actualizar revision técnica :', error);
          Swal.fire('Error', 'Hubo un problema al actualizar la revision técnica', 'error');
        }
      });
    }
  }

  editReview(review: any): void {
    this.isEditMode = true;
    this.review = { ...review };
    const selectedUser = this.users.find(user => user.id === this.review.tecnicoId);
    if (selectedUser) {
      this.review.tecnico = selectedUser.username;
    }
    this.openModal();
  }

  deleteReview(id: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: '¡No podrás revertir esto!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, ¡elimínalo!',
      cancelButtonText: 'No, cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.http.delete(`${this.apiUrl}/${id}`).subscribe(() => {
          this.getReviews();
          Swal.fire('¡Eliminado!', 'La revision tecnica ha sido eliminada.', 'success');
        });
      }
    });
  }

  resetForm(): void {
    this.review = {
      id: 0,
      date_review: '',
      code: '',
      tecnicoId: 0,
      tecnico: '',
      state: true,
      lotId: 0,
      lot: '',
      evidences: [{ code: '', document: '' }],
      checklists: {
        id: 0,
        code: '',
        calification_total: 0,
        qualifications: [{ observation: '', qualification_criteria: 0, assessmentCriteriaId: 0 }]
      }
    };
    this.filteredUsers = [];
    this.filteredFarms = [];

  }
}