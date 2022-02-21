import { InjectionToken } from '@angular/core';
import {
  BinaryConverter,
  DefaultBinaryConverter,
} from '@diplomatiq/convertibles';

export const BinaryConverterInjectionToken =
  new InjectionToken<BinaryConverter>('Binary Converter', {
    providedIn: 'root',
    factory: () => new DefaultBinaryConverter(),
  });
