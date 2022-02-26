import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class SymbolService {
  public getBaseAsset(symbolId: string): string {
    return symbolId.split('_')[0];
  }

  public getQuoteAsset(symbolId: string): string {
    return symbolId.split('_')[1];
  }
}
