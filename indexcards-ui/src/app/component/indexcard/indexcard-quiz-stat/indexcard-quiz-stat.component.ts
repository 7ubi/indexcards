import {Component, OnInit} from '@angular/core';
import {Assessment, UserProjectResponse} from "../../../app.response";
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {MessageService} from "primeng/api";
import {LoginService} from "../../auth/login/login.service";
import {TranslateService} from "@ngx-translate/core";
import {HttpService} from "../../../services/http.service";

@Component({
  selector: 'app-indexcard-quiz-stat',
  templateUrl: './indexcard-quiz-stat.component.html',
  styleUrls: ['./indexcard-quiz-stat.component.css']
})
export class IndexcardQuizStatComponent implements OnInit{

  userProject?: UserProjectResponse;

  private id: string | null = '';

  data: any;

  chartOptions: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private messageService: MessageService,
    private loginService: LoginService,
    private translateService: TranslateService,
    private httpService: HttpService
  ) {
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');

    this.httpService.get<UserProjectResponse>(`/api/project/project?id=${this.id}`,
      response => {
          this.userProject = response;
          this.generateChartData();
        }, () => this.router.navigate(['']));
  }

  generateChartData(): void {
    this.chartOptions = {
      plugins: {
        legend: {
          labels: {
            color: '#FFFFFF'
          }
        }
      }
    };

    const datasets = [];
    const colors = [];
    const documentStyle = getComputedStyle(document.documentElement);
    for(const assessment in Object.keys(Assessment).filter((item) => {return isNaN(Number(item));})) {
      datasets.push( this.userProject?.projectResponse.indexCardResponses
          .filter(v => Assessment[v.assessment].toString() === assessment).length
      );
      colors.push(documentStyle.getPropertyValue(`--${Assessment[assessment].toString().toLowerCase()}`));
    }
    this.data = {
      title: "Status",
      labels: Object.keys(Assessment).filter((item) => {return isNaN(Number(item));}),
      datasets: [
        {
          data: datasets,
          backgroundColor: colors
        }
      ]
    }
  }

  onClickQuizButton() {
    this.router.navigate(['project', this.id, 'quiz']);
  }

  onClickToProject() {
    this.router.navigate(['project', this.id]);
  }
}
