import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class SymbolService {
  private readonly SYMBOL_ID_SEPARATOR = '_';
  private readonly FRIENDLY_NAME_SEPARATOR = '/';

  public getBaseAssetFromSymbolId(symbolId: string): string {
    return symbolId.split(this.SYMBOL_ID_SEPARATOR)[0].toUpperCase();
  }

  public getQuoteAssetFromSymbolId(symbolId: string): string {
    return symbolId.split(this.SYMBOL_ID_SEPARATOR)[1].toUpperCase();
  }

  public getFriendlyNameFromSymbolId(symbolId: string): string {
    return [
      this.getBaseAssetFromSymbolId(symbolId),
      this.getQuoteAssetFromSymbolId(symbolId),
    ]
      .join(this.FRIENDLY_NAME_SEPARATOR)
      .toUpperCase();
  }

  public getBaseAssetFromFriendlyName(friendlyName: string): string {
    return friendlyName.split(this.FRIENDLY_NAME_SEPARATOR)[0].toUpperCase();
  }

  public getQuoteAssetFromFriendlyName(friendlyName: string): string {
    return friendlyName.split(this.FRIENDLY_NAME_SEPARATOR)[1].toUpperCase();
  }

  public getSymbolIdFromFriendlyName(friendlyName: string): string {
    return [
      this.getBaseAssetFromFriendlyName(friendlyName),
      this.getQuoteAssetFromFriendlyName(friendlyName),
    ]
      .join(this.SYMBOL_ID_SEPARATOR)
      .toUpperCase();
  }
}
