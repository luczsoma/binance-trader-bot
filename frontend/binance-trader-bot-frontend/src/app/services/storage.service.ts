import { Inject, Injectable } from '@angular/core';
import { LocalStorageInjectionToken } from '../injection-tokens/local-storage.injection-token';

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  public constructor(
    @Inject(LocalStorageInjectionToken)
    private readonly localStorage: Storage
  ) {}

  public get(key: string): string | null {
    return this.localStorage.getItem(key);
  }

  public set(key: string, value: string): void {
    this.localStorage.setItem(key, value);
  }

  public remove(key: string): void {
    this.localStorage.removeItem(key);
  }

  public clear(): void {
    this.localStorage.clear();
  }
}
