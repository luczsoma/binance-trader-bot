import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from '../components/login/login.component';
import { TraderComponent } from '../components/trader/trader.component';
import { CanActivateLoginGuard } from '../guards/can-activate-login.guard';
import { CanActivateTraderGuard } from '../guards/can-activate-trader.guard';

const routes: Routes = [
  {
    path: '',
    component: TraderComponent,
    canActivate: [CanActivateTraderGuard],
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [CanActivateLoginGuard],
  },
  {
    path: '**',
    redirectTo: '/',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
