import { Component, OnInit } from '@angular/core';
import { RoleViewService } from './roleview.service';

@Component({
  selector: 'app-roleview',
  templateUrl: './roleview.component.html',
  styleUrls: ['./roleview.component.css']
})
export class RoleviewComponent implements OnInit {
  roleViews: any[] = [];  // Usa `any` para evitar definir una interfaz o clase

  constructor(private roleViewService: RoleViewService) {}

  ngOnInit(): void {
    this.loadRoleViews();
  }

  loadRoleViews(): void {
    this.roleViewService.getAllRoleViews().subscribe(
      (data) => this.roleViews = data,
      (error) => console.error('Error fetching role views:', error)
    );
  }
}
