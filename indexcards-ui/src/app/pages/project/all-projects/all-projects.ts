import {ChangeDetectionStrategy, Component, model, ModelSignal, OnInit, signal, WritableSignal} from '@angular/core';
import {ProjectResponse} from '../../../app.responses';
import {Router, RouterLink} from '@angular/router';
import HttpService from '../../../service/http/http.service';
import {SnackbarService} from '../../../service/snackbar/snackbar.service';
import {MatDialog} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {ConfirmDialog} from '../../../component/confirm-dialog/confirm-dialog';
import {TranslatePipe} from '@ngx-translate/core';
import {MatIcon} from '@angular/material/icon';

export interface DialogData {
  project: ProjectResponse;
}

@Component({
  selector: 'app-all-projects',
  templateUrl: './all-projects.html',
  styleUrl: './all-projects.css',
  imports: [MatFormFieldModule, MatInputModule, FormsModule, MatButtonModule, TranslatePipe, MatIcon, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AllProjects implements OnInit {

  readonly project: ModelSignal<ProjectResponse> = model({} as ProjectResponse);
  userProjectsResponse: WritableSignal<ProjectResponse[]> = signal([]);

  constructor(
    private snackbarService: SnackbarService,
    private router: Router,
    private httpService: HttpService,
    private dialog: MatDialog,
  ) {
  }

  ngOnInit(): void {
    this.getAllProjects();
  }

  getAllProjects() {
    this.httpService.get<ProjectResponse[]>('/api/project/projects', (response: ProjectResponse[]) => {
      this.userProjectsResponse.set(response);
    });
  }

  goToProject(id: number) {
    this.router.navigate(['/project', id]).then();
  }

  deleteProject(id: number) {
    this.httpService.delete<undefined>(`/api/project/delete?id=${id}`, () => {
      this.snackbarService.showSuccessMessage('project.deleted');

      this.getAllProjects();
    });
  }

  openDialog(project: ProjectResponse): void {
    this.project.set(project);

    const dialogRef = this.dialog.open(ConfirmDialog, {
      data: {project: this.project()},
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.deleteProject(project.id);
        this.getAllProjects();
      }
    });
  }

  editProject(id: number) {
    this.router.navigate(['/project', id, 'edit']).then();
  }
}
