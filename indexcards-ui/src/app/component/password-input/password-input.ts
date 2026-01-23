import {Component, forwardRef, Input, signal, WritableSignal} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatFormField, MatInput, MatLabel, MatSuffix} from "@angular/material/input";
import {ControlValueAccessor, NG_VALUE_ACCESSOR, ReactiveFormsModule} from "@angular/forms";
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-password-input',
  imports: [
    MatIcon,
    MatIconButton,
    MatInput,
    MatSuffix,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    TranslatePipe
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PasswordInput),
      multi: true
    }
  ],
  templateUrl: './password-input.html',
  styleUrl: './password-input.css',
})
export class PasswordInput implements ControlValueAccessor {

  @Input()
  label: string = 'auth.password';

  value: string = '';

  hide: WritableSignal<boolean> = signal(true);

  private onChange: (_: any) => void = (_: any) => {
  };
  private onTouched: () => void = () => {
  };

  writeValue(obj: any): void {
    this.value = obj ?? '';
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
  }

  onInput(value: string) {
    this.value = value;
    this.onChange(value);
  }

  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }
}
