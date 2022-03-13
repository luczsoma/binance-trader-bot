import { AbstractControl, ValidatorFn } from '@angular/forms';

export class CustomValidators {
  public static allowedValues(allowedValues: Set<any>): ValidatorFn {
    return (control: AbstractControl) => {
      return allowedValues.has(control.value)
        ? null
        : { forbiddenValue: control.value };
    };
  }
}
