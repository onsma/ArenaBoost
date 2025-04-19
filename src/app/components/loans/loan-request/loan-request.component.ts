import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-loan-request',
  template: `<h2 class="text-gold">Loan Request Component</h2>`,
  styles: [`.text-gold { color: #d4af37; }`]
})
export class LoanRequestComponent implements OnInit {
  constructor() { }
  ngOnInit(): void { }
}
