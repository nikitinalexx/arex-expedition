import {Component, Inject, InjectionToken, OnInit} from '@angular/core';
import {CrisisRecordEntity} from '../data/CrisisRecordEntity';
import {GameRepository} from '../model/gameRepository.model';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Subscription} from "rxjs";

export const BASE_URL = new InjectionToken('rest_url');

@Component({
  selector: 'app-crisis-leaderboard',
  templateUrl: './crisisLeaderboard.component.html',
  styleUrls: ['./crisisLeaderboard.component.css']
})
export class CrisisLeaderboardComponent implements OnInit {
  records: CrisisRecordEntity[];
  errorMessage: string;
  loading: boolean;
  loadingResult: Subscription;

  parentForm: FormGroup;

  constructor(private model: GameRepository,
              private formBuilder: FormBuilder,
              @Inject(BASE_URL) private url: string) {
  }

  ngOnInit(): void {
    this.parentForm = this.formBuilder.group({
      playerCount: ['1', Validators.required]
    });

    this.loadCrisisRecords(1);
  }

  loadCrisisRecords(playerCount: number) {
    this.records = null;
    this.loading = true;
    this.loadingResult?.unsubscribe();
    this.loadingResult = this.model.getCrisisRecords(playerCount).subscribe(response => {
      if (response) {
        this.loading = false;
        this.records = response;
      }
    }, error => {
      this.loading = false;
      this.errorMessage = error;
    });
  }

  clickPlayerCount(playerCount: number) {
    this.loadCrisisRecords(playerCount);
  }

  getPlayerLink(player: string): string {
    return this.url + '/game/' + player;
  }

}
