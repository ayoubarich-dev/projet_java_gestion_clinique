package fr.clinique.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnexion {
    private static final String URL = "jdbc:mysql://localhost:3306/clinique_db"; // Remplacez par le bon nom de base
    private static final String USER = "root"; // Nom d'utilisateur MySQL
    private static final String PASSWORD = ""; // Mot de passe MySQL
    private static final int MAX_TIMEOUT = 3; // Timeout en secondes pour vérifier si la connexion est valide

    private static Connection connexion;

    /**
     * Récupère la connexion à la base de données.
     * Vérifie si la connexion existante est valide, sinon en crée une nouvelle.
     * @return Connection MySQL
     */
    public static Connection getConnexion() {
        try {
            if (connexion == null || connexion.isClosed() || !isConnectionValid()) {
                try {
                    // Si le driver n'est pas chargé, le charger explicitement
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    // Créer une nouvelle connexion
                    connexion = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Nouvelle connexion à MySQL établie !");
                } catch (ClassNotFoundException e) {
                    System.err.println("Erreur de chargement du driver MySQL: " + e.getMessage());
                    throw new SQLException("Impossible de charger le driver MySQL", e);
                } catch (SQLException e) {
                    System.err.println("Erreur de création de connexion à la base de données : " + e.getMessage());
                    throw e;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la connexion : " + e.getMessage());
            e.printStackTrace();
        }

        return connexion;
    }


    private static boolean isConnectionValid() {
        try {
            if (connexion == null) {
                return false;
            }

            // Vérifier si la connexion est valide avec un timeout
            return connexion.isValid(MAX_TIMEOUT);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la validité de la connexion : " + e.getMessage());
            return false;
        }
    }

    /**
     * Ferme la connexion à la base de données.
     */
    public static void fermerConnexion() {
        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
                connexion = null; // Réinitialiser la connexion
                System.out.println("Connexion à MySQL fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }


}