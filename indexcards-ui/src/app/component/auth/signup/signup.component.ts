import {HttpClient} from '@angular/common/http';
import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {MessageService} from 'primeng/api';
import {TranslateService} from "@ngx-translate/core";
import {HttpService} from "../../../services/http.service";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  public signUpFormGroup: FormGroup;

  constructor(
    private http: HttpClient,
    private httpService: HttpService,
    private router: Router,
    private messageService: MessageService,
    private formBuilder: FormBuilder,
    private translateService: TranslateService
  ) {
    this.signUpFormGroup = this.formBuilder.group({
      username: ['', Validators.required],
      surname: ['', Validators.required],
      firstname: ['', Validators.required],
      password: ['', Validators.required],
      repeatPassword: ['', Validators.required]
    });
  }

  createAccount() {
    if (!this.signUpFormGroup.valid) {
      this.messageService.add({
        key: 'tr',
        severity: 'error',
        summary: this.translateService.instant('common.error'),
        detail: this.translateService.instant('auth.fields_required'),
      });
      return;
    }

    if (this.signUpFormGroup.get('password')?.value !== this.signUpFormGroup.get('repeatPassword')?.value) {
      this.messageService.add({
        key: 'tr',
        severity: 'error',
        summary: this.translateService.instant('common.error'),
        detail: this.translateService.instant('auth.password_match'),
      });
      return;
    }

    this.httpService.post<undefined>('/api/auth/signup', this.getCreateAccountParameter(), (_) => {
      this.messageService.add({
        key: 'tr',
        severity: 'success',
        summary: this.translateService.instant('common.success'),
        detail: this.translateService.instant('auth.account_created'),
      });
      this.router.navigate(['/login']).then();
    });
  }

  getCreateAccountParameter() {
    return {
      username: this.signUpFormGroup.get('username')?.value,
      firstname: this.signUpFormGroup.get('firstname')?.value,
      surname: this.signUpFormGroup.get('surname')?.value,
      password: this.signUpFormGroup.get('password')?.value,
    }
  }
}
