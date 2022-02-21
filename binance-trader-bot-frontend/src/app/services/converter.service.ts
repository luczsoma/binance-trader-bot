import { Inject, Injectable } from '@angular/core';
import { BinaryConverter, StringConverter } from '@diplomatiq/convertibles';
import { BinaryConverterInjectionToken } from '../injection-tokens/binary-converter.injection-token';
import { StringConverterInjectionToken } from '../injection-tokens/string-converter.injection-token';

@Injectable({
  providedIn: 'root',
})
export class ConverterService {
  public constructor(
    @Inject(StringConverterInjectionToken)
    private readonly stringConverter: StringConverter,

    @Inject(BinaryConverterInjectionToken)
    private readonly binaryConverter: BinaryConverter
  ) {}

  public stringToBytes(string: string): Uint8Array {
    return this.stringConverter.encodeToBytes(string);
  }

  public bytesToBase64(bytes: Uint8Array): string {
    return this.binaryConverter.encodeToBase64(bytes);
  }
}
