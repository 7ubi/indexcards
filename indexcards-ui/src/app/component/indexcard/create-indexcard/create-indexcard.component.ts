import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ResultResponse} from "../../../app.response";
import {LoginService} from "../../auth/login/login.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-create-indexcard',
  templateUrl: './create-indexcard.component.html',
  styleUrls: ['./create-indexcard.component.css']
})
export class CreateIndexcardComponent implements OnInit {

  id: string | null | undefined;

  createIndexCardFormGroup: FormGroup;

  constructor(
    private http: HttpClient,
    private loginService: LoginService,
    private messageService: MessageService,
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private translateService: TranslateService
  ) {
    this.createIndexCardFormGroup = this.formBuilder.group({
      answer: ['', Validators.required],
      question: ['', Validators.required],
    });
  }

  ngOnInit(): void {
      this.id = this.route.snapshot.paramMap.get('id');
    }

  createIndexcard() {
    if(!this.createIndexCardFormGroup.valid) {
      this.throwInvalidForm();
      return;
    }

    this.http.post<ResultResponse>('/api/indexCard/create', this.createRequest(),
      { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => {
          if(response.success) {
            this.messageService.add({
              key: 'tr',
              severity: 'success',
              summary: this.translateService.instant('common.success'),
              detail: this.translateService.instant('indexcard.created'),
            });
            this.router.navigate(["/project", this.id]);
          }
        }, err => {
          const response: ResultResponse = err.error;

          response.errorMessages.forEach(error => {
            this.messageService.add({
              key: 'tr',
              severity: 'error',
              summary: this.translateService.instant('common.error'),
              detail: error.message,
            });
          })
        }
      )
  }

  createRequest() {
    return {
      projectId: this.id,
      question: this.createIndexCardFormGroup.get('question')?.value,
      answer: this.createIndexCardFormGroup.get('answer')?.value
    };
  }

  throwInvalidForm() {
    this.messageService.add({
      key: 'tr',
      severity: 'error',
      summary: 'ERROR',
      detail: 'Answer and Question are required to create an index card!',
    });
  }
}
