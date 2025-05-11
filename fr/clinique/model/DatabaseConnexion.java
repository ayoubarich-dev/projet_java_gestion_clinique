package fr.clinique.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnexion {
    private static final String URL = "jdbc:mysql://localhost:3306/clinique_db"; // Remplacez par le bon nom de base
    private static final String USER = "root"; // Nom d'utilisateur MySQL
    private static final String PASSWORD = ""; // Mot de passe MySQL

    private static Connection connexion;

    /**
     * Récupère la connexion à la base de données.
     * @return Connection MySQL
     */
    public static Connection getConnexion() {
        if (connexion == null) {
            try {
                connexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion à MySQL établie !");
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            }
        }
        return connexion;
    }

    /**
     * Ferme la connexion à la base de données.
     */
    public static void fermerConnexion() {
        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
                System.out.println("Connexion à MySQL fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
