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
    /**
     * Obtient l'identifiant du rendez-vous.
     * @return L'identifiant
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant du rendez-vous.
     * @param id Le nouvel identifiant
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtient le patient concerné par le rendez-vous.
     * @return Le patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Définit le patient concerné par le rendez-vous.
     * @param patient Le nouveau patient
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Obtient le médecin concerné par le rendez-vous.
     * @return Le médecin
     */
    public Medecin getMedecin() {
        return medecin;
    }

    /**
     * Définit le médecin concerné par le rendez-vous.
     * @param medecin Le nouveau médecin
     */
    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }

    /**
     * Obtient la date du rendez-vous.
     * @return La date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Définit la date du rendez-vous.
     * @param date La nouvelle date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Obtient l'heure du rendez-vous.
     * @return L'heure
     */
    public String getHeure() {
        return heure;
    }

    /**
     * Définit l'heure du rendez-vous.
     * @param heure La nouvelle heure
     */
    public void setHeure(String heure) {
        this.heure = heure;
    }

    /**
     * Obtient le motif du rendez-vous.
     * @return Le motif
     */
    public String getMotif() {
        return motif;
    }

    /**
     * Définit le motif du rendez-vous.
     * @param motif Le nouveau motif
     */
    public void setMotif(String motif) {
        this.motif = motif;
    }

    /**
     * Retourne une représentation textuelle du rendez-vous.
     * @return La description du rendez-vous
     */
    @Override
    public String toString() {
        return "Rendez-vous le " + date + " à " + heure + " avec Dr. " + medecin.getNom() +
                " pour " + patient.getPrenom() + " " + patient.getNom();
    }

    // Méthodes pour le pattern Observer
    /**
     * Ajoute un observateur à la liste des observateurs.
     * @param o L'observateur à ajouter
     */
    public void ajouterObservateur(Observer o) {
        if (!observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    /**
     * Supprime un observateur de la liste des observateurs.
     * @param o L'observateur à supprimer
     */
    public void supprimerObservateur(Observer o) {
        observateurs.remove(o);
    }

    /**
     * Notifie tous les observateurs du rendez-vous.
     */
    public void notifierObservateurs() {
        for (Observer observateur : observateurs) {
            observateur.update(this);
        }
    }

    /**
     * Enregistre le rendez-vous dans la base de données.
     * @return true si l'enregistrement a réussi, false sinon
     */
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

                    // Notifier les observateurs pour les nouveaux rendez-vous
                    this.ajouterObservateur(this.medecin);
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

    /**
     * Supprime le rendez-vous de la base de données.
     * @return true si la suppression a réussi, false sinon
     */
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

    /**
     * Récupère tous les rendez-vous de la base de données.
     * @return Liste de tous les rendez-vous
     */
    @Override
    public List<RendezVous> afficherTous() {
        List<RendezVous> rendezVousList = new ArrayList<>();
        Connection connection = DatabaseConnexion.getConnexion();
        String query = "SELECT * FROM rendez_vous";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                // Récupérer le patient
                Patient patient = Patient.getPatientById(rs.getInt("id_patient"));

                // IMPORTANT: Corriger la récupération du médecin
                // id_medecin dans la table rendez_vous correspond à l'ID dans la table medecins
                // Récupérer le médecin directement par son ID
                Medecin medecin = null;
                int medecinId = rs.getInt("id_medecin");

                // Requête pour récupérer le médecin par son ID
                String medecinQuery = "SELECT m.*, u.* FROM medecins m " +
                        "JOIN utilisateurs u ON m.id_utilisateur = u.id " +
                        "WHERE m.id = ?";
                PreparedStatement psMedecin = connection.prepareStatement(medecinQuery);
                psMedecin.setInt(1, medecinId);
                ResultSet rsMedecin = psMedecin.executeQuery();

                if (rsMedecin.next()) {
                    medecin = new Medecin(
                            rsMedecin.getInt("u.id"),
                            rsMedecin.getString("u.nom"),
                            rsMedecin.getString("u.prenom"),
                            rsMedecin.getString("u.login"),
                            rsMedecin.getString("u.password"),
                            rsMedecin.getString("m.specialite"),
                            rsMedecin.getString("m.horaires")
                    );
                    // Définir l'ID du médecin (table medecins)
                    medecin.setId(medecinId);
                }
                rsMedecin.close();
                psMedecin.close();

                if (patient != null && medecin != null) {
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
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rendezVousList;
    }

    /**
     * Recherche un rendez-vous par son identifiant.
     * @param id L'identifiant du rendez-vous à rechercher
     * @return Le rendez-vous trouvé ou null si aucun rendez-vous ne correspond
     */
    @Override
    public RendezVous rechercherParId(int id) {
        Connection connection = DatabaseConnexion.getConnexion();
        String query = "SELECT * FROM rendez_vous WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Patient patient = (Patient) new Patient().rechercherParId(rs.getInt("id_patient"));
                Medecin medecin = Medecin.rechercherParIdUtilisateur(rs.getInt("id_medecin"));

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
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Récupère les rendez-vous d'un médecin.
     * @param idMedecin L'identifiant du médecin
     * @return Liste des rendez-vous du médecin
     */
    public static List<RendezVous> getRendezVousParMedecin(int idMedecin) {
        List<RendezVous> rendezVousList = new ArrayList<>();
        Connection connection = DatabaseConnexion.getConnexion();
        String query = "SELECT * FROM rendez_vous WHERE id_medecin = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idMedecin);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Récupérer le patient
                Patient patient = Patient.getPatientById(rs.getInt("id_patient"));

                // Récupérer le médecin
                Medecin medecin = null;
                int medecinId = rs.getInt("id_medecin");

                String medecinQuery = "SELECT m.*, u.* FROM medecins m " +
                        "JOIN utilisateurs u ON m.id_utilisateur = u.id " +
                        "WHERE m.id = ?";
                PreparedStatement psMedecin = connection.prepareStatement(medecinQuery);
                psMedecin.setInt(1, medecinId);
                ResultSet rsMedecin = psMedecin.executeQuery();

                if (rsMedecin.next()) {
                    medecin = new Medecin(
                            rsMedecin.getInt("u.id"),
                            rsMedecin.getString("u.nom"),
                            rsMedecin.getString("u.prenom"),
                            rsMedecin.getString("u.login"),
                            rsMedecin.getString("u.password"),
                            rsMedecin.getString("m.specialite"),
                            rsMedecin.getString("m.horaires")
                    );
                    medecin.setId(medecinId);
                }
                rsMedecin.close();
                psMedecin.close();

                if (patient != null && medecin != null) {
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
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
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
     * @param idMedecin L'identifiant du médecin
     * @param date La date du rendez-vous
     * @param heure L'heure du rendez-vous
     * @param motif Le motif du rendez-vous
     * @return true si l'ajout a réussi, false sinon
     */
    public static boolean ajouterRendezVous(int idPatient, int idMedecin, Date date, String heure, String motif) {
        Patient patient = Patient.getPatientById(idPatient);
        Medecin medecin = Medecin.getMedecinById(idMedecin);

        if (patient == null || medecin == null) {
            return false;
        }

        // Récupérer l'ID du médecin dans la table medecins (pas l'ID utilisateur)
        int medecinTableId = recupererIdMedecinDansTableMedecins(medecin.getId());

        Connection connection = DatabaseConnexion.getConnexion();
        try {
            String query = "INSERT INTO rendez_vous (id_patient, id_medecin, date, heure, motif) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idPatient);
            ps.setInt(2, medecinTableId); // Utiliser l'ID de la table medecins
            ps.setDate(3, new java.sql.Date(date.getTime()));
            ps.setString(4, heure);
            ps.setString(5, motif);

            int result = ps.executeUpdate();
            ps.close();

            if (result > 0) {
                // Notifier le médecin
                RendezVous rendezVous = new RendezVous(patient, medecin, date, heure, motif);
                rendezVous.ajouterObservateur(medecin);
                rendezVous.notifierObservateurs();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
        Patient patient = Patient.getPatientById(idPatient);
        Medecin medecin = Medecin.getMedecinById(idMedecin);

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

    private static int recupererIdMedecinDansTableMedecins(int idUtilisateur) {
        Connection connection = DatabaseConnexion.getConnexion();
        String query = "SELECT id FROM medecins WHERE id_utilisateur = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
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