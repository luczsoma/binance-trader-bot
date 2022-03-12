import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TradingStrategyService } from 'src/app/services/trading-strategy.service';
import { TradingConfiguration } from 'src/app/types/tradingConfiguration';
import { TradingStrategyName } from 'src/app/types/tradingStrategyName';

@Component({
  selector:
    'binance-trader-bot-frontend-create-or-edit-trading-strategy-dialog',
  templateUrl: './create-or-edit-trading-strategy-dialog.component.html',
  styleUrls: ['./create-or-edit-trading-strategy-dialog.component.scss'],
})
export class CreateOrEditTradingStrategyDialogComponent {
  public readonly tradingStrategyNames: Set<TradingStrategyName>;

  public selectedTradingStrategy?: TradingStrategyName;

  public constructor(
    public dialogRef: MatDialogRef<CreateOrEditTradingStrategyDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public readonly tradingConfiguration: TradingConfiguration | undefined,
    private readonly tradingStrategyService: TradingStrategyService
  ) {
    this.tradingStrategyNames =
      this.tradingStrategyService.getTradingStrategyNames();
    this.selectedTradingStrategy = this.tradingConfiguration
      ? this.tradingStrategyService.getTradingStrategyName(
          this.tradingConfiguration.strategy
        )
      : undefined;
  }

  public getTradingStrategyDescription(
    tradingStrategyName: TradingStrategyName
  ): string {
    return this.tradingStrategyService.getTradingStrategyDesciption(
      tradingStrategyName
    );
  }

  public save(): void {
    this.dialogRef.close(this.tradingConfiguration);
  }

  public cancel(): void {
    this.dialogRef.close();
  }
}
