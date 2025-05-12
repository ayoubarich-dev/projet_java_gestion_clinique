package fr.clinique.view;

import fr.clinique.model.Medecin;
import fr.clinique.model.Role;
import fr.clinique.model.Utilisateur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VueMedecin extends JPanel {
    private Utilisateur utilisateur;

    private JTable tableMedecins;
    private DefaultTableModel modelTable;
    private JTextField tfRecherche;
    private JButton btnRecherche, btnAjouter, btnModifier, btnSupprimer, btnRafraichir;

    // Composants du formulaire
    private JDialog dialogFormulaire;
    private JTextField tfNom, tfPrenom, tfSpecialite, tfHoraires, tfLogin;
    private JPasswordField pfPassword;
    private JButton btnValider, btnAnnuler;
    private boolean modeAjout = true;
    private int idMedecinSelectionne = -1;

    public VueMedecin(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de recherche en haut
        JPanel panelRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblRecherche = new JLabel("Rechercher un médecin:");
        tfRecherche = new JTextField(20);
        btnRecherche = new JButton("Rechercher");
        btnRafraichir = new JButton("Rafraîchir");

        panelRecherche.add(lblRecherche);
        panelRecherche.add(tfRecherche);
        panelRecherche.add(btnRecherche);
        panelRecherche.add(btnRafraichir);

        // Tableau des médecins
        String[] columnsNames = {"ID", "Nom", "Prénom", "Spécialité", "Horaires"};
        modelTable = new DefaultTableModel(columnsNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre les cellules non éditables
            }
        };

        tableMedecins = new JTable(modelTable);
        tableMedecins.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableMedecins);

        // Panel des boutons en bas
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");

        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);

        // Ajout des composants au panel principal
        add(panelRecherche, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBoutons, BorderLayout.SOUTH);

        // Gestion des droits selon le rôle
        if (utilisateur.getRole() != Role.ADMINISTRATEUR) {
            btnAjouter.setEnabled(false);
            btnSupprimer.setEnabled(false);
            btnModifier.setEnabled(false);
        }
    }

    // Méthode pour créer le formulaire d'ajout/modification
    private void creerFormulaire() {
        if (dialogFormulaire == null) {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                dialogFormulaire = new JDialog((JFrame) window, "", true);
            } else {
                dialogFormulaire = new JDialog((Dialog) window, "", true);
            }

            dialogFormulaire.setSize(400, 350);
            dialogFormulaire.setLocationRelativeTo(window);
            dialogFormulaire.setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Nom
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Nom:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            tfNom = new JTextField(20);
            panel.add(tfNom, gbc);

            // Prénom
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0;
            panel.add(new JLabel("Prénom:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            tfPrenom = new JTextField(20);
            panel.add(tfPrenom, gbc);

            // Spécialité
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0;
            panel.add(new JLabel("Spécialité:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            tfSpecialite = new JTextField(20);
            panel.add(tfSpecialite, gbc);

            // Horaires
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.weightx = 0;
            panel.add(new JLabel("Horaires:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            tfHoraires = new JTextField(20);
            panel.add(tfHoraires, gbc);

            // Section login/password (uniquement pour l'ajout)
            JPanel panelLogin = new JPanel(new GridBagLayout());
            panelLogin.setBorder(BorderFactory.createTitledBorder("Informations de connexion"));
            GridBagConstraints gbcLogin = new GridBagConstraints();
            gbcLogin.insets = new Insets(5, 5, 5, 5);
            gbcLogin.fill = GridBagConstraints.HORIZONTAL;

            // Login
            gbcLogin.gridx = 0;
            gbcLogin.gridy = 0;
            panelLogin.add(new JLabel("Login:"), gbcLogin);

            gbcLogin.gridx = 1;
            gbcLogin.weightx = 1;
            tfLogin = new JTextField(20);
            panelLogin.add(tfLogin, gbcLogin);

            // Password
            gbcLogin.gridx = 0;
            gbcLogin.gridy = 1;
            gbcLogin.weightx = 0;
            panelLogin.add(new JLabel("Mot de passe:"), gbcLogin);

            gbcLogin.gridx = 1;
            gbcLogin.weightx = 1;
            pfPassword = new JPasswordField(20);
            panelLogin.add(pfPassword, gbcLogin);

            // Boutons
            JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnValider = new JButton("Valider");
            btnAnnuler = new JButton("Annuler");

            panelBoutons.add(btnValider);
            panelBoutons.add(btnAnnuler);

            // Panneau principal du formulaire
            JPanel mainPanel = new JPanel(new BorderLayout());
            JPanel fieldsPanel = new JPanel(new BorderLayout());
            fieldsPanel.add(panel, BorderLayout.NORTH);
            fieldsPanel.add(panelLogin, BorderLayout.CENTER);

            mainPanel.add(fieldsPanel, BorderLayout.CENTER);
            mainPanel.add(panelBoutons, BorderLayout.SOUTH);

            dialogFormulaire.add(mainPanel);
        }
    }

    // Méthode pour afficher le formulaire d'ajout
    public void afficherFormulaireAjout() {
        modeAjout = true;
        creerFormulaire();

        // Réinitialiser tous les champs
        tfNom.setText("");
        tfPrenom.setText("");
        tfSpecialite.setText("");
        tfHoraires.setText("");
        tfLogin.setText("");
        pfPassword.setText("");

        // Afficher le panneau de login pour l'ajout
        Container contentPane = dialogFormulaire.getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        JPanel fieldsPanel = (JPanel) mainPanel.getComponent(0);
        JPanel loginPanel = (JPanel) fieldsPanel.getComponent(1);
        loginPanel.setVisible(true);

        dialogFormulaire.setTitle("Ajouter un médecin");
        dialogFormulaire.setVisible(true);
    }

    // Méthode pour afficher le formulaire de modification
    public void afficherFormulaireModification(int id) {
        modeAjout = false;
        idMedecinSelectionne = id;
        creerFormulaire();

        // Charger les données du médecin
        Medecin medecin = Medecin.getMedecinById(id);
        if (medecin != null) {
            tfNom.setText(medecin.getNom());
            tfPrenom.setText(medecin.getPrenom());
            tfSpecialite.setText(medecin.getSpecialite());
            tfHoraires.setText(medecin.getHoraires());

            // Cacher le panneau de login pour la modification
            Container contentPane = dialogFormulaire.getContentPane();
            JPanel mainPanel = (JPanel) contentPane.getComponent(0);
            JPanel fieldsPanel = (JPanel) mainPanel.getComponent(0);
            JPanel loginPanel = (JPanel) fieldsPanel.getComponent(1);
            loginPanel.setVisible(false);

            dialogFormulaire.setTitle("Modifier un médecin");
            dialogFormulaire.setVisible(true);
        }
    }

    // Méthodes d'affichage et de mise à jour
    public void afficherDonnees(List<Medecin> medecins) {
        modelTable.setRowCount(0);

        for (Medecin medecin : medecins) {
            modelTable.addRow(new Object[] {
                    medecin.getId(),
                    medecin.getNom(),
                    medecin.getPrenom(),
                    medecin.getSpecialite(),
                    medecin.getHoraires()
            });
        }
    }

    // Méthodes pour le contrôleur
    public void afficherMessage(String message, String titre, int messageType) {
        JOptionPane.showMessageDialog(this, message, titre, messageType);
    }

    public int afficherConfirmation(String message, String titre) {
        return JOptionPane.showConfirmDialog(this, message, titre, JOptionPane.YES_NO_OPTION);
    }

    // Getters
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public JTable getTableMedecins() {
        return tableMedecins;
    }

    public DefaultTableModel getModelTable() {
        return modelTable;
    }

    public JTextField getTfRecherche() {
        return tfRecherche;
    }

    public JButton getBtnRecherche() {
        return btnRecherche;
    }

    public JButton getBtnAjouter() {
        return btnAjouter;
    }

    public JButton getBtnModifier() {
        return btnModifier;
    }

    public JButton getBtnSupprimer() {
        return btnSupprimer;
    }

    public JButton getBtnRafraichir() {
        return btnRafraichir;
    }

    // Getters pour le formulaire
    public JDialog getDialogFormulaire() {
        return dialogFormulaire;
    }

    public JTextField getTfNom() {
        return tfNom;
    }

    public JTextField getTfPrenom() {
        return tfPrenom;
    }

    public JTextField getTfSpecialite() {
        return tfSpecialite;
    }

    public JTextField getTfHoraires() {
        return tfHoraires;
    }

    public JTextField getTfLogin() {
        return tfLogin;
    }

    public JPasswordField getPfPassword() {
        return pfPassword;
    }

    public JButton getBtnValider() {
        return btnValider;
    }

    public JButton getBtnAnnuler() {
        return btnAnnuler;
    }

    public boolean isModeAjout() {
        return modeAjout;
    }

    public int getIdMedecinSelectionne() {
        return idMedecinSelectionne;
    }

    public void setModeAjout(boolean modeAjout) {
        this.modeAjout = modeAjout;
    }

    public void setIdMedecinSelectionne(int idMedecinSelectionne) {
        this.idMedecinSelectionne = idMedecinSelectionne;
    }
}