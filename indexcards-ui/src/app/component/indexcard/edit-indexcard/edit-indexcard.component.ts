import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MessageService} from "primeng/api";
import {ActivatedRoute, Router} from "@angular/router";
import {TranslateService} from "@ngx-translate/core";
import {HttpService} from "../../../services/http.service";

@Component({
  selector: 'app-edit-indexcard',
  templateUrl: './edit-indexcard.component.html',
  styleUrls: ['./edit-indexcard.component.css']
})
export class EditIndexcardComponent {
  editIndexCardFormGroup: FormGroup;

  constructor(
    private messageService: MessageService,
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private translateService: TranslateService,
    private httpService: HttpService
  ) {
    this.editIndexCardFormGroup = this.formBuilder.group({
      answer: ['', Validators.required],
      question: ['', Validators.required],
    });
  }

  editIndexCard() {

  }
}
