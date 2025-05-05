package tn.esprit.pidev.utils;

import tn.esprit.pidev.entities.Loantype;
import tn.esprit.pidev.entities.Status;

/**
 * Classe utilitaire pour convertir des chaînes de caractères en enums
 */
public class EnumConverter {

    /**
     * Convertit une chaîne de caractères en enum Status
     * @param statusStr La chaîne de caractères à convertir
     * @return L'enum Status correspondant, ou null si la conversion échoue
     */
    public static Status convertToStatus(String statusStr) {
        if (statusStr == null) {
            return null;
        }
        
        try {
            return Status.valueOf(statusStr.toLowerCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de conversion du statut: " + statusStr);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Convertit une chaîne de caractères en enum Loantype
     * @param loantypeStr La chaîne de caractères à convertir
     * @return L'enum Loantype correspondant, ou null si la conversion échoue
     */
    public static Loantype convertToLoantype(String loantypeStr) {
        if (loantypeStr == null) {
            return null;
        }
        
        try {
            return Loantype.valueOf(loantypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de conversion du type de prêt: " + loantypeStr);
            e.printStackTrace();
            return null;
        }
    }
}
