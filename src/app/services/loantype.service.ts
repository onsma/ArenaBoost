import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoanType } from './models/loan.model';

@Injectable({
  providedIn: 'root'
})
export class LoanTypeService {
  private loanTypes: LoanType[] = [
    {
      id_loantype: 1,
      name: 'EQUIPMENT',
      description: 'Crédit pour équipement sportif'
    },
    {
      id_loantype: 2,
      name: 'TRAINING',
      description: 'Crédit pour formation et coaching'
    },
    {
      id_loantype: 3,
      name: 'MEDICAL',
      description: 'Crédit pour soins médicaux'
    },
    {
      id_loantype: 4,
      name: 'TRAVEL',
      description: 'Crédit pour déplacements et compétitions'
    },
    {
      id_loantype: 5,
      name: 'MARKETING',
      description: 'Crédit pour sponsoring et image'
    },
    {
      id_loantype: 6,
      name: 'INFRASTRUCTURE',
      description: 'Crédit pour infrastructures sportives'
    },
    {
      id_loantype: 7,
      name: 'TEAM_MANAGEMENT',
      description: 'Crédit pour gestion des équipes'
    },
    {
      id_loantype: 8,
      name: 'TOURNAMENTS',
      description: 'Crédit pour participation aux tournois'
    },
    {
      id_loantype: 9,
      name: 'RECRUITMENT',
      description: 'Crédit pour recrutement de joueurs/coachs'
    }
  ];

  constructor() {}

  // Récupérer tous les types de prêt
  getAllLoanTypes(): Observable<LoanType[]> {
    return new Observable<LoanType[]>(observer => {
      observer.next(this.loanTypes);
      observer.complete();
    });
  }

  // Récupérer un type de prêt par ID
  getLoanType(id: number): Observable<LoanType> {
    const type = this.loanTypes.find(t => t.id_loantype === id);
    return new Observable<LoanType>(observer => {
      if (type) {
        observer.next(type);
      }
      observer.complete();
    });
  }
}
