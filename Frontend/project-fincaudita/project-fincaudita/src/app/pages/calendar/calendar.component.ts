import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [FormsModule, CommonModule],
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

  events: any[] = [
    // Puedes agregar eventos para años futuros aquí
  ];

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
  editingEvent: any = null; // Propiedad para mantener el evento en edición

  constructor() { }

  ngOnInit(): void {
    this.initDate();
    this.getNoOfDays();
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
    
    // Convert getDay() result to match the starting day of the week (Monday = 0, Sunday = 6)
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
  
  changeYear(direction: string) {
    if (direction === 'prev') {
      this.year--;
    } else {
      this.year++;
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
    this.editingEvent = null; // Resetea la propiedad de edición al cerrar el modal
  }

  addEvent() {
    if (this.event_title.trim()) {
      if (this.editingEvent) {
        // Si hay un evento en edición, actualiza el evento existente
        this.editingEvent.event_title = this.event_title;
        this.editingEvent.event_date = new Date(this.event_date);
        this.editingEvent.event_theme = this.event_theme;
        this.editingEvent = null; // Resetea la propiedad de edición
      } else {
        // Si no hay un evento en edición, agrega un nuevo evento
        this.events.push({
          event_date: new Date(this.event_date),
          event_title: this.event_title,
          event_theme: this.event_theme
        });
      }
      this.closeEventModal();
    }
  }

  deleteEvent(event: any) {
    this.events = this.events.filter(e => e !== event);
  }
  
  editEvent(event: any) {
    this.editingEvent = event; // Marca el evento que está siendo editado
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