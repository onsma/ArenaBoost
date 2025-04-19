import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-profile',
  template: `<h2 class="text-gold">Profile Component</h2>`,
  styles: [`.text-gold { color: #d4af37; }`]
})
export class ProfileComponent implements OnInit {
  constructor() { }
  ngOnInit(): void { }
}
