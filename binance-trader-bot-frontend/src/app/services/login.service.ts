import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ApiException } from '../types/apiException';
import { CryptoService } from './crypto.service';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private readonly loginKeyStorageKey = 'binance-trader-bot-login-key';

  public constructor(
    private readonly storageService: StorageService,
    private readonly cryptoService: CryptoService,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar
  ) {}

  public async withLoginErrorHandling<T>(cb: () => Promise<T>): Promise<T> {
    try {
      return await cb();
    } catch (ex) {
      if (ex instanceof ApiException) {
        if (ex.errorCode === 'Unauthorized') {
          await this.logout('invalid-credentials');
        }
      }

      throw ex;
    }
  }

  public isLoggedIn(): boolean {
    return this.getLoginKey() !== '';
  }

  public getLoginKey(): string {
    return this.storageService.get(this.loginKeyStorageKey) ?? '';
  }

  public async login(emailAddress: string, password: string): Promise<void> {
    const loginKey = await this.cryptoService.scrypt(password, emailAddress);
    this.storageService.set(this.loginKeyStorageKey, loginKey);
  }

  public async logout(
    reason?: 'regular-logout' | 'invalid-credentials'
  ): Promise<void> {
    this.storageService.remove(this.loginKeyStorageKey);
    await this.router.navigateByUrl('/login');

    switch (reason) {
      case 'regular-logout':
        this.snackBar.open('Sikeresen kijelentkezett.');
        break;

      case 'invalid-credentials':
        this.snackBar.open(
          'Érvénytelen bejelentkezési adatok. Jelentkezzen be újra!'
        );
        break;
    }
  }
}
