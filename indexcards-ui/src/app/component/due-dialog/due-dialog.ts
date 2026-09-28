import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle,
} from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';

export interface DueProject {
  projectName: string;
  cardCount: number;
}

export interface DueDialogData {
  cardCount: number;
  projects: DueProject[];
}

@Component({
  selector: 'app-due-dialog',
  templateUrl: './due-dialog.html',
  styleUrl: './due-dialog.css',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    MatButton,
    MatIconButton,
    MatIcon,
    MatTableModule,
    TranslatePipe,
  ],
})
export class DueDialog {
  readonly data = inject<DueDialogData>(MAT_DIALOG_DATA);

  readonly columns = ['project', 'count'];
}
