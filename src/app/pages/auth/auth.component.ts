import { Component, OnInit, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss']
})
export class AuthComponent implements OnInit, AfterViewInit {

  constructor() { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    // We'll use the data-bs-toggle attribute for tabs instead of JavaScript initialization
  }

  // Method to switch to register tab programmatically
  switchToRegister(): void {
    const registerTab = document.querySelector('#register-tab') as HTMLElement;
    if (registerTab) {
      registerTab.click();
    }
  }

  // Method to switch to login tab programmatically
  switchToLogin(): void {
    const loginTab = document.querySelector('#login-tab') as HTMLElement;
    if (loginTab) {
      loginTab.click();
    }
  }
}
