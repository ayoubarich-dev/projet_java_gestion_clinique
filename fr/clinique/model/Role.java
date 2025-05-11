package fr.clinique.model;

/**
 * Enumération des rôles possibles pour les utilisateurs.
 */
public enum Role {
    /**
     * Administrateur du système avec tous les droits.
     */
    ADMINISTRATEUR,
    
    /**
     * Médecin pouvant gérer ses rendez-vous.
     */
    MEDECIN,
    
    /**
     * Secrétaire pouvant gérer les patients et les rendez-vous.
     */
    SECRETAIRE
}