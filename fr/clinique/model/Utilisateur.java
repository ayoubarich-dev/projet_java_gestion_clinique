package fr.clinique.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un utilisateur du système.
 * Hérite de la classe Personne.
 */
public class Utilisateur extends Personne {
    private String login;
    private String password;
    private Role role;

    /**
     * Constructeur par défaut.
     */
    public Utilisateur() {
        super();
    }

    /**
     * Constructeur avec login, password et role.
     * @param login Le login de l'utilisateur
     * @param password Le mot de passe de l'utilisateur
     * @param role Le rôle de l'utilisateur
     */
    public Utilisateur(String login, String password, Role role) {
        super();
        this.login = login;
        this.password = password;
        this.role = role;
    }

    /**
     * Constructeur avec nom, prénom, login, password et role.
     * @param nom Le nom de l'utilisateur
     * @param prenom Le prénom de l'utilisateur
     * @param login Le login de l'utilisateur
     * @param password Le mot de passe de l'utilisateur
     * @param role Le rôle de l'utilisateur
     */
    public Utilisateur(String nom, String prenom, String login, String password, Role role) {
        super(nom, prenom);
        this.login = login;
        this.password = password;
        this.role = role;
    }

    /**
     * Constructeur complet avec ID.
     * @param id L'identifiant de l'utilisateur
     * @param nom Le nom de l'utilisateur
     * @param prenom Le prénom de l'utilisateur
     * @param login Le login de l'utilisateur
     * @param password Le mot de passe de l'utilisateur
     * @param role Le rôle de l'utilisateur
     */
    public Utilisateur(int id, String nom, String prenom, String login, String password, Role role) {
        super(id, nom, prenom);
        this.login = login;
        this.password = password;
        this.role = role;
    }

    // Getters et setters
    /**
     * Obtient le login de l'utilisateur.
     * @return Le login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Définit le login de l'utilisateur.
     * @param login Le nouveau login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Obtient le mot de passe de l'utilisateur.
     * @return Le mot de passe
     */
    public String getPassword() {
        return password;
    }

    /**
     * Définit le mot de passe de l'utilisateur.
     * @param password Le nouveau mot de passe
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtient le rôle de l'utilisateur.
     * @return Le rôle
     */
    public Role getRole() {
        return role;
    }

