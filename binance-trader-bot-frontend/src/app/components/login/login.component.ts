import { Component } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from 'src/app/services/api.service';
import { LoginService } from 'src/app/services/login.service';

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

  public constructor(
    private readonly loginService: LoginService,
    private readonly apiService: ApiService,
    private readonly router: Router
  ) {}

  public async login(): Promise<void> {
    await this.loginService.login(
      this.emailFormControl.value,
      this.passwordFormControl.value
    );

    try {
      await this.loginService.withLoginErrorHandling(async () =>
        this.apiService.getBalances()
      );
      await this.router.navigateByUrl('/');
    } catch (ex) {}
  }
}
