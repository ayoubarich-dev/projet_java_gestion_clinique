package fr.clinique.model;

import java.util.List;

/**
 * Classe façade pour les opérations sur les utilisateurs.
 */
public class UtilisateurModel {
    /**
     * Authentifie un utilisateur avec son login et son mot de passe.
     * @param login Le login de l'utilisateur
     * @param password Le mot de passe de l'utilisateur
     * @return L'utilisateur authentifié ou null si l'authentification échoue
     */
    public static Utilisateur authentifier(String login, String password) {
        return Utilisateur.authentifier(login, password);
    }
    
    /**
     * Récupère toutes les secrétaires.
     * @return Liste de toutes les secrétaires
     */
    public static List<Secretaire> getSecretaires() {
        return Secretaire.getAllSecretaires();
    }
    
    /**
     * Récupère tous les médecins.
     * @return Liste de tous les médecins
     */
    public static List<Medecin> getMedecins() {
        return Medecin.afficherTousMedecins();
    }
    
    /**
     * Recherche un utilisateur par son identifiant.
     * @param id L'identifiant de l'utilisateur
     * @return L'utilisateur trouvé ou null si aucun utilisateur ne correspond
     */
    public static Utilisateur getUtilisateurById(int id) {
        Utilisateur utilisateur = new Utilisateur();
        Personne personne = utilisateur.rechercherParId(id);
        if (personne instanceof Utilisateur) {
            return (Utilisateur) personne;
        }
        return null;
    }
    
    /**
     * Ajoute une nouvelle secrétaire.
     * @param nom Le nom de la secrétaire
     * @param prenom Le prénom de la secrétaire
     * @param login Le login de la secrétaire
     * @param password Le mot de passe de la secrétaire
     * @return true si l'ajout a réussi, false sinon
     */
    public static boolean ajouterSecretaire(String nom, String prenom, String login, String password) {
        Secretaire secretaire = new Secretaire(nom, prenom, login, password);
        return secretaire.enregistrer();
    }
    
    /**
     * Supprime un utilisateur.
     * @param id L'identifiant de l'utilisateur
     * @return true si la suppression a réussi, false sinon
     */
    public static boolean supprimerUtilisateur(int id) {
        Utilisateur utilisateur = getUtilisateurById(id);
        if (utilisateur == null) {
            return false;
        }
        
        return utilisateur.supprimer();
    }
}