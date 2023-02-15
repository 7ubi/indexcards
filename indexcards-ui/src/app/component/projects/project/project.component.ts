import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit{
  constructor(
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    console.log(id);
  }
}
