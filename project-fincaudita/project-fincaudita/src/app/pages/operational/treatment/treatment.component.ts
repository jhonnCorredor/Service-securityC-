import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule, NgForm } from '@angular/forms';
import { NgbDatepickerModule, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-treatment',
  standalone: true,
  imports: [HttpClientModule, FormsModule, CommonModule, NgbDatepickerModule],
  templateUrl: './treatment.component.html',
  styleUrls: ['./treatment.component.css']
})
export class TreatmentComponent implements OnInit {
  treatments: any[] = [];
  treatment: any = { id: 0, dateTreatment: { year: 2024, month: 9, day: 19 }, typeTreatment: '', quantityMix: '', state: false };
  isModalOpen = false;

  private apiUrl = 'http://localhost:9191/api/Treatment';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.getTreatments();
  }

  getTreatments(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(
      (treatments) => {
        this.treatments = treatments.map(treatment => ({
          ...treatment,
          dateTreatment: new Date(treatment.dateTreatment)  // Convierte a Date
        }));
        this.cdr.detectChanges();
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
    const formattedDate = this.convertNgbDateToDateString(this.treatment.dateTreatment);

    if (this.treatment.id === 0) {
      this.http.post(this.apiUrl, { ...this.treatment, dateTreatment: formattedDate })
        .subscribe(() => {
          this.getTreatments();
          this.closeModal();
          Swal.fire('Success', 'Treatment created successfully!', 'success');
        }, error => {
          console.error('Error creating treatment:', error);
          Swal.fire('Error', 'Failed to create treatment.', 'error');
        });
    } else {
      this.http.put(this.apiUrl, { ...this.treatment, dateTreatment: formattedDate })
        .subscribe(() => {
          this.getTreatments();
          this.closeModal();
          Swal.fire('Success', 'Treatment updated successfully!', 'success');
        }, error => {
          console.error('Error updating treatment:', error);
          Swal.fire('Error', 'Failed to update treatment.', 'error');
        });
    }
  }

  editTreatment(treatment: any): void {
    this.treatment = { 
      ...treatment,
      dateTreatment: {
        year: new Date(treatment.dateTreatment).getFullYear(),
        month: new Date(treatment.dateTreatment).getMonth() + 1,
        day: new Date(treatment.dateTreatment).getDate()
      }
    };
    this.openModal();
  }

  deleteTreatment(id: number): void {
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
          this.getTreatments();
          Swal.fire('Deleted!', 'Your treatment has been deleted.', 'success');
        });
      }
    });
  }

  resetForm(): void {
    this.treatment = { id: 0, dateTreatment: { year: 2024, month: 9, day: 19 }, typeTreatment: '', quantityMix: '', state: false };
  }

  onDateSelect(date: NgbDate): void {
    this.treatment.dateTreatment = { year: date.year, month: date.month, day: date.day };
  }

  convertNgbDateToDateString(date: NgbDate): string {
    return `${date.year}-${date.month.toString().padStart(2, '0')}-${date.day.toString().padStart(2, '0')}T00:00:00`;
  }
}
