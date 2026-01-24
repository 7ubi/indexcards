import {Component, inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {DialogData} from '../../pages/project/all-projects/all-projects';
import {TranslatePipe} from '@ngx-translate/core';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.html',
  styleUrl: './confirm-dialog.css',
  imports: [
    MatDialogTitle,
    TranslatePipe,
    MatDialogActions,
    MatButton,
    MatDialogClose
  ]
})
export class ConfirmDialog {
  readonly dialogRef = inject(MatDialogRef<ConfirmDialog>);
  readonly data = inject<DialogData>(MAT_DIALOG_DATA);
}
