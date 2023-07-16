import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-assessment-button',
  templateUrl: './assessment-button.component.html',
  styleUrls: ['./assessment-button.component.css']
})
export class AssessmentButtonComponent {
  @Input() assessment: string = '';
}
