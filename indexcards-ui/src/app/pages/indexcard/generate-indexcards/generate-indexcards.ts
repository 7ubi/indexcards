import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnInit,
  inject,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatFormField, MatHint, MatInput, MatLabel } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatOption, MatSelect } from '@angular/material/select';
import { AiService } from '../../../service/ai/ai.service';
import { SnackbarService } from '../../../service/snackbar/snackbar.service';
import { AiStatusResponse } from '../../../app.responses';
import { LoadingSpinner } from '../../../component/loading-spinner/loading-spinner';
import { MathjaxDirective } from '../../../directives/mathjax.directive';
import { MarkdownMathPipe } from '../../../pipes/markdown-math.pipe';

export interface CardDraft {
  question: string;
  answer: string;
  selected: boolean;
  editing: boolean;
}

type Step = 'input' | 'generating' | 'review';

@Component({
  selector: 'app-generate-indexcards',
  imports: [
    FormsModule,
    RouterLink,
    TranslatePipe,
    MatButton,
    MatCheckbox,
    MatFormField,
    MatHint,
    MatInput,
    MatLabel,
    MatIcon,
    MatSelect,
    MatOption,
    LoadingSpinner,
    MathjaxDirective,
    MarkdownMathPipe,
  ],
  templateUrl: './generate-indexcards.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './generate-indexcards.css',
})
export class GenerateIndexcards implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private aiService = inject(AiService);
  private snackbarService = inject(SnackbarService);
  private cdr = inject(ChangeDetectorRef);

  private readonly cardCountOptions = [5, 10, 15, 20, 30];

  id: string | null = '';
  status?: AiStatusResponse;
  step: Step = 'input';
  notes = '';
  file?: File;
  cardCount = 10;
  cards: CardDraft[] = [];
  saving = false;

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    this.aiService.getStatus((status) => {
      this.status = status;
      if (!status.enabled) {
        this.router.navigate(['/project', this.id]);
      }
      this.cdr.detectChanges();
    });
  }

  allowedCardCounts(): number[] {
    const max = this.status?.maxCards ?? 30;
    return this.cardCountOptions.filter((count) => count <= max);
  }

  maxPdfMegabytes(): number {
    return Math.floor((this.status?.maxPdfBytes ?? 0) / (1024 * 1024));
  }

  canGenerate(): boolean {
    return (
      !!this.status?.enabled &&
      this.status.apiKeyConfigured &&
      this.notes.length <= this.status.maxNotesChars &&
      (this.notes.trim().length > 0 || !!this.file)
    );
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    if (!file) {
      return;
    }
    if (file.type !== 'application/pdf') {
      this.snackbarService.showErrorMessage('ai.pdf_only');
      return;
    }
    if (this.status && file.size > this.status.maxPdfBytes) {
      this.snackbarService.showErrorMessage('backend.ai_pdf_too_large');
      return;
    }
    this.file = file;
  }

  removeFile(): void {
    this.file = undefined;
  }

  generate(): void {
    if (!this.canGenerate() || !this.id) {
      return;
    }
    this.step = 'generating';
    this.aiService.generate(
      this.id,
      this.notes,
      this.cardCount,
      this.file,
      (response) => {
        this.cards = response.cards.map((card) => ({ ...card, selected: true, editing: false }));
        this.step = 'review';
        this.cdr.detectChanges();
      },
      () => {
        this.step = 'input';
        this.cdr.detectChanges();
      },
    );
  }

  selectedCount(): number {
    return this.cards.filter((card) => card.selected).length;
  }

  setAllSelected(selected: boolean): void {
    this.cards.forEach((card) => (card.selected = selected));
  }

  toggleEditing(card: CardDraft): void {
    card.editing = !card.editing;
  }

  isCardValid(card: CardDraft): boolean {
    return card.question.trim().length > 0 && card.answer.trim().length > 0;
  }

  canSave(): boolean {
    const selected = this.cards.filter((card) => card.selected);
    return !this.saving && selected.length > 0 && selected.every((card) => this.isCardValid(card));
  }

  backToInput(): void {
    this.step = 'input';
  }

  save(): void {
    if (!this.canSave() || !this.id) {
      return;
    }
    this.saving = true;
    const cards = this.cards
      .filter((card) => card.selected)
      .map((card) => ({ question: card.question.trim(), answer: card.answer.trim() }));
    this.aiService.saveCards(
      this.id,
      cards,
      () => {
        this.snackbarService.showSuccessMessage('ai.saved');
        this.router.navigate(['/project', this.id]);
      },
      () => {
        this.saving = false;
        this.cdr.detectChanges();
      },
    );
  }
}
