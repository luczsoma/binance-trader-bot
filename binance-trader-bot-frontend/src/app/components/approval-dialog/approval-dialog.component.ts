import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ApprovalDialogData } from './approval-dialog-data';

@Component({
  selector: 'binance-trader-bot-frontend-approval-dialog',
  templateUrl: './approval-dialog.component.html',
  styleUrls: ['./approval-dialog.component.scss'],
})
export class ApprovalDialogComponent {
  public constructor(
    @Inject(MAT_DIALOG_DATA)
    public readonly data: ApprovalDialogData
  ) {}
}
