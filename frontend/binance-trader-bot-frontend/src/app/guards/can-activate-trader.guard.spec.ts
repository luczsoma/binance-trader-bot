import { TestBed } from '@angular/core/testing';
import { CanActivateTraderGuard } from './can-activate-trader.guard';

describe('CaActivateTraderGuard', () => {
  let guard: CanActivateTraderGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(CanActivateTraderGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
