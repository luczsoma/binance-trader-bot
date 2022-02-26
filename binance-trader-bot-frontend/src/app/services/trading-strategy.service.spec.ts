import { TestBed } from '@angular/core/testing';

import { TradingStrategyService } from './trading-strategy.service';

describe('TradingStrategyService', () => {
  let service: TradingStrategyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TradingStrategyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
