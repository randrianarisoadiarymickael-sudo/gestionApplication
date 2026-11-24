import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { TicketComponent } from './components/ticket/ticket.component';
import { TacheComponent } from './components/tache/tache.component';
import { HistoriqueComponent } from './components/historique/historique.component';
import { DashboardAdminComponent } from './components/admin/dashboard-admin/dashboard-admin.component';
import { DetailTicketComponent } from './components/admin/detail-ticket/detail-ticket.component';
import { HistoriqueTicketComponent } from './components/admin/historique-ticket/historique-ticket.component';
import { DashboardDevComponent } from './components/dev/dashboard-dev/dashboard-dev.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    TicketComponent,
    TacheComponent,
    HistoriqueComponent,
    DashboardAdminComponent,
    DetailTicketComponent,
    HistoriqueTicketComponent,
    DashboardDevComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }