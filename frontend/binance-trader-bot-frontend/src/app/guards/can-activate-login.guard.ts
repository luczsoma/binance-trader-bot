import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { LoginService } from '../services/login.service';

@Injectable({
  providedIn: 'root',
})
export class CanActivateLoginGuard implements CanActivate {
  public constructor(
    private readonly loginService: LoginService,
    private readonly router: Router
  ) {}

  public canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): true | UrlTree {
    return !this.loginService.isLoggedIn() ? true : this.router.parseUrl('/');
  }
}
