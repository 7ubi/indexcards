import {Component, OnInit} from '@angular/core';
import {IndexCardResponse} from "../../../app.response";
import {HttpService} from "../../../services/http.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-edit-indexcard',
  templateUrl: './edit-indexcard.component.html',
  styleUrls: ['./edit-indexcard.component.css']
})
export class EditIndexcardComponent implements OnInit {

  indexCardResponse?: IndexCardResponse;

  editIndexCardFormGroup: FormGroup;

  id: string | null = null;
  indexCardId: string | null = null;

  constructor(private httpService: HttpService, private formBuilder: FormBuilder, private messageService: MessageService,
              private router: Router, private route: ActivatedRoute, private translateService: TranslateService) {

    this.editIndexCardFormGroup = this.formBuilder.group({
      answer: ['', Validators.required],
      question: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    this.indexCardId = this.route.snapshot.paramMap.get('indexCardId');

    this.httpService.get<IndexCardResponse>(`/api/indexCard/get?id=${this.indexCardId}`, response => {
      this.indexCardResponse = response;
      this.editIndexCardFormGroup.setValue({answer: response.answer, question: response.question});
    });

  }


  editIndexCard() {
    if (!this.editIndexCardFormGroup.valid) {
      this.throwInvalidForm();
      return;
    }

    this.httpService.put<undefined>(`/api/indexCard/edit?id=${this.indexCardId}`, this.getRequest(), response => {
      this.messageService.add({
        key: 'tr',
        severity: 'success',
        summary: this.translateService.instant('common.success'),
        detail: this.translateService.instant('indexcard.created'),
      });
      this.router.navigate(["/project", this.id]).then();
    })
  }

  private getRequest() {
    return {
      question: this.editIndexCardFormGroup.get('question')?.value,
      answer: this.editIndexCardFormGroup.get('answer')?.value,
    }
  }

  throwInvalidForm() {
    this.messageService.add({
      key: 'tr',
      severity: 'error',
      summary: this.translateService.instant('common.error'),
      detail: this.translateService.instant('indexcard.required'),
    });
  }
}
