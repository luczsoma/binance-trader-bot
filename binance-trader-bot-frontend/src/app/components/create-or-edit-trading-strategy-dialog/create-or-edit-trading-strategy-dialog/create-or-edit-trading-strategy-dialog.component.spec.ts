import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateOrEditTradingStrategyDialogComponent } from './create-or-edit-trading-strategy-dialog.component';

describe('CreateOrEditTradingStrategyDialogComponent', () => {
  let component: CreateOrEditTradingStrategyDialogComponent;
  let fixture: ComponentFixture<CreateOrEditTradingStrategyDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateOrEditTradingStrategyDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateOrEditTradingStrategyDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