    /**
     * Définit le rôle de l'utilisateur.
     * @param role Le nouveau rôle
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Enregistre l'utilisateur dans la base de données.
     * @return true si l'enregistrement a réussi, false sinon
     */
    @Override
    public boolean enregistrer() {
        Connection connection = DatabaseConnexion.getConnexion();
        try {
            if (this.id == 0) {
                // Insertion d'un nouvel utilisateur
                String query = "INSERT INTO utilisateurs (nom, prenom, login, password, role) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, this.nom);
                ps.setString(2, this.prenom);
                ps.setString(3, this.login);
                ps.setString(4, this.password);
                ps.setString(5, this.role.toString());

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
                // Mise à jour d'un utilisateur existant
                String query = "UPDATE utilisateurs SET nom = ?, prenom = ?, login = ?, password = ?, role = ? WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, this.nom);
                ps.setString(2, this.prenom);
                ps.setString(3, this.login);
                ps.setString(4, this.password);
                ps.setString(5, this.role.toString());
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
     * Supprime l'utilisateur de la base de données.
     * @return true si la suppression a réussi, false sinon
     */
    @Override
    public boolean supprimer() {
        if (this.id == 0) return false;

        Connection connection = DatabaseConnexion.getConnexion();
        String query = "DELETE FROM utilisateurs WHERE id = ?";
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
     * Récupère tous les utilisateurs de la base de données.
     * @return Liste de tous les utilisateurs
     */
    @Override
    public List<Personne> afficherTous() {
        List<Personne> utilisateurs = new ArrayList<>();
        Connection connection = DatabaseConnexion.getConnexion();
        String query = "SELECT * FROM utilisateurs";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("login"),
                    rs.getString("password"),
                    Role.valueOf(rs.getString("role"))
                );
                utilisateurs.add(utilisateur);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateurs;
    }

    /**
     * Recherche un utilisateur par son identifiant.
     * @param id L'identifiant de l'utilisateur à rechercher
     * @return L'utilisateur trouvé ou null si aucun utilisateur ne correspond
     */
    @Override
    public Personne rechercherParId(int id) {
        Connection connection = DatabaseConnexion.getConnexion();
        String query = "SELECT * FROM utilisateurs WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Utilisateur utilisateur = new Utilisateur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("login"),
                    rs.getString("password"),
                    Role.valueOf(rs.getString("role"))
                );
                rs.close();
                ps.close();
                return utilisateur;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Authentifie un utilisateur avec son login et son mot de passe.
     * @param login Le login de l'utilisateur
     * @param password Le mot de passe de l'utilisateur
     * @return L'utilisateur authentifié ou null si l'authentification échoue
     */
    public static Utilisateur authentifier(String login, String password) {
        Connection connection = DatabaseConnexion.getConnexion();
        String query = "SELECT * FROM utilisateurs WHERE login = ? AND password = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, login);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Selon le rôle, retourner le bon type d'Utilisateur
                Role role = Role.valueOf(rs.getString("role"));
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String loginDb = rs.getString("login");
                String passwordDb = rs.getString("password");

                Utilisateur utilisateur;

                switch (role) {
                    case MEDECIN:
                        // Chercher les informations du médecin
                        Medecin medecin = Medecin.rechercherParIdUtilisateur(id);
                        if (medecin != null) {
                            return medecin;
                        } else {
                            // Si pas de médecin correspondant, créer un utilisateur médecin de base
                            utilisateur = new Medecin();
                            utilisateur.setId(id);
                            utilisateur.setNom(nom);
                            utilisateur.setPrenom(prenom);
                            utilisateur.setLogin(loginDb);
                            utilisateur.setPassword(passwordDb);
                            utilisateur.setRole(role);
                            return utilisateur;
                        }
                    case SECRETAIRE:
                        utilisateur = new Secretaire();
                        utilisateur.setId(id);
                        utilisateur.setNom(nom);
                        utilisateur.setPrenom(prenom);
                        utilisateur.setLogin(loginDb);
                        utilisateur.setPassword(passwordDb);
                        utilisateur.setRole(role);
                        return utilisateur;
                    default:
                        utilisateur = new Utilisateur();
                        utilisateur.setId(id);
                        utilisateur.setNom(nom);
                        utilisateur.setPrenom(prenom);
                        utilisateur.setLogin(loginDb);
                        utilisateur.setPassword(passwordDb);
                        utilisateur.setRole(role);
                        return utilisateur;
                }
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recherche des utilisateurs par leur rôle.
     * @param role Le rôle des utilisateurs à rechercher
     * @return Liste des utilisateurs ayant le rôle spécifié
     */
    public static List<Utilisateur> rechercherParRole(Role role) {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        Connection connection = DatabaseConnexion.getConnexion();
        String query = "SELECT * FROM utilisateurs WHERE role = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, role.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Utilisateur utilisateur = null;

                switch (role) {
                    case MEDECIN:
                        utilisateur = new Medecin();
                        break;
                    case SECRETAIRE:
                        utilisateur = new Secretaire();
                        break;
                    default:
                        utilisateur = new Utilisateur();
                }

                utilisateur.setId(rs.getInt("id"));
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setPrenom(rs.getString("prenom"));
                utilisateur.setLogin(rs.getString("login"));
                utilisateur.setPassword(rs.getString("password"));
                utilisateur.setRole(Role.valueOf(rs.getString("role")));

                utilisateurs.add(utilisateur);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return utilisateurs;
    }
}