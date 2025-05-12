package fr.clinique.model;

import fr.clinique.observer.Observer;
import fr.clinique.util.ExcelExporter;
import fr.clinique.util.PDFExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe représentant un rendez-vous.
 * Implémente l'interface Model.
 */
public class RendezVous implements Model<RendezVous> {
    private int id;
    private Patient patient;
    private Medecin medecin;
    private Date date;
    private String heure;
    private String motif;

    private List<Observer> observateurs = new ArrayList<>();

    /**
     * Constructeur par défaut.
     */
    public RendezVous() {
        // Constructeur par défaut
    }

    /**
     * Constructeur avec les attributs de base.
     * @param patient Le patient concerné
     * @param medecin Le médecin concerné
     * @param date La date du rendez-vous
     * @param heure L'heure du rendez-vous
     * @param motif Le motif du rendez-vous
     */
    public RendezVous(Patient patient, Medecin medecin, Date date, String heure, String motif) {
        this.patient = patient;
        this.medecin = medecin;
        this.date = date;
        this.heure = heure;
        this.motif = motif;
    }

    /**
     * Constructeur complet avec ID.
     * @param id L'identifiant du rendez-vous
     * @param patient Le patient concerné
     * @param medecin Le médecin concerné
     * @param date La date du rendez-vous
     * @param heure L'heure du rendez-vous
     * @param motif Le motif du rendez-vous
     */
    public RendezVous(int id, Patient patient, Medecin medecin, Date date, String heure, String motif) {
        this.id = id;
        this.patient = patient;
        this.medecin = medecin;
        this.date = date;
        this.heure = heure;
        this.motif = motif;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Medecin getMedecin() {
        return medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    @Override
    public String toString() {
        return "Rendez-vous le " + date + " à " + heure + " avec Dr. " + medecin.getNom() +
                " pour " + patient.getPrenom() + " " + patient.getNom();
    }

    // Méthodes pour le pattern Observer
    public void ajouterObservateur(Observer o) {
        if (!observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    public void supprimerObservateur(Observer o) {
        observateurs.remove(o);
    }

    public void notifierObservateurs() {
        for (Observer observateur : observateurs) {
            observateur.update(this);
        }
    }


    // Dans la classe RendezVous.java - méthode enregistrer()
    @Override
    public boolean enregistrer() {
        Connection connection = DatabaseConnexion.getConnexion();
        try {
            if (this.id == 0) {
                // Insertion d'un nouveau rendez-vous
                String query = "INSERT INTO rendez_vous (id_patient, id_medecin, date, heure, motif) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, this.patient.getId());
                ps.setInt(2, this.medecin.getId());
                ps.setDate(3, new java.sql.Date(this.date.getTime()));
                ps.setString(4, this.heure);
                ps.setString(5, this.motif);

                int result = ps.executeUpdate();

                if (result > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        this.id = rs.getInt(1);
                    }
                    rs.close();

                    // CORRECTION: Vérifier si le médecin n'est pas déjà observateur avant de l'ajouter
                    if (!observateurs.contains(this.medecin)) {
                        this.ajouterObservateur(this.medecin);
                    }
                    this.notifierObservateurs();
                }

                ps.close();
                return result > 0;
            } else {
                // Mise à jour d'un rendez-vous existant
                String query = "UPDATE rendez_vous SET id_patient = ?, id_medecin = ?, date = ?, heure = ?, motif = ? WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, this.patient.getId());
                ps.setInt(2, this.medecin.getId());
                ps.setDate(3, new java.sql.Date(this.date.getTime()));
                ps.setString(4, this.heure);
                ps.setString(5, this.motif);
                ps.setInt(6, this.id);

                int result = ps.executeUpdate();
                ps.close();
                return result > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supprimer() {
        if (this.id == 0) return false;

        Connection connection = DatabaseConnexion.getConnexion();
        String query = "DELETE FROM rendez_vous WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, this.id);

            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<RendezVous> afficherTous() {
        List<RendezVous> rendezVousList = new ArrayList<>();
        Connection connection = DatabaseConnexion.getConnexion();

        String query = "SELECT rv.*, " +
                "p.nom as patient_nom, p.prenom as patient_prenom, p.date_naissance, p.telephone, p.numero_dossier, " +
                "u.id as medecin_id, u.nom as medecin_nom, u.prenom as medecin_prenom, u.login, u.password, " +
                "m.specialite, m.horaires " +
                "FROM rendez_vous rv " +
                "JOIN patients p ON rv.id_patient = p.id " +
                "JOIN utilisateurs u ON rv.id_medecin = u.id " +
                "JOIN medecins m ON u.id = m.id_utilisateur " +
                "WHERE u.role = 'MEDECIN'";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                // Créer le patient
                Patient patient = new Patient(
                        rs.getInt("id_patient"),
                        rs.getString("patient_nom"),
                        rs.getString("patient_prenom"),
                        rs.getDate("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("numero_dossier")
                );

                // Créer le médecin
                Medecin medecin = new Medecin(
                        rs.getInt("medecin_id"),
                        rs.getString("medecin_nom"),
                        rs.getString("medecin_prenom"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("specialite"),
                        rs.getString("horaires")
                );

                // Créer le rendez-vous
                RendezVous rendezVous = new RendezVous(
                        rs.getInt("id"),
                        patient,
                        medecin,
                        rs.getDate("date"),
                        rs.getString("heure"),
                        rs.getString("motif")
                );

                rendezVousList.add(rendezVous);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des rendez-vous: " + e.getMessage());
            e.printStackTrace();
        }

        return rendezVousList;
    }

    @Override
    public RendezVous rechercherParId(int id) {
        Connection connection = DatabaseConnexion.getConnexion();

        String query = "SELECT rv.*, " +
                "p.nom as patient_nom, p.prenom as patient_prenom, p.date_naissance, p.telephone, p.numero_dossier, " +
                "u.id as medecin_id, u.nom as medecin_nom, u.prenom as medecin_prenom, u.login, u.password, " +
                "m.specialite, m.horaires " +
                "FROM rendez_vous rv " +
                "JOIN patients p ON rv.id_patient = p.id " +
                "JOIN utilisateurs u ON rv.id_medecin = u.id " +
                "JOIN medecins m ON u.id = m.id_utilisateur " +
                "WHERE rv.id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Créer le patient
                Patient patient = new Patient(
                        rs.getInt("id_patient"),
                        rs.getString("patient_nom"),
                        rs.getString("patient_prenom"),
                        rs.getDate("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("numero_dossier")
                );

                // Créer le médecin
                Medecin medecin = new Medecin(
                        rs.getInt("medecin_id"),
                        rs.getString("medecin_nom"),
                        rs.getString("medecin_prenom"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("specialite"),
                        rs.getString("horaires")
                );

                // Créer le rendez-vous
                RendezVous rendezVous = new RendezVous(
                        rs.getInt("id"),
                        patient,
                        medecin,
                        rs.getDate("date"),
                        rs.getString("heure"),
                        rs.getString("motif")
                );

                rs.close();
                ps.close();
                return rendezVous;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du rendez-vous: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Récupère les rendez-vous d'un médecin.
     * @param idMedecin L'identifiant du médecin (ID utilisateur)
     * @return Liste des rendez-vous du médecin
     */
    public static List<RendezVous> getRendezVousParMedecin(int idMedecin) {
        List<RendezVous> rendezVousList = new ArrayList<>();
        Connection connection = DatabaseConnexion.getConnexion();

        String query = "SELECT rv.*, " +
                "p.nom as patient_nom, p.prenom as patient_prenom, p.date_naissance, p.telephone, p.numero_dossier, " +
                "u.id as medecin_id, u.nom as medecin_nom, u.prenom as medecin_prenom, u.login, u.password, " +
                "m.specialite, m.horaires " +
                "FROM rendez_vous rv " +
                "JOIN patients p ON rv.id_patient = p.id " +
                "JOIN utilisateurs u ON rv.id_medecin = u.id " +
                "JOIN medecins m ON u.id = m.id_utilisateur " +
                "WHERE rv.id_medecin = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idMedecin);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Créer le patient
                Patient patient = new Patient(
                        rs.getInt("id_patient"),
                        rs.getString("patient_nom"),
                        rs.getString("patient_prenom"),
                        rs.getDate("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("numero_dossier")
                );

                // Créer le médecin
                Medecin medecin = new Medecin(
                        rs.getInt("medecin_id"),
                        rs.getString("medecin_nom"),
                        rs.getString("medecin_prenom"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("specialite"),
                        rs.getString("horaires")
                );

                // Créer le rendez-vous
                RendezVous rendezVous = new RendezVous(
                        rs.getInt("id"),
                        patient,
                        medecin,
                        rs.getDate("date"),
                        rs.getString("heure"),
                        rs.getString("motif")
                );

                rendezVousList.add(rendezVous);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des rendez-vous par médecin: " + e.getMessage());
            e.printStackTrace();
        }

        return rendezVousList;
    }

    /**
     * Récupère tous les rendez-vous.
     * @return Liste de tous les rendez-vous
     */
    public static List<RendezVous> getTousRendezVous() {
        RendezVous rendezVous = new RendezVous();
        return rendezVous.afficherTous();
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
     * @param idMedecin L'identifiant du médecin (ID utilisateur)
     * @param date La date du rendez-vous
     * @param heure L'heure du rendez-vous
     * @param motif Le motif du rendez-vous
     * @return true si l'ajout a réussi, false sinon
     */
    public static boolean ajouterRendezVous(int idPatient, int idMedecin, Date date, String heure, String motif) {
        System.out.println("=== RendezVous.ajouterRendezVous ===");
        System.out.println("ID Patient: " + idPatient);
        System.out.println("ID Médecin (utilisateur): " + idMedecin);

        Patient patient = Patient.getPatientById(idPatient);
        Medecin medecin = Medecin.getMedecinById(idMedecin);

        if (patient == null) {
            System.err.println("Patient non trouvé avec l'ID: " + idPatient);
            return false;
        }

        if (medecin == null) {
            System.err.println("Médecin non trouvé avec l'ID utilisateur: " + idMedecin);
            return false;
        }

        System.out.println("Patient trouvé: " + patient.getNom() + " " + patient.getPrenom());
        System.out.println("Médecin trouvé: " + medecin.getNom() + " " + medecin.getPrenom());

        // Créer le rendez-vous
        RendezVous rendezVous = new RendezVous(patient, medecin, date, heure, motif);

        // Enregistrer (la notification sera gérée dans la méthode enregistrer)
        return rendezVous.enregistrer();
    }

    /**
     * Modifie un rendez-vous existant.
     * @param id L'identifiant du rendez-vous
     * @param idPatient L'identifiant du patient
     * @param idMedecin L'identifiant du médecin (ID utilisateur)
     * @param date La nouvelle date du rendez-vous
     * @param heure La nouvelle heure du rendez-vous
     * @param motif Le nouveau motif du rendez-vous
     * @return true si la modification a réussi, false sinon
     */
    public static boolean modifierRendezVous(int id, int idPatient, int idMedecin, Date date, String heure, String motif) {
        Patient patient = Patient.getPatientById(idPatient);
        Medecin medecin = Medecin.getMedecinById(idMedecin);

        if (patient == null || medecin == null) {
            System.err.println("Patient ou médecin non trouvé pour la modification");
            return false;
        }

        Connection connection = DatabaseConnexion.getConnexion();
        try {
            String query = "UPDATE rendez_vous SET id_patient = ?, id_medecin = ?, date = ?, heure = ?, motif = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idPatient);
            ps.setInt(2, idMedecin); // Utiliser directement l'ID utilisateur du médecin
            ps.setDate(3, new java.sql.Date(date.getTime()));
            ps.setString(4, heure);
            ps.setString(5, motif);
            ps.setInt(6, id);

            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la modification du rendez-vous: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
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