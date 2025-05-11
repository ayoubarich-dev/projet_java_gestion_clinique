package fr.clinique;

import fr.clinique.controller.ControleurAuthentification;
import fr.clinique.model.DatabaseConnexion;
import fr.clinique.view.VueAuthentification;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // Vérifier la connexion à la base de données d'abord
        try {
            Connection conn = DatabaseConnexion.getConnexion();
            if (conn == null) {
                JOptionPane.showMessageDialog(null,
                        "Impossible de se connecter à la base de données.",
                        "Erreur de connexion",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            System.out.println("Connexion à la base de données établie avec succès.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la connexion à la base de données: " + e.getMessage(),
                    "Erreur de connexion",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // S'assurer que l'interface utilisateur s'exécute dans l'EDT (Event Dispatch Thread)
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                VueAuthentification vueAuth = new VueAuthentification();
                // Créer le contrôleur avec la vue
                new ControleurAuthentification(vueAuth);
            }
        });
    }
}