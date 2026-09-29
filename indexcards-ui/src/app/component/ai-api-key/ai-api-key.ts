import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnInit,
  inject,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { MatButton } from '@angular/material/button';
import { MatFormField, MatHint, MatInput, MatLabel } from '@angular/material/input';
import { AiService } from '../../service/ai/ai.service';
import { SnackbarService } from '../../service/snackbar/snackbar.service';
import { AiStatusResponse } from '../../app.responses';

/**
 * Account section where users save their own Gemini API key for the AI card generation. The key is sent once and
 * never shown again; the backend only returns its last characters.
 */
@Component({
  selector: 'app-ai-api-key',
  imports: [
    FormsModule,
    RouterLink,
    TranslatePipe,
    MatButton,
    MatFormField,
    MatHint,
    MatInput,
    MatLabel,
  ],
  templateUrl: './ai-api-key.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './ai-api-key.css',
})
export class AiApiKey implements OnInit {
  private aiService = inject(AiService);
  private snackbarService = inject(SnackbarService);
  private cdr = inject(ChangeDetectorRef);

  status?: AiStatusResponse;
  apiKey = '';
  saving = false;

  ngOnInit(): void {
    this.aiService.getStatus((status) => {
      this.status = status;
      this.cdr.detectChanges();
    });
  }

  saveApiKey(): void {
    const apiKey = this.apiKey.trim();
    if (!apiKey || this.saving) {
      return;
    }
    this.saving = true;
    this.aiService.saveApiKey(
      apiKey,
      (status) => {
        this.status = status;
        this.apiKey = '';
        this.saving = false;
        this.snackbarService.showSuccessMessage('ai.key_saved');
        this.cdr.detectChanges();
      },
      () => {
        this.saving = false;
        this.cdr.detectChanges();
      },
    );
  }

  deleteApiKey(): void {
    this.aiService.deleteApiKey((status) => {
      if (status) {
        this.status = status;
      }
      this.snackbarService.showSuccessMessage('ai.key_deleted');
      this.cdr.detectChanges();
    });
  }
}
