import { InjectionToken } from '@angular/core';

export const LocalStorageInjectionToken = new InjectionToken<Storage>(
  'Local Storage',
  {
    providedIn: 'root',
    factory: () => window.localStorage,
  }
);
