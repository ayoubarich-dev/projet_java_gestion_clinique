package fr.clinique.model;

import java.util.List;

/**
 * Classe façade pour les opérations sur les médecins.
 */
public class MedecinModel {
    /**
     * Récupère tous les médecins.
     * @return Liste de tous les médecins
     */
    public static List<Medecin> getTousMedecins() {
        return Medecin.afficherTousMedecins();
    }

    /**
     * Recherche un médecin par son identifiant.
     * @param id L'identifiant du médecin
     * @return Le médecin trouvé ou null si aucun médecin ne correspond
     */
    public static Medecin getMedecinById(int id) {
        Utilisateur utilisateur = new Utilisateur();
        Personne personne = utilisateur.rechercherParId(id);
        if (personne instanceof Medecin) {
            return (Medecin) personne;
        }
        return null;
    }

    /**
     * Recherche un médecin par l'identifiant de son utilisateur.
     * @param idUtilisateur L'identifiant de l'utilisateur
     * @return Le médecin trouvé ou null si aucun médecin ne correspond
     */
    public static Medecin getMedecinByUtilisateurId(int idUtilisateur) {
        return Medecin.rechercherParIdUtilisateur(idUtilisateur);
    }

    /**
     * Ajoute un nouveau médecin.
     * @param nom Le nom du médecin
     * @param prenom Le prénom du médecin
     * @param specialite La spécialité du médecin
     * @param horaires Les horaires du médecin
     * @param login Le login du médecin
     * @param password Le mot de passe du médecin
     * @return true si l'ajout a réussi, false sinon
     */
    public static boolean ajouterMedecin(String nom, String prenom, String specialite, String horaires, String login, String password) {
        Medecin medecin = new Medecin(nom, prenom, login, password, specialite, horaires);
        return medecin.enregistrer();
    }

    /**
     * Modifie un médecin existant.
     * @param id L'identifiant du médecin
     * @param nom Le nouveau nom du médecin
     * @param prenom Le nouveau prénom du médecin
     * @param specialite La nouvelle spécialité du médecin
     * @param horaires Les nouveaux horaires du médecin
     * @return true si la modification a réussi, false sinon
     */
    public static boolean modifierMedecin(int id, String nom, String prenom, String specialite, String horaires) {
        Medecin medecin = getMedecinById(id);
        if (medecin == null) {
            return false;
        }

        medecin.setNom(nom);
        medecin.setPrenom(prenom);
        medecin.setSpecialite(specialite);
        medecin.setHoraires(horaires);

        return medecin.enregistrer();
    }

    /**
     * Supprime un médecin.
     * @param id L'identifiant du médecin
     * @return true si la suppression a réussi, false sinon
     */
    public static boolean supprimerMedecin(int id) {
        Medecin medecin = getMedecinById(id);
        if (medecin == null) {
            return false;
        }

        return medecin.supprimer();
    }

    /**
     * Récupère les messages d'un médecin.
     * @param idMedecin L'identifiant du médecin
     * @return Liste des messages du médecin
     */
    public static List<String> getMessagesPourMedecin(int idMedecin) {
        return Medecin.getMessagesPourMedecin(idMedecin);
    }

    /**
     * Efface les messages d'un médecin.
     * @param idMedecin L'identifiant du médecin
     */
    public static void clearMessagesPourMedecin(int idMedecin) {
        Medecin.clearMessagesPourMedecin(idMedecin);
    }

    /**
     * Ajoute un message pour un médecin.
     * @param idMedecin L'identifiant du médecin
     * @param message Le message à ajouter
     */
    public static void ajouterMessagePourMedecin(int idMedecin, String message) {
        Medecin.ajouterMessagePourMedecin(idMedecin, message);
    }
}