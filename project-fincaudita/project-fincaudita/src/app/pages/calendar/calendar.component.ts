import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {
  month: number = 0;
  year: number = 0;
  no_of_days: number[] = [];
  blankdays: number[] = [];
  days: string[] = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];
  monthNames: string[] = [
    'Enero ', 'Febrero ', 'Marzo ', 'Abril ', 'Mayo ', 'Junio ',
    'Julio ', 'Agosto ', 'Septiembre ', 'Octubre ', 'Noviembre ', 'Diciembre '
  ];

  events: any[] = []; // Datos que provienen del endpoint
  event_title: string = '';
  event_date: string = '';
  event_theme: string = 'azul';
  themes = [
    { value: 'azul', label: 'Azul' },
    { value: 'rojo', label: 'Rojo' },
    { value: 'amarillo', label: 'Amarillo' },
    { value: 'verde', label: 'Verde' },
    { value: 'morado', label: 'Morado' }
  ];

  openEventModal: boolean = false;
  editingEvent: any = null;

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.initDate();
    this.getNoOfDays();
    this.fetchEvents();
  }

  initDate() {
    const today = new Date();
    this.month = today.getMonth();
    this.year = today.getFullYear();
  }

  isToday(date: number): boolean {
    const today = new Date();
    const d = new Date(this.year, this.month, date);
    return today.toDateString() === d.toDateString();
  }

  getNoOfDays() {
    const daysInMonth = new Date(this.year, this.month + 1, 0).getDate();
    const firstDayOfMonth = new Date(this.year, this.month, 1).getDay();
    const dayOffset = (firstDayOfMonth === 0) ? 6 : firstDayOfMonth - 1;

    this.blankdays = Array(dayOffset).fill(0);
    this.no_of_days = Array.from({ length: daysInMonth }, (_, i) => i + 1);
  }

  changeMonth(direction: string) {
    if (direction === 'prev') {
      if (this.month === 0) {
        this.month = 11;
        this.year--;
      } else {
        this.month--;
      }
    } else {
      if (this.month === 11) {
        this.month = 0;
        this.year++;
      } else {
        this.month++;
      }
    }
    this.getNoOfDays();
  }

  showEventModal(date: number) {
    this.event_date = new Date(this.year, this.month, date).toDateString();
    this.openEventModal = true;
  }

  closeEventModal() {
    this.openEventModal = false;
    this.event_title = '';
    this.event_theme = 'azul';
    this.editingEvent = '';
  }

  // Obtener eventos desde el backend
  fetchEvents() {
    this.http.get<any[]>('http://localhost:9191/api/alert')
      .subscribe({
        next: (data) => {
          this.events = data.map(event => ({
            event_id: event.id,
            event_date: new Date(event.date),
            event_title: event.title,
            event_theme: event.theme.toLowerCase()
          }));
        },
        error: (err) => {
          Swal.fire('Error', 'No se pudieron cargar los eventos', 'error');
        }
      });
  }

  addEvent() {
    const newEvent = {
      id: this.editingEvent ? this.editingEvent.event_id : null, // Incluye el ID si se está editando un evento
      title: this.event_title,
      date: new Date(this.event_date),
      theme: this.event_theme.charAt(0).toUpperCase() + this.event_theme.slice(1)
    };
  
    if (this.editingEvent) {
      // Actualizar evento existente
      this.http.put('http://localhost:9191/api/alert', newEvent) // No incluyas el ID en la URL
        .subscribe({
          next: () => {
            Swal.fire('Actualizado', 'El evento ha sido actualizado', 'success');
            this.fetchEvents(); // Refrescar la lista de eventos
            this.closeEventModal();
          },
          error: () => Swal.fire('Error', 'No se pudo actualizar el evento', 'error')
        });
    } else {
      // Crear nuevo evento
      this.http.post('http://localhost:9191/api/alert', newEvent)
        .subscribe({
          next: () => {
            Swal.fire('Creado', 'El evento ha sido creado', 'success');
            this.fetchEvents(); // Refrescar la lista de eventos
            this.closeEventModal();
          },
          error: () => Swal.fire('Error', 'No se pudo crear el evento', 'error')
        });
    }
  }
  

  deleteEvent(event: any) {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'No podrás recuperar este evento',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminarlo'
    }).then((result) => {
      if (result.isConfirmed) {
        this.http.delete(`http://localhost:9191/api/alert/${event.event_id}`)
          .subscribe({
            next: () => {
              Swal.fire('Eliminado', 'El evento ha sido eliminado', 'success');
              this.fetchEvents(); // Refrescar la lista de eventos
            },
            error: () => Swal.fire('Error', 'No se pudo eliminar el evento', 'error')
          });
      }
    });
  }

  editEvent(event: any) {
    this.editingEvent = event;
    this.event_title = event.event_title;
    this.event_date = new Date(event.event_date).toDateString();
    this.event_theme = event.event_theme;
    this.openEventModal = true;
  }

  getEventsForDate(date: number): any[] {
    const targetDate = new Date(this.year, this.month, date).toDateString();
    return this.events.filter(event => new Date(event.event_date).toDateString() === targetDate);
  }
}
