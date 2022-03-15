import { Component, Inject } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Duration } from 'luxon';
import { map, Observable, startWith } from 'rxjs';
import { SymbolService } from 'src/app/services/symbol.service';
import { TradingStrategyService } from 'src/app/services/trading-strategy.service';
import { BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy } from 'src/app/types/strategies/buyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy';
import { TradingStrategy } from 'src/app/types/strategies/tradingStrategy';
import { TradingConfiguration } from 'src/app/types/tradingConfiguration';
import { TradingStrategyName } from 'src/app/types/tradingStrategyName';
import { CustomValidators } from 'src/app/utils/customValidators';
import { CreateOrEditTradingStrategyDialogData } from './create-or-edit-trading-strategy-dialog-data';

@Component({
  selector:
    'binance-trader-bot-frontend-create-or-edit-trading-strategy-dialog',
  templateUrl: './create-or-edit-trading-strategy-dialog.component.html',
  styleUrls: ['./create-or-edit-trading-strategy-dialog.component.scss'],
})
export class CreateOrEditTradingStrategyDialogComponent {
  public tradingPair = new FormControl('', [
    Validators.required,
    CustomValidators.allowedValues(this.data.tradableSymbols),
  ]);
  public filteredTradingPairs?: Observable<string[]>;

  public tradingStrategy = new FormControl('', [
    Validators.required,
    CustomValidators.allowedValues(this.data.tradingStrategies),
  ]);

  public tradingConfigurationEnabled = false;

  public priceMonitorWindowDays = new FormControl('', [Validators.required]);
  public priceMonitorWindowHours = new FormControl('', [Validators.required]);
  public priceMonitorWindowMinutes = new FormControl('', [Validators.required]);
  public priceMonitorWindowSeconds = new FormControl('', [Validators.required]);

  public buyOnPercentageDecreaseInTimeframeAndSetLimitOrderForm = {
    priceDecreaseTriggerPercentage: new FormControl('', [Validators.required]),
    buySpendAmount: new FormControl('', [Validators.required]),
    limitSellPricePercentage: new FormControl('', [Validators.required]),
  };

  public loaded = false;

  public get quoteCurrency(): string {
    switch (this.data.state) {
      case 'create':
      case 'editOne':
        return this.symbolService.getQuoteAssetFromFriendlyName(
          this.tradingPair.value
        );

      case 'editMultiple':
        return 'egység';
    }
  }

  public constructor(
    private readonly dialogRef: MatDialogRef<CreateOrEditTradingStrategyDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public readonly data: CreateOrEditTradingStrategyDialogData,
    private readonly tradingStrategyService: TradingStrategyService,
    private readonly symbolService: SymbolService
  ) {
    this.filteredTradingPairs = this.tradingPair.valueChanges.pipe(
      startWith(''),
      map((value) =>
        Array.from(this.data.tradableSymbols).filter((tradingPair) =>
          tradingPair.toLowerCase().includes(value.toLowerCase())
        )
      )
    );

    if (this.data.state === 'editOne') {
      this.tradingPair.setValue(this.data.tradingConfiguration!.symbol);

      const tradingStrategy = this.data.tradingConfiguration!.strategy;
      const tradingStrategyName =
        this.tradingStrategyService.getTradingStrategyName(tradingStrategy);
      this.tradingStrategy.setValue(tradingStrategyName);

      this.tradingConfigurationEnabled =
        this.data.tradingConfiguration!.enabled;

      const priceMonitorWindowDuration = Duration.fromObject({
        seconds:
          this.data.tradingConfiguration!.strategy.priceMonitorWindowSeconds,
      }).shiftTo('days', 'hours', 'minutes', 'seconds');

      this.priceMonitorWindowDays.setValue(priceMonitorWindowDuration.days);
      this.priceMonitorWindowHours.setValue(priceMonitorWindowDuration.hours);
      this.priceMonitorWindowMinutes.setValue(
        priceMonitorWindowDuration.minutes
      );
      this.priceMonitorWindowSeconds.setValue(
        priceMonitorWindowDuration.seconds
      );

      switch (tradingStrategyName) {
        case BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy.NAME:
          this.buyOnPercentageDecreaseInTimeframeAndSetLimitOrderForm.priceDecreaseTriggerPercentage.setValue(
            (
              tradingStrategy as BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy
            ).priceDecreaseTriggerRatio * 100
          );
          this.buyOnPercentageDecreaseInTimeframeAndSetLimitOrderForm.buySpendAmount.setValue(
            (
              tradingStrategy as BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy
            ).buySpendAmount
          );
          this.buyOnPercentageDecreaseInTimeframeAndSetLimitOrderForm.limitSellPricePercentage.setValue(
            (
              tradingStrategy as BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy
            ).limitSellPriceRatio * 100
          );
          break;
      }
    }
  }

  public getTradingStrategyDescription(
    tradingStrategyName: TradingStrategyName
  ): string {
    return this.tradingStrategyService.getTradingStrategyDesciption(
      tradingStrategyName
    );
  }

  public isValid(): boolean {
    let tradingPairIsValid =
      this.data.state !== 'create' || this.tradingPair.valid;

    let strategyFormIsValid = false;
    switch (this.tradingStrategy.value) {
      case BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy.NAME:
        strategyFormIsValid =
          this.buyOnPercentageDecreaseInTimeframeAndSetLimitOrderForm
            .priceDecreaseTriggerPercentage.valid &&
          this.buyOnPercentageDecreaseInTimeframeAndSetLimitOrderForm
            .buySpendAmount.valid &&
          this.buyOnPercentageDecreaseInTimeframeAndSetLimitOrderForm
            .limitSellPricePercentage.valid;
        break;
    }

    return (
      tradingPairIsValid &&
      this.tradingStrategy.valid &&
      this.priceMonitorWindowDays.valid &&
      this.priceMonitorWindowHours.valid &&
      this.priceMonitorWindowMinutes.valid &&
      this.priceMonitorWindowSeconds.valid &&
      strategyFormIsValid
    );
  }

  public save(): void {
    let symbol: string;
    switch (this.data.state) {
      case 'create':
        symbol = this.tradingPair.value;
        break;

      case 'editOne':
        symbol = this.data.tradingConfiguration!.symbol;
        break;

      case 'editMultiple':
        symbol = '';
        break;
    }

    const priceMonitorWindowSeconds = Duration.fromObject({
      days: this.priceMonitorWindowDays.value,
      hours: this.priceMonitorWindowHours.value,
      minutes: this.priceMonitorWindowMinutes.value,
      seconds: this.priceMonitorWindowSeconds.value,
    }).shiftTo('seconds').seconds;

    let strategy: TradingStrategy;
    switch (this.tradingStrategy.value) {
      case BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy.NAME:
        strategy =
          new BuyOnPercentageDecreaseInTimeframeAndSetLimitOrderStrategy(
            priceMonitorWindowSeconds,
            this.buyOnPercentageDecreaseInTimeframeAndSetLimitOrderForm
              .priceDecreaseTriggerPercentage.value / 100,
            this.buyOnPercentageDecreaseInTimeframeAndSetLimitOrderForm.buySpendAmount.value,
            this.buyOnPercentageDecreaseInTimeframeAndSetLimitOrderForm
              .limitSellPricePercentage.value / 100
          );
        break;

      default:
        throw new Error();
    }

    const result: TradingConfiguration = {
      symbol,
      strategy,
      enabled: this.tradingConfigurationEnabled,
    };
    this.dialogRef.close(result);
  }

  public cancel(): void {
    this.dialogRef.close();
  }
}
