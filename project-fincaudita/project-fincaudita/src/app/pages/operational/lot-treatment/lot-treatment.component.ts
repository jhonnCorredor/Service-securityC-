import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-lot-treatment',
  standalone: true,
  imports: [HttpClientModule, FormsModule, CommonModule, NgbTypeaheadModule],
  templateUrl: './lot-treatment.component.html',
  styleUrls: ['./lot-treatment.component.css']
})
export class LotTreatmentComponent implements OnInit {
  treatments: any[] = [];
  treatment: any = { id: 0, lotId: 0, treatmentId: 0, state: true };
  lots: any[] = [];
  treatmentTypes: any[] = [];
  isModalOpen = false;

  private apiUrl = 'http://localhost:9191/api/lottreatment';
  private lotsUrl = 'http://localhost:9191/api/lot';
  private treatmentsUrl = 'http://localhost:9191/api/treatment';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  searchLots = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      map(term => term.length < 1 ? []
        : this.lots.filter(lot => lot.id.toString().includes(term)).slice(0, 10))
    );

  formatLot = (lot: any) => `Lote ${lot.id}`;

  onLotSelect(event: any): void {
    const selectedLot = event.item;
    this.treatment.lotId = selectedLot.id;
    this.cdr.detectChanges(); // Forzar la actualización del componente
  }

  searchTreatments = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      map(term => term.length < 1 ? []
        : this.treatmentTypes.filter(treatment => treatment.id.toString().includes(term)).slice(0, 10))
    );

  formatTreatment = (treatment: any) => `Tratamiento ${treatment.id}`;

  onTreatmentSelect(event: any): void {
    const selectedTreatment = event.item;
    this.treatment.treatmentId = selectedTreatment.id;
    this.cdr.detectChanges(); // Forzar la actualización del componente
  }

  ngOnInit(): void {
    this.getTreatments();
    this.getLots();
    this.getTreatmentTypes();
  }

  getTreatments(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(
      (treatments) => {
        this.treatments = treatments;
        this.cdr.detectChanges();
      },
      (error) => {
        console.error('Error fetching treatments:', error);
      }
    );
  }

  getLots(): void {
    this.http.get<any[]>(this.lotsUrl).subscribe(
      (lots) => {
        this.lots = lots;
      },
      (error) => {
        console.error('Error fetching lots:', error);
      }
    );
  }

  getTreatmentTypes(): void {
    this.http.get<any[]>(this.treatmentsUrl).subscribe(
      (treatmentTypes) => {
        this.treatmentTypes = treatmentTypes;
      },
      (error) => {
        console.error('Error fetching treatments:', error);
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
    if (!this.treatment.lotId || !this.treatment.treatmentId) {
      Swal.fire('Error', 'Debe seleccionar un lote y un tratamiento válidos.', 'error');
      return;
    }

    if (this.treatment.id === 0) {
      this.http.post(this.apiUrl, this.treatment).subscribe({
        next: () => {
          this.getTreatments();
          this.closeModal();
          Swal.fire('Success', 'Tratamiento creado exitosamente!', 'success');
        },
        error: (err) => {
          console.error('Error creating treatment:', err);
          Swal.fire('Error', 'Error al crear el tratamiento.', 'error');
        }
      });
    } else {
      this.http.put(this.apiUrl, this.treatment).subscribe({
        next: () => {
          this.getTreatments();
          this.closeModal();
          Swal.fire('Success', 'Tratamiento actualizado exitosamente!', 'success');
        },
        error: (err) => {
          console.error('Error updating treatment:', err);
          Swal.fire('Error', 'Error al actualizar el tratamiento.', 'error');
        }
      });
    }
  }

  editTreatment(treatment: any): void {
    this.treatment = { ...treatment };
    this.openModal();
  }

  deleteTreatment(id: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: '¡No podrás revertir esto!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, elimínalo!',
      cancelButtonText: 'No, cancela!',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.http.delete(`${this.apiUrl}/${id}`).subscribe(() => {
          this.getTreatments();
          Swal.fire('Eliminado!', 'El tratamiento ha sido eliminado.', 'success');
        }, (error) => {
          console.error('Error deleting treatment:', error);
          Swal.fire('Error', 'Error al eliminar el tratamiento.', 'error');
        });
      }
    });
  }

  resetForm(): void {
    this.treatment = { id: 0, lotId: 0, treatmentId: 0, state: true };
  }

  getLotName(lotId: number): string {
    const lot = this.lots.find(l => l.id === lotId);
    return lot ? `Lote ${lot.id}` : '';
  }

  getTreatmentName(treatmentId: number): string {
    const treatment = this.treatmentTypes.find(t => t.id === treatmentId);
    return treatment ? `Tratamiento ${treatment.id}` : '';
  }
}
