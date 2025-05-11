package fr.clinique.model;

import fr.clinique.util.ExcelExporter;
import fr.clinique.util.PDFExporter;

import java.util.Date;
import java.util.List;

/**
 * Classe façade pour les opérations sur les rendez-vous.
 */
public class RendezVousModel {
    /**
     * Récupère tous les rendez-vous.
     * @return Liste de tous les rendez-vous
     */
    public static List<RendezVous> getTousRendezVous() {
        RendezVous rendezVous = new RendezVous();
        return rendezVous.afficherTous();
    }

    /**
     * Récupère les rendez-vous d'un médecin.
     * @param idMedecin L'identifiant du médecin
     * @return Liste des rendez-vous du médecin
     */
    public static List<RendezVous> getRendezVousParMedecin(int idMedecin) {
        return RendezVous.afficherParMedecin(idMedecin);
    }

    /**
     * Recherche un rendez-vous par son identifiant.
     * @param id L'identifiant du rendez-vous
     * @return Le rendez-vous trouvé ou null si aucun rendez-vous ne correspond
     */
    public static RendezVous getRendezVousById(int id) {
        RendezVous rendezVous = new RendezVous();
        return rendezVous.rechercherParId(id);
    }

    /**
     * Ajoute un nouveau rendez-vous.
     * @param idPatient L'identifiant du patient
     * @param idMedecin L'identifiant du médecin
     * @param date La date du rendez-vous
     * @param heure L'heure du rendez-vous
     * @param motif Le motif du rendez-vous
     * @return true si l'ajout a réussi, false sinon
     */
    public static boolean ajouterRendezVous(int idPatient, int idMedecin, Date date, String heure, String motif) {
        Patient patient = PatientModel.getPatientById(idPatient);
        Medecin medecin = MedecinModel.getMedecinById(idMedecin);

        if (patient == null || medecin == null) {
            return false;
        }

        RendezVous rendezVous = new RendezVous(patient, medecin, date, heure, motif);
        // La notification du médecin est gérée dans la méthode enregistrer
        return rendezVous.enregistrer();
    }

    /**
     * Modifie un rendez-vous existant.
     * @param id L'identifiant du rendez-vous
     * @param idPatient L'identifiant du patient
     * @param idMedecin L'identifiant du médecin
     * @param date La nouvelle date du rendez-vous
     * @param heure La nouvelle heure du rendez-vous
     * @param motif Le nouveau motif du rendez-vous
     * @return true si la modification a réussi, false sinon
     */
    public static boolean modifierRendezVous(int id, int idPatient, int idMedecin, Date date, String heure, String motif) {
        Patient patient = PatientModel.getPatientById(idPatient);
        Medecin medecin = MedecinModel.getMedecinById(idMedecin);

        if (patient == null || medecin == null) {
            return false;
        }

        RendezVous rendezVous = new RendezVous(id, patient, medecin, date, heure, motif);
        return rendezVous.enregistrer();
    }

    /**
     * Supprime un rendez-vous.
     * @param id L'identifiant du rendez-vous
     * @return true si la suppression a réussi, false sinon
     */
    public static boolean supprimerRendezVous(int id) {
        RendezVous rendezVous = getRendezVousById(id);
        if (rendezVous == null) {
            return false;
        }

        return rendezVous.supprimer();
    }

    /**
     * Exporte les rendez-vous au format Excel.
     * @param rendezVous La liste des rendez-vous à exporter
     * @param cheminFichier Le chemin du fichier de destination
     * @return true si l'export a réussi, false sinon
     */
    public static boolean exporterExcel(List<RendezVous> rendezVous, String cheminFichier) {
        ExcelExporter exporter = new ExcelExporter();
        return exporter.exporter(rendezVous, cheminFichier);
    }

    /**
     * Exporte les rendez-vous au format PDF.
     * @param rendezVous La liste des rendez-vous à exporter
     * @param cheminFichier Le chemin du fichier de destination
     * @return true si l'export a réussi, false sinon
     */
    public static boolean exporterPDF(List<RendezVous> rendezVous, String cheminFichier) {
        PDFExporter exporter = new PDFExporter();
        return exporter.exporter(rendezVous, cheminFichier);
    }
}