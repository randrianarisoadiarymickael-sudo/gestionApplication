import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardDevComponent } from './dashboard-dev.component';

describe('DashboardDevComponent', () => {
  let component: DashboardDevComponent;
  let fixture: ComponentFixture<DashboardDevComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashboardDevComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardDevComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
