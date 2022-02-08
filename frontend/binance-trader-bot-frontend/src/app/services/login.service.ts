import { Injectable } from '@angular/core';
import {
  BinaryConverter,
  DefaultBinaryConverter,
  DefaultStringConverter,
  StringConverter,
} from '@diplomatiq/convertibles';
import { scrypt } from 'scrypt-js';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private readonly localStorageKey = 'binance-trader-bot-key';

  private readonly localStorage: Storage = window.localStorage;
  private readonly stringConverter: StringConverter =
    new DefaultStringConverter();
  private readonly binaryConverter: BinaryConverter =
    new DefaultBinaryConverter();

  public isLoggedIn(): boolean {
    return this.getKey() !== '';
  }

  public getKey(): string {
    return this.localStorage.getItem(this.localStorageKey) ?? '';
  }

  public async login(emailAddress: string, password: string): Promise<void> {
    const keyBinary = await scrypt(
      this.stringConverter.encodeToBytes(password),
      this.stringConverter.encodeToBytes(emailAddress),
      8192,
      8,
      1,
      32
    );
    const keyString = this.binaryConverter.encodeToBase64(keyBinary);
    this.localStorage.setItem(this.localStorageKey, keyString);
  }

  public logout(): void {
    this.localStorage.removeItem(this.localStorageKey);
  }
}
