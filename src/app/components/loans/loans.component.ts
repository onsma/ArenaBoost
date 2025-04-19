import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-loans',
  template: `<h2 class="text-gold">Loans Component</h2>`,
  styles: [`.text-gold { color: #d4af37; }`]
})
export class LoansComponent implements OnInit {
  constructor() { }
  ngOnInit(): void { }
}
