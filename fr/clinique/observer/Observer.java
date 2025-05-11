package fr.clinique.observer;

import fr.clinique.model.RendezVous;

/**
 * Interface Observer pour le patron de conception Observer.
 * Permet d'être notifié lors de la création ou modification d'un rendez-vous.
 */
public interface Observer {
    /**
     * Méthode appelée lorsqu'un rendez-vous est créé ou modifié.
     * @param rendezVous Le rendez-vous concerné
     */
    void update(RendezVous rendezVous);
}