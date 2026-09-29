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

export type ConfirmAction = 'delete' | 'archive' | 'make_admin';

/** Exactly one of `project` or `username` is the subject shown in the dialog. */
export interface ConfirmDialogData {
  project?: ProjectResponse;
  username?: string;
  action: ConfirmAction;
  /** Translation key prefix for the texts; defaults to `confirm_dialog.<action>`. */
  keyPrefix?: string;
}

const ICONS: Record<ConfirmAction, string> = {
  delete: 'delete_forever',
  archive: 'archive',
  make_admin: 'admin_panel_settings',
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
  readonly keyPrefix = this.data.keyPrefix ?? `confirm_dialog.${this.data.action}`;
  readonly cardCount = this.data.project?.indexCardResponses?.length ?? 0;
}
