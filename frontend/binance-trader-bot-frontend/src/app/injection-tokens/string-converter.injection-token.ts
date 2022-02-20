import { InjectionToken } from '@angular/core';
import {
  DefaultStringConverter,
  StringConverter,
} from '@diplomatiq/convertibles';

export const StringConverterInjectionToken =
  new InjectionToken<StringConverter>('String Converter', {
    providedIn: 'root',
    factory: () => new DefaultStringConverter(),
  });
