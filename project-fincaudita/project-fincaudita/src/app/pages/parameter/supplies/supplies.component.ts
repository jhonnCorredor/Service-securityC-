import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-supplies',
  standalone: true,
  imports: [HttpClientModule, FormsModule, CommonModule, NgbTypeaheadModule],
  templateUrl: './supplies.component.html',
  styleUrl: './supplies.component.css'
})
export class SuppliesComponent {
  supplies: any[] = [];
  supplie: any = { id: 0, name: '', description: '', code: '', price: 0, state: false };
  isModalOpen = false;

  private apiUrl = 'http://localhost:9191/api/Supplies';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.getsupplies();
  }

  getsupplies(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(
      (supplies) => {
        this.supplies = supplies;
        this.cdr.detectChanges();
      },
      (error) => {
        console.error('Error fetching supplies:', error);
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
    if (this.supplie.id === 0) {
      this.http.post(this.apiUrl, this.supplie).subscribe(() => {
        this.getsupplies();
        this.closeModal();
        Swal.fire('Success', 'supplie created successfully!', 'success');
      });
    } else {
      this.http.put(this.apiUrl, this.supplie).subscribe(() => {
        this.getsupplies();
        this.closeModal();
        Swal.fire('Success', 'supplie updated successfully!', 'success');
      });
    }
  }

  editsupplie(supplie: any): void {
    this.supplie = { ...supplie };
    this.openModal();
  }

  deletesupplie(id: number): void {
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
          this.getsupplies();
          Swal.fire('Deleted!', 'Your file has been deleted.', 'success');
        });
      }
    });
  }

  resetForm(): void {
    this.supplie = { id: 0, name: '', description: '', code: '', price: 0, state: false };
  }
}
