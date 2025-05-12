import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RiskListComponent } from './components/risk-list/risk-list.component';
import { RiskFormComponent } from './components/risk-form/risk-form.component';

const routes: Routes = [
  { path: '', component: RiskListComponent },
  { path: 'add', component: RiskFormComponent },
  { path: 'edit/:id', component: RiskFormComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RiskRoutingModule {}