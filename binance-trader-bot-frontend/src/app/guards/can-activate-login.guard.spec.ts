import { TestBed } from '@angular/core/testing';

import { CanActivateLoginGuard } from './can-activate-login.guard';

describe('CanActivateLoginGuard', () => {
  let guard: CanActivateLoginGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(CanActivateLoginGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
