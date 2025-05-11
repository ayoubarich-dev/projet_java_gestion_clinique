package fr.clinique.view;

import fr.clinique.controller.ControleurAuthentification;
import fr.clinique.model.Utilisateur;

import javax.swing.*;
import java.awt.*;

public class VueAuthentification extends JFrame {
    private JTextField tfLogin;
    private JPasswordField pfPassword;
    private JButton btnConnexion;

    public VueAuthentification() {
        initUI();
    }

    private void initUI() {
        setTitle("Connexion - Clinique Médicale");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Titre
        JLabel lblTitre = new JLabel("Connexion à l'application");
        lblTitre.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(lblTitre, gbc);

        // Login
        JLabel lblLogin = new JLabel("Identifiant:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblLogin, gbc);

        tfLogin = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(tfLogin, gbc);

        // Password
        JLabel lblPassword = new JLabel("Mot de passe:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblPassword, gbc);

        pfPassword = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(pfPassword, gbc);

        // Bouton de connexion
        btnConnexion = new JButton("Se connecter");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnConnexion, gbc);

        add(panel);

        // Créer une méthode publique pour faciliter le test de l'interface
        // Si on ne crée pas le contrôleur dans Main, on peut le créer ici
        // new ControleurAuthentification(this);

        setVisible(true);
    }

    // Getters pour les composants d'interface utilisateur
    public JTextField getTfLogin() {
        return tfLogin;
    }

    public JPasswordField getPfPassword() {
        return pfPassword;
    }

    public JButton getBtnConnexion() {
        return btnConnexion;
    }

    // Méthode pour afficher un message d'erreur
    public void afficherErreur(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
    }

    // Méthode pour ouvrir la vue principale et fermer la vue d'authentification
    public void ouvrirVuePrincipale(Utilisateur utilisateur) {
        dispose(); // Fermer la fenêtre de connexion
        new VuePrincipale(utilisateur);
    }

    // Méthode pour tester l'authentification directement (utile pour le débogage)
    public void testerConnexion() {
        tfLogin.setText("admin");
        pfPassword.setText("admin");
        btnConnexion.doClick();
    }
}