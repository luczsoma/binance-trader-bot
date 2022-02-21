import { Injectable } from '@angular/core';
import { scrypt } from 'scrypt-js';
import { ConverterService } from './converter.service';

@Injectable({
  providedIn: 'root',
})
export class CryptoService {
  public constructor(private readonly converterService: ConverterService) {}

  public async scrypt(password: string, salt: string): Promise<string> {
    const passwordBytes = this.converterService.stringToBytes(password);
    const saltBytes = this.converterService.stringToBytes(salt);
    const keyBytes = await scrypt(passwordBytes, saltBytes, 8192, 8, 1, 32);
    return this.converterService.bytesToBase64(keyBytes);
  }
}
