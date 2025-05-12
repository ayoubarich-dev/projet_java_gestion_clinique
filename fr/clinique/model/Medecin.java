package fr.clinique.model;

import fr.clinique.observer.Observer;
import fr.clinique.observer.NotificationManager;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe représentant un médecin.
 * Hérite de la classe Utilisateur et implémente l'interface Observer.
 */
public class Medecin extends Utilisateur implements Observer {
    private String specialite;
    private String horaires;

    // Map pour stocker les messages par médecin (clé = ID du médecin)
    private static Map<Integer, List<String>> messagesParMedecin = new HashMap<>();

    /**
     * Constructeur par défaut.
     */
    public Medecin() {
        super();
        setRole(Role.MEDECIN);
    }

    /**
     * Constructeur avec les attributs de base.
     * @param nom Le nom du médecin
     * @param prenom Le prénom du médecin
     * @param login Le login du médecin
     * @param password Le mot de passe du médecin
     * @param specialite La spécialité du médecin
     * @param horaires Les horaires de travail du médecin
     */
    public Medecin(String nom, String prenom, String login, String password, String specialite, String horaires) {
        super(nom, prenom, login, password, Role.MEDECIN);
        this.specialite = specialite;
        this.horaires = horaires;
    }

    /**
     * Constructeur complet avec ID.
     * @param id L'identifiant du médecin
     * @param nom Le nom du médecin
     * @param prenom Le prénom du médecin
     * @param login Le login du médecin
     * @param password Le mot de passe du médecin
     * @param specialite La spécialité du médecin
     * @param horaires Les horaires de travail du médecin
     */
    public Medecin(int id, String nom, String prenom, String login, String password, String specialite, String horaires) {
        super(id, nom, prenom, login, password, Role.MEDECIN);
        this.specialite = specialite;
        this.horaires = horaires;
    }

    // Getters et setters
    /**
     * Obtient la spécialité du médecin.
     * @return La spécialité
     */
    public String getSpecialite() {
        return specialite;
    }

    /**
     * Définit la spécialité du médecin.
     * @param specialite La nouvelle spécialité
     */
    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    /**
     * Obtient les horaires du médecin.
     * @return Les horaires
     */
    public String getHoraires() {
        return horaires;
    }

    /**
     * Définit les horaires du médecin.
     * @param horaires Les nouveaux horaires
     */
    public void setHoraires(String horaires) {
        this.horaires = horaires;
    }

    /**
     * Retourne une représentation textuelle du médecin.
     * @return Le titre, prénom, nom et spécialité du médecin
     */
    @Override
    public String toString() {
        return "Dr. " + prenom + " " + nom + " (" + specialite + ")";
    }

