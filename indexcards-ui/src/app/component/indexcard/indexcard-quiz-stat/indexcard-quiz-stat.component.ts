import {Component, OnInit} from '@angular/core';
import {Assessment, ResultResponse, UserProjectResponse} from "../../../app.response";
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {MessageService} from "primeng/api";
import {LoginService} from "../../auth/login/login.service";

@Component({
  selector: 'app-indexcard-quiz-stat',
  templateUrl: './indexcard-quiz-stat.component.html',
  styleUrls: ['./indexcard-quiz-stat.component.css']
})
export class IndexcardQuizStatComponent implements OnInit{

  userProject?: UserProjectResponse;

  data: any;

  chartOptions: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private messageService: MessageService,
    private loginService: LoginService
  ) {
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    this.http.get<UserProjectResponse>('/api/project/project?id=' + id,
      { headers: this.loginService.getHeaderWithBearer()})
      .subscribe(
        response => {
          this.userProject = response;
          this.generateChartData();
        }, err => {
          const response: ResultResponse = err.error;

          response.errorMessages.forEach(error => {
            this.messageService.add({
              key: 'tr',
              severity: 'error',
              summary: 'ERROR',
              detail: error.message,
            });
          });
          this.router.navigate(['']);
        }
      );
  }

  generateChartData(): void {
    this.chartOptions = {
      plugins: {
        legend: {
          labels: {
            color: '#495057'
          }
        }
      }
    };

    const datasets = [];
    const colors = [];
    const documentStyle = getComputedStyle(document.documentElement);
    for(const assessment in Object.keys(Assessment).filter((item) => {return isNaN(Number(item));})) {
      datasets.push( this.userProject?.projectResponse.indexCardResponses
          .filter(v => Assessment[v.assessment].toString() === assessment.toString()).length
      );
      colors.push(documentStyle.getPropertyValue('--' + Assessment[assessment].toString().toLowerCase()))
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
}
