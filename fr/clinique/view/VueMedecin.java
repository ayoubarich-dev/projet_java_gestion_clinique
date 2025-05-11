package fr.clinique.view;

import fr.clinique.model.Medecin;
import fr.clinique.model.Role;
import fr.clinique.model.Utilisateur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class VueMedecin extends JPanel {
    private Utilisateur utilisateur;

    private JTable tableMedecins;
    private DefaultTableModel modelTable;
    private JTextField tfRecherche;
    private JButton btnRecherche, btnAjouter, btnModifier, btnSupprimer, btnRafraichir;

    // Formulaire d'ajout/modification
    private JDialog dialogFormulaire;
    private JTextField tfNom, tfPrenom, tfSpecialite, tfHoraires, tfLogin, tfPassword;
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

    // Méthodes d'affichage et de mise à jour
    public void afficherDonnees(List<Medecin> medecins) {
        System.out.println("Affichage des données médecins: " + medecins.size() + " médecins");
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

    public void afficherFormulaireAjout() {
        System.out.println("Affichage du formulaire d'ajout médecin");
        modeAjout = true;
        creerFormulaire();

        // Vider les champs
        tfNom.setText("");
        tfPrenom.setText("");
        tfSpecialite.setText("");
        tfHoraires.setText("");
        tfLogin.setText("");
        tfPassword.setText("");

        // Le panneau de login/password n'est visible qu'en mode ajout
        dialogFormulaire.getContentPane().getComponent(0).setVisible(true);

        dialogFormulaire.setTitle("Ajouter un médecin");
        dialogFormulaire.setVisible(true);

        System.out.println("Formulaire d'ajout médecin affiché");
    }

    public void afficherFormulaireModification(int id) {
        System.out.println("Affichage du formulaire de modification médecin pour id=" + id);
        modeAjout = false;

        // Rechercher le médecin dans le tableau
        for (int i = 0; i < modelTable.getRowCount(); i++) {
            if ((int) modelTable.getValueAt(i, 0) == id) {
                creerFormulaire();

                tfNom.setText((String) modelTable.getValueAt(i, 1));
                tfPrenom.setText((String) modelTable.getValueAt(i, 2));
                tfSpecialite.setText((String) modelTable.getValueAt(i, 3));
                tfHoraires.setText((String) modelTable.getValueAt(i, 4));

                // Le panneau de login/password n'est pas visible en mode modification
                dialogFormulaire.getContentPane().getComponent(0).setVisible(false);

                dialogFormulaire.setTitle("Modifier un médecin");
                dialogFormulaire.setVisible(true);

                System.out.println("Formulaire de modification médecin affiché");
                return;
            }
        }

        JOptionPane.showMessageDialog(this,
                "Médecin introuvable",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
    }

    private void creerFormulaire() {
        System.out.println("Création du formulaire médecin");
        if (dialogFormulaire == null) {
            // Créer la boîte de dialogue une seule fois
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                dialogFormulaire = new JDialog((JFrame) window, "", true);
            } else {
                dialogFormulaire = new JDialog((Dialog) window, "", true);
            }

            dialogFormulaire.setSize(400, 350);
            dialogFormulaire.setLocationRelativeTo(window);
            dialogFormulaire.setLayout(new BorderLayout());

            // Panel pour les informations de connexion (uniquement pour l'ajout)
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
            tfPassword = new JPasswordField(20);
            panelLogin.add(tfPassword, gbcLogin);

            // Panel pour les informations du médecin
            JPanel panelInfo = new JPanel(new GridBagLayout());
            panelInfo.setBorder(BorderFactory.createTitledBorder("Informations du médecin"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Nom
            gbc.gridx = 0;
            gbc.gridy = 0;
            panelInfo.add(new JLabel("Nom:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            tfNom = new JTextField(20);
            panelInfo.add(tfNom, gbc);

            // Prénom
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0;
            panelInfo.add(new JLabel("Prénom:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            tfPrenom = new JTextField(20);
            panelInfo.add(tfPrenom, gbc);

            // Spécialité
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0;
            panelInfo.add(new JLabel("Spécialité:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            tfSpecialite = new JTextField(20);
            panelInfo.add(tfSpecialite, gbc);

            // Horaires
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.weightx = 0;
            panelInfo.add(new JLabel("Horaires:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            tfHoraires = new JTextField(20);
            panelInfo.add(tfHoraires, gbc);

            // Panel principal du formulaire
            JPanel panelFormulaire = new JPanel(new BorderLayout());
            panelFormulaire.add(panelLogin, BorderLayout.NORTH);
            panelFormulaire.add(panelInfo, BorderLayout.CENTER);

            // Boutons
            JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnValider = new JButton("Valider");
            btnAnnuler = new JButton("Annuler");

            System.out.println("Création des boutons du formulaire médecin");
            System.out.println("btnValider créé: " + (btnValider != null));
            System.out.println("btnAnnuler créé: " + (btnAnnuler != null));

            panelBoutons.add(btnValider);
            panelBoutons.add(btnAnnuler);

            dialogFormulaire.add(panelFormulaire, BorderLayout.CENTER);
            dialogFormulaire.add(panelBoutons, BorderLayout.SOUTH);
        }

        // S'assurer que les boutons sont visibles
        if (btnValider != null) {
            btnValider.setVisible(true);
        }

        if (btnAnnuler != null) {
            btnAnnuler.setVisible(true);
        }
    }

    // Méthodes pour le contrôleur
    public void afficherMessage(String message, String titre, int messageType) {
        JOptionPane.showMessageDialog(this, message, titre, messageType);
    }

    public int afficherConfirmation(String message, String titre) {
        return JOptionPane.showConfirmDialog(this, message, titre, JOptionPane.YES_NO_OPTION);
    }

    // Getters pour les composants d'interface utilisateur
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

    public JTextField getTfPassword() {
        return tfPassword;
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