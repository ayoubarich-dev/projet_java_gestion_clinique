package fr.clinique.view;

import fr.clinique.model.Utilisateur;
import javax.swing.*;
import java.awt.*;

public class VueAuthentification extends JFrame {
    private JTextField tfLogin;
    private JPasswordField pfPassword;
    private JButton btnConnexion;

    private static final ImageIcon LOGO_ICON = new ImageIcon("resources/logo.png");
    private static final ImageIcon USER_ICON = new ImageIcon("resources/user.png");
    private static final ImageIcon LOCK_ICON = new ImageIcon("resources/lock.png");

    public VueAuthentification() {
        ThemeManager.applyTheme();
        initUI();
    }

    private void initUI() {
        setTitle("Connexion - Clinique Médicale");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Panel principal avec gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, ThemeManager.SECONDARY_COLOR,
                        0, getHeight(), ThemeManager.PRIMARY_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Container pour le formulaire
        JPanel loginContainer = new JPanel();
        loginContainer.setLayout(new BoxLayout(loginContainer, BoxLayout.Y_AXIS));
        loginContainer.setBackground(ThemeManager.WHITE);
        loginContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.SECONDARY_COLOR, 2, true),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        // Logo de la clinique
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(LOGO_ICON);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setText("Clinique Médicale");
        logoLabel.setHorizontalTextPosition(JLabel.CENTER);
        logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
        logoLabel.setFont(ThemeManager.TITLE_FONT);
        logoLabel.setForeground(ThemeManager.PRIMARY_COLOR);

        // Titre
        JLabel titleLabel = ThemeManager.createSubtitleLabel("Connexion");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Panel pour le login
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        loginPanel.setBackground(ThemeManager.WHITE);
        JLabel loginLabel = new JLabel("Identifiant:");
        loginLabel.setIcon(USER_ICON);
        loginLabel.setFont(ThemeManager.NORMAL_FONT);
        tfLogin = ThemeManager.createStyledTextField(15);
        loginPanel.add(loginLabel);
        loginPanel.add(tfLogin);

        // Panel pour le mot de passe
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.setBackground(ThemeManager.WHITE);
        JLabel passwordLabel = new JLabel("Mot de passe:");
        passwordLabel.setIcon(LOCK_ICON);
        passwordLabel.setFont(ThemeManager.NORMAL_FONT);
        pfPassword = ThemeManager.createStyledPasswordField(15);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(pfPassword);

        // Bouton de connexion
        btnConnexion = ThemeManager.createStyledButton("Se connecter", ThemeManager.PRIMARY_COLOR);
        btnConnexion.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConnexion.setPreferredSize(new Dimension(200, 40));

        // Message d'aide
        JLabel helpLabel = new JLabel("Utilisez vos identifiants fournis par l'administration");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        helpLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        helpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ajouter les composants au container
        loginContainer.add(logoLabel);
        loginContainer.add(Box.createVerticalStrut(10));
        loginContainer.add(titleLabel);
        loginContainer.add(loginPanel);
        loginContainer.add(Box.createVerticalStrut(10));
        loginContainer.add(passwordPanel);
        loginContainer.add(Box.createVerticalStrut(20));
        loginContainer.add(btnConnexion);
        loginContainer.add(Box.createVerticalStrut(10));
        loginContainer.add(helpLabel);

        // Centrer le container dans le mainPanel
        mainPanel.add(loginContainer);

        // Configuration de la fenêtre
        add(mainPanel);
        setSize(450, 550);
        setLocationRelativeTo(null);

        // Focus sur le champ login au démarrage
        SwingUtilities.invokeLater(() -> tfLogin.requestFocusInWindow());

        setVisible(true);
    }

    // Getters existants
    public JTextField getTfLogin() {
        return tfLogin;
    }

    public JPasswordField getPfPassword() {
        return pfPassword;
    }

    public JButton getBtnConnexion() {
        return btnConnexion;
    }

    // Méthode pour afficher un message d'erreur stylisé
    public void afficherErreur(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Erreur de connexion",
                JOptionPane.ERROR_MESSAGE);
    }

    // Méthode pour ouvrir la vue principale
    public void ouvrirVuePrincipale(Utilisateur utilisateur) {
        this.setVisible(false);
        VuePrincipale vuePrincipale = new VuePrincipale(utilisateur);
        vuePrincipale.setVueAuthentification(this);
    }

    // Méthode pour réinitialiser le formulaire
    public void reinitialiserFormulaire() {
        tfLogin.setText("");
        pfPassword.setText("");
        tfLogin.requestFocus();
    }

    // Méthode pour afficher à nouveau la fenêtre
    public void afficherFenetreConnexion() {
        reinitialiserFormulaire();
        this.setVisible(true);
    }
}