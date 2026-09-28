import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { ProjectResponse } from '../../app.responses';

export type ConfirmAction = 'delete' | 'archive';

export interface ConfirmDialogData {
  project: ProjectResponse;
  action: ConfirmAction;
}

const ICONS: Record<ConfirmAction, string> = {
  delete: 'delete_forever',
  archive: 'archive',
};

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.html',
  styleUrl: './confirm-dialog.css',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    MatButton,
    MatIconButton,
    MatIcon,
    TranslatePipe,
  ],
})
export class ConfirmDialog {
  readonly dialogRef = inject(MatDialogRef<ConfirmDialog>);
  readonly data = inject<ConfirmDialogData>(MAT_DIALOG_DATA);

  readonly icon = ICONS[this.data.action];
  readonly keyPrefix = `confirm_dialog.${this.data.action}`;
  readonly cardCount = this.data.project.indexCardResponses?.length ?? 0;
}
