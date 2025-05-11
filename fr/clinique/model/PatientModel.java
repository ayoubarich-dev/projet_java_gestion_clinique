package fr.clinique.model;

import java.util.Date;
import java.util.List;

/**
 * Classe façade pour les opérations sur les patients.
 */
public class PatientModel {
    /**
     * Récupère tous les patients.
     * @return Liste de tous les patients
     */
    public static List<Patient> getTousPatients() {
        return Patient.getAllPatients();
    }

    /**
     * Récupère les patients suivis par un médecin.
     * @param idMedecin L'identifiant du médecin
     * @return Liste des patients suivis par le médecin
     */
    public static List<Patient> getPatientsParMedecin(int idMedecin) {
        return Patient.afficherParMedecin(idMedecin);
    }

    /**
     * Recherche un patient par son identifiant.
     * @param id L'identifiant du patient
     * @return Le patient trouvé ou null si aucun patient ne correspond
     */
    public static Patient getPatientById(int id) {
        Patient patient = new Patient();
        Personne personne = patient.rechercherParId(id);
        if (personne instanceof Patient) {
            return (Patient) personne;
        }
        return null;
    }

    /**
     * Recherche un patient par son numéro de dossier.
     * @param numeroDossier Le numéro de dossier du patient
     * @return Le patient trouvé ou null si aucun patient ne correspond
     */
    public static Patient rechercherParNumeroDossier(String numeroDossier) {
        return Patient.rechercherParNumeroDossier(numeroDossier);
    }

    /**
     * Ajoute un nouveau patient.
     * @param nom Le nom du patient
     * @param prenom Le prénom du patient
     * @param dateNaissance La date de naissance du patient
     * @param telephone Le numéro de téléphone du patient
     * @return true si l'ajout a réussi, false sinon
     */
    public static boolean ajouterPatient(String nom, String prenom, Date dateNaissance, String telephone) {
        Patient patient = new Patient(nom, prenom, dateNaissance, telephone);
        return patient.enregistrer();
    }

    /**
     * Modifie un patient existant.
     * @param id L'identifiant du patient
     * @param nom Le nouveau nom du patient
     * @param prenom Le nouveau prénom du patient
     * @param dateNaissance La nouvelle date de naissance du patient
     * @param telephone Le nouveau numéro de téléphone du patient
     * @return true si la modification a réussi, false sinon
     */
    public static boolean modifierPatient(int id, String nom, String prenom, Date dateNaissance, String telephone) {
        Patient patient = getPatientById(id);
        if (patient == null) {
            return false;
        }

        patient.setNom(nom);
        patient.setPrenom(prenom);
        patient.setDateNaissance(dateNaissance);
        patient.setTelephone(telephone);

        return patient.enregistrer();
    }

    /**
     * Supprime un patient.
     * @param id L'identifiant du patient
     * @return true si la suppression a réussi, false sinon
     */
    public static boolean supprimerPatient(int id) {
        Patient patient = getPatientById(id);
        if (patient == null) {
            return false;
        }

        return patient.supprimer();
    }
}