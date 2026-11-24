import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoriqueTicketComponent } from './historique-ticket.component';

describe('HistoriqueTicketComponent', () => {
  let component: HistoriqueTicketComponent;
  let fixture: ComponentFixture<HistoriqueTicketComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HistoriqueTicketComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoriqueTicketComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