    /**
     * Méthode appelée lorsqu'un rendez-vous est créé ou modifié.
     * Implémentation de l'interface Observer.
     * @param rendezVous Le rendez-vous concerné
     */
    @Override
    public void update(RendezVous rendezVous) {
        if (rendezVous.getMedecin().getId() == this.id) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String message = "Nouveau patient affecté: " +
                    rendezVous.getPatient().getPrenom() + " " +
                    rendezVous.getPatient().getNom() +
                    " le " + sdf.format(rendezVous.getDate()) +
                    " à " + rendezVous.getHeure();

            // Stocker le message pour ce médecin
            ajouterMessagePourMedecin(this.id, message);

            // Utiliser le gestionnaire de notifications pour l'UI
            NotificationManager.getInstance().ajouterNotification(
                    "Notification au Dr. " + nom + " " + prenom + " : " + message
            );
        }
    }

    /**
     * Enregistre le médecin dans la base de données.
     * @return true si l'enregistrement a réussi, false sinon
     */
    public boolean enregistrer() {
        System.out.println("Début de la méthode enregistrer() pour le médecin");

        // D'abord enregistrer les informations de l'utilisateur
        boolean userSaved = super.enregistrer();

        if (!userSaved) {
            System.err.println("Échec de l'enregistrement de l'utilisateur du médecin");
            return false;
        }

        // Ensuite enregistrer les informations du médecin
        Connection connection = DatabaseConnexion.getConnexion();
        try {
            // Vérifier si le médecin existe déjà
            String checkQuery = "SELECT id FROM medecins WHERE id_utilisateur = ?";
            PreparedStatement checkPs = connection.prepareStatement(checkQuery);
            checkPs.setInt(1, getId());
            ResultSet rs = checkPs.executeQuery();

            boolean result;
            if (rs.next()) {
                // Médecin existe, faire une mise à jour
                System.out.println("Mise à jour du médecin existant avec id_utilisateur=" + getId());
                String updateQuery = "UPDATE medecins SET specialite = ?, horaires = ? WHERE id_utilisateur = ?";
                PreparedStatement updatePs = connection.prepareStatement(updateQuery);
                updatePs.setString(1, specialite);
                updatePs.setString(2, horaires);
                updatePs.setInt(3, getId());

                result = updatePs.executeUpdate() > 0;
                updatePs.close();
            } else {
                // Nouveau médecin, faire une insertion
                System.out.println("Insertion d'un nouveau médecin avec id_utilisateur=" + getId());
                String insertQuery = "INSERT INTO medecins (id_utilisateur, specialite, horaires) VALUES (?, ?, ?)";
                PreparedStatement insertPs = connection.prepareStatement(insertQuery);
                insertPs.setInt(1, getId());
                insertPs.setString(2, specialite);
                insertPs.setString(3, horaires);

                result = insertPs.executeUpdate() > 0;
                insertPs.close();
            }

            rs.close();
            checkPs.close();

            if (result) {
                System.out.println("Médecin enregistré avec succès");
            } else {
                System.err.println("Échec de l'enregistrement du médecin dans la table medecins");
            }

            return result;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'enregistrement du médecin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime le médecin de la base de données.
     * @return true si la suppression a réussi, false sinon
     */
    @Override
    public boolean supprimer() {
        // D'abord supprimer les informations du médecin
        Connection connection = DatabaseConnexion.getConnexion();
        try {
            String query = "DELETE FROM medecins WHERE id_utilisateur = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Ensuite supprimer l'utilisateur
        return super.supprimer();
    }

    /**
     * Recherche un médecin par l'identifiant de son utilisateur.
     * @param idUtilisateur L'identifiant de l'utilisateur
     * @return Le médecin trouvé ou null si aucun médecin ne correspond
     */
    public static Medecin rechercherParIdUtilisateur(int idUtilisateur) {
        Connection connection = DatabaseConnexion.getConnexion();

        try {
            // D'abord récupérer les infos utilisateur
            String userQuery = "SELECT * FROM utilisateurs WHERE id = ?";
            PreparedStatement userPs = connection.prepareStatement(userQuery);
            userPs.setInt(1, idUtilisateur);
            ResultSet userRs = userPs.executeQuery();

            if (userRs.next()) {
                // Ensuite récupérer les infos médecin
                String medecinQuery = "SELECT * FROM medecins WHERE id_utilisateur = ?";
                PreparedStatement medecinPs = connection.prepareStatement(medecinQuery);
                medecinPs.setInt(1, idUtilisateur);
                ResultSet medecinRs = medecinPs.executeQuery();

                if (medecinRs.next()) {
                    Medecin medecin = new Medecin(
                            userRs.getInt("id"),
                            userRs.getString("nom"),
                            userRs.getString("prenom"),
                            userRs.getString("login"),
                            userRs.getString("password"),
                            medecinRs.getString("specialite"),
                            medecinRs.getString("horaires")
                    );

                    medecinRs.close();
                    medecinPs.close();
                    userRs.close();
                    userPs.close();

                    return medecin;
                }

                medecinPs.close();
            }

            userRs.close();
            userPs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Récupère tous les médecins de la base de données.
     * @return Liste de tous les médecins
     */
    public static List<Medecin> getAllMedecins() {
        List<Medecin> medecins = new ArrayList<>();
        Connection connection = DatabaseConnexion.getConnexion();

        try {
            String query = "SELECT u.*, m.specialite, m.horaires FROM utilisateurs u " +
                    "JOIN medecins m ON u.id = m.id_utilisateur " +
                    "WHERE u.role = 'MEDECIN'";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Medecin medecin = new Medecin(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("specialite"),
                        rs.getString("horaires")
                );

                medecins.add(medecin);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medecins;
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
     * Ajoute un message pour un médecin.
     * @param idMedecin L'identifiant du médecin
     * @param message Le message à ajouter
     */
    public static void ajouterMessagePourMedecin(int idMedecin, String message) {
        if (!messagesParMedecin.containsKey(idMedecin)) {
            messagesParMedecin.put(idMedecin, new ArrayList<>());
        }
        messagesParMedecin.get(idMedecin).add(message);
    }

    /**
     * Récupère les messages d'un médecin.
     * @param idMedecin L'identifiant du médecin
     * @return Liste des messages du médecin
     */
    public static List<String> getMessagesPourMedecin(int idMedecin) {
        if (messagesParMedecin.containsKey(idMedecin)) {
            return new ArrayList<>(messagesParMedecin.get(idMedecin));
        }
        return new ArrayList<>();
    }

    /**
     * Efface les messages d'un médecin.
     * @param idMedecin L'identifiant du médecin
     */
    public static void clearMessagesPourMedecin(int idMedecin) {
        if (messagesParMedecin.containsKey(idMedecin)) {
            messagesParMedecin.get(idMedecin).clear();
        }
    }
}