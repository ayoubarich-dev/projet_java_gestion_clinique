package fr.clinique.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe représentant un patient.
 * Hérite de la classe Personne.
 */
public class Patient extends Personne {
    private Date dateNaissance;
    private String telephone;
    private String numeroDossier;

    /**
     * Constructeur par défaut.
     */
    public Patient() {
        super();
    }

    /**
     * Constructeur avec les attributs de base.
     * @param nom Le nom du patient
     * @param prenom Le prénom du patient
     * @param dateNaissance La date de naissance du patient
     * @param telephone Le numéro de téléphone du patient
     */
    public Patient(String nom, String prenom, Date dateNaissance, String telephone) {
        super(nom, prenom);
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
    }

    /**
     * Constructeur complet avec ID.
     * @param id L'identifiant du patient
     * @param nom Le nom du patient
     * @param prenom Le prénom du patient
     * @param dateNaissance La date de naissance du patient
     * @param telephone Le numéro de téléphone du patient
     * @param numeroDossier Le numéro de dossier du patient
     */
    public Patient(int id, String nom, String prenom, Date dateNaissance, String telephone, String numeroDossier) {
        super(id, nom, prenom);
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.numeroDossier = numeroDossier;
    }

    // Getters et setters
    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNumeroDossier() {
        return numeroDossier;
    }

    public void setNumeroDossier(String numeroDossier) {
        this.numeroDossier = numeroDossier;
    }

    /**
     * Retourne une représentation textuelle du patient.
     * @return Le prénom et le nom du patient
     */
    @Override
    public String toString() {
        // Vérifier que les attributs ne sont pas null
        String prenomStr = (prenom != null) ? prenom : "";
        String nomStr = (nom != null) ? nom : "";
        return prenomStr + " " + nomStr;
    }

    @Override
    public boolean enregistrer() {
        Connection connection = DatabaseConnexion.getConnexion();
        try {
            if (this.id == 0) {
                // Insertion d'un nouveau patient
                String query = "INSERT INTO patients (nom, prenom, date_naissance, telephone, numero_dossier) VALUES (?, ?, ?, ?, ?)";

                // Générer un numéro de dossier unique si nécessaire
                if (this.numeroDossier == null || this.numeroDossier.isEmpty()) {
                    this.numeroDossier = "P" + System.currentTimeMillis();
                }

                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, this.nom);
                ps.setString(2, this.prenom);
                ps.setDate(3, new java.sql.Date(this.dateNaissance.getTime()));
                ps.setString(4, this.telephone);
                ps.setString(5, this.numeroDossier);

                int result = ps.executeUpdate();

                if (result > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        this.id = rs.getInt(1);
                    }
                    rs.close();
                }

                ps.close();
                return result > 0;
            } else {
                // Mise à jour d'un patient existant
                String query = "UPDATE patients SET nom = ?, prenom = ?, date_naissance = ?, telephone = ? WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, this.nom);
                ps.setString(2, this.prenom);
                ps.setDate(3, new java.sql.Date(this.dateNaissance.getTime()));
                ps.setString(4, this.telephone);
                ps.setInt(5, this.id);

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
        String query = "DELETE FROM patients WHERE id = ?";
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
    public List<Personne> afficherTous() {
        List<Personne> patients = new ArrayList<>();
        Connection connection = DatabaseConnexion.getConnexion();
        String query = "SELECT * FROM patients";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("numero_dossier")
                );
                patients.add(patient);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public Personne rechercherParId(int id) {
        Connection connection = DatabaseConnexion.getConnexion();
        String query = "SELECT * FROM patients WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Patient patient = new Patient(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("numero_dossier")
                );
                rs.close();
                ps.close();
                return patient;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Patient rechercherParNumeroDossier(String numeroDossier) {
        Connection connection = DatabaseConnexion.getConnexion();
        String query = "SELECT * FROM patients WHERE numero_dossier = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, numeroDossier);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Patient patient = new Patient(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("numero_dossier")
                );
                rs.close();
                ps.close();
                return patient;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Patient> getPatientsParMedecin(int idMedecin) {
        List<Patient> patients = new ArrayList<>();
        Connection connection = DatabaseConnexion.getConnexion();
        String query = "SELECT DISTINCT p.* FROM patients p " +
                "JOIN rendez_vous rv ON p.id = rv.id_patient " +
                "WHERE rv.id_medecin = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, idMedecin);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("numero_dossier")
                );
                patients.add(patient);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patients;
    }

    public static List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        Patient patient = new Patient();
        List<Personne> personnes = patient.afficherTous();

        for (Personne personne : personnes) {
            if (personne instanceof Patient) {
                patients.add((Patient) personne);
            }
        }

        return patients;
    }

    public static Patient getPatientById(int id) {
        Patient patient = new Patient();
        Personne personne = patient.rechercherParId(id);
        if (personne instanceof Patient) {
            return (Patient) personne;
        }
        return null;
    }

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
}