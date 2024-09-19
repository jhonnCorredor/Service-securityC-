import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-checklist',
  standalone: true,
  imports: [HttpClientModule, FormsModule, CommonModule, NgbTypeaheadModule],
  templateUrl: './checklist.component.html',
  styleUrls: ['./checklist.component.css']
})
export class ChecklistComponent implements OnInit {
  checklists: any[] = [];
  checklist: any = { id: 0, code: '', calification_total: 0, state: false };
  isModalOpen = false;

  private apiUrl = 'http://localhost:9191/api/checklist';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.getChecklists();
  }

  getChecklists(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(
      (checklists) => {
        this.checklists = checklists;
        this.cdr.detectChanges();
      },
      (error) => {
        console.error('Error fetching checklists:', error);
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
    if (this.checklist.id === 0) {
      this.http.post(this.apiUrl, this.checklist).subscribe(() => {
        this.getChecklists();
        this.closeModal();
        Swal.fire('Success', 'Checklist created successfully!', 'success');
      });
    } else {
      this.http.put(this.apiUrl, this.checklist).subscribe(() => {
        this.getChecklists();
        this.closeModal();
        Swal.fire('Success', 'Checklist updated successfully!', 'success');
      });
    }
  }

  editChecklist(checklist: any): void {
    this.checklist = { ...checklist };
    this.openModal();
  }

  deleteChecklist(id: number): void {
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
          this.getChecklists();
          Swal.fire('Deleted!', 'Your checklist has been deleted.', 'success');
        });
      }
    });
  }

  resetForm(): void {
    this.checklist = { id: 0, code: '', calification_total: 0, state: false };
  }
}
