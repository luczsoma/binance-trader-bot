import { Component } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'binance-trader-bot-frontend-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  public readonly emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);
  public readonly passwordFormControl = new FormControl('', [
    Validators.required,
  ]);

  public async login(): Promise<void> {}
}
