import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-statistics',
  template: `<h2 class="text-gold">Statistics Component</h2>`,
  styles: [`.text-gold { color: #d4af37; }`]
})
export class StatisticsComponent implements OnInit {
  constructor() { }
  ngOnInit(): void { }
}
