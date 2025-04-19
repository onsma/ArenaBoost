import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-loan-details',
  template: `<h2 class="text-gold">Loan Details Component</h2>`,
  styles: [`.text-gold { color: #d4af37; }`]
})
export class LoanDetailsComponent implements OnInit {
  constructor() { }
  ngOnInit(): void { }
}
