import { Injectable, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ProjectResponse } from '../../app.responses';
import { ConfirmDialog, ConfirmDialogData } from '../../component/confirm-dialog/confirm-dialog';
import HttpService from '../http/http.service';
import { SnackbarService } from '../snackbar/snackbar.service';

@Injectable({
  providedIn: 'root',
})
export class ProjectArchiveService {
  private dialog = inject(MatDialog);
  private httpService = inject(HttpService);
  private snackbarService = inject(SnackbarService);

  /**
   * Archives the project after the user confirmed it, or unarchives it right away.
   * @param project project to archive or unarchive
   * @param onChanged called once the project was archived or unarchived
   */
  toggleArchive(project: ProjectResponse, onChanged: () => void): void {
    if (project.archived) {
      this.setArchived(project, false, onChanged);
      return;
    }

    this.dialog
      .open<ConfirmDialog, ConfirmDialogData, boolean>(ConfirmDialog, {
        width: '480px',
        maxWidth: '90vw',
        // Focus the dialog itself so no button shows its focus background on open.
        autoFocus: 'dialog',
        data: { project, action: 'archive' },
      })
      .afterClosed()
      .subscribe((result) => {
        if (result === true) {
          this.setArchived(project, true, onChanged);
        }
      });
  }

  private setArchived(project: ProjectResponse, archived: boolean, onChanged: () => void): void {
    this.httpService.put<undefined>(
      `/api/project/${project.id}/${archived ? 'archive' : 'unarchive'}`,
      {},
      () => {
        this.snackbarService.showSuccessMessage(
          archived ? 'project.archived_success' : 'project.unarchived_success',
        );
        onChanged();
      },
    );
  }
}
