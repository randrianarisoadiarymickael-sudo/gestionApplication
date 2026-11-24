import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { TicketComponent } from './components/ticket/ticket.component';
import { TacheComponent } from './components/tache/tache.component';
import { HistoriqueComponent } from './components/historique/historique.component';
import { DashboardAdminComponent } from './components/admin/dashboard-admin/dashboard-admin.component';
import { DetailTicketComponent } from './components/admin/detail-ticket/detail-ticket.component';
import { HistoriqueTicketComponent } from './components/admin/historique-ticket/historique-ticket.component';
import { DashboardDevComponent } from './components/dev/dashboard-dev/dashboard-dev.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'tickets', component: TicketComponent },
  { path: 'taches', component: TacheComponent },
  { path: 'historique/:id', component: HistoriqueComponent },
  { path: 'admin/dashboard', component: DashboardAdminComponent },
  { path: 'admin/ticket/:id', component: DetailTicketComponent },
  { path: 'admin/historique/:id', component: HistoriqueTicketComponent },
  { path: 'dev/dashboard', component: DashboardDevComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }