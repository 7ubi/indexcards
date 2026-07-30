import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  ChangeDetectionStrategy,
} from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { MathjaxDirective } from '../../directives/mathjax.directive';
import { MarkdownMathPipe } from '../../pipes/markdown-math.pipe';

@Component({
  selector: 'app-card-flip',
  standalone: true,
  imports: [TranslatePipe, MathjaxDirective, MarkdownMathPipe],
  templateUrl: './card-flip.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './card-flip.css',
})
export class CardFlip implements OnChanges {
  @Input()
  question?: string;

  @Input()
  answer?: string;

  @Input()
  showAnswer = false;

  @Output()
  showAnswerChange = new EventEmitter<boolean>();

  skipTransition = false;

  toggleAnswer(): void {
    this.showAnswerChange.emit(!this.showAnswer);
  }

  // Switching to a new card resets showAnswer to the front; animating that
  // reset would look like the previous card flipping back before the next
  // one appears, so the transition is suppressed for that single change.
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['question'] && !changes['question'].firstChange) {
      this.skipTransition = true;
      setTimeout(() => (this.skipTransition = false));
    }
  }
}
