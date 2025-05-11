package fr.clinique.model;

import java.util.List;

/**
 * Interface générique pour les modèles qui utilisent la persistance des données.
 * @param <T> Le type d'objet géré par le modèle
 */
public interface Model<T> {
    /**
     * Enregistre l'objet dans la base de données (création ou mise à jour).
     * @return true si l'enregistrement a réussi, false sinon
     */
    boolean enregistrer();

    /**
     * Supprime l'objet de la base de données.
     * @return true si la suppression a réussi, false sinon
     */
    boolean supprimer();

    /**
     * Récupère tous les objets de ce type depuis la base de données.
     * @return Liste contenant tous les objets
     */
    List<T> afficherTous();

    /**
     * Recherche un objet par son identifiant.
     * @param id L'identifiant de l'objet à rechercher
     * @return L'objet trouvé ou null si aucun objet ne correspond
     */
    T rechercherParId(int id);
}