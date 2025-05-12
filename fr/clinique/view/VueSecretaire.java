package fr.clinique.view;

import fr.clinique.model.Role;
import fr.clinique.model.Secretaire;
import fr.clinique.model.Utilisateur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VueSecretaire extends JPanel {
    private Utilisateur utilisateur;
    private JTable tableSecretaires;
    private DefaultTableModel modelTable;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnRafraichir;

    // Composants du formulaire
    private JDialog dialogFormulaire;
    private JTextField tfNom, tfPrenom, tfLogin;
    private JPasswordField pfPassword;
    private JButton btnValider, btnAnnuler;
    private boolean modeAjout = true;
    private int idSecretaireSelectionne = -1;

    public VueSecretaire(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Titre
        JLabel lblTitre = new JLabel("Gestion des Secrétaires", JLabel.CENTER);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 16));

        // Tableau des secrétaires
        String[] columnsNames = {"ID", "Nom", "Prénom", "Login"};
        modelTable = new DefaultTableModel(columnsNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre les cellules non éditables
            }
        };

        tableSecretaires = new JTable(modelTable);
        tableSecretaires.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableSecretaires);

        // Panel des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnRafraichir = new JButton("Rafraîchir");

        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);
        panelBoutons.add(btnRafraichir);

        // Ajout des composants au panel principal
        add(lblTitre, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBoutons, BorderLayout.SOUTH);

        // Cette vue n'est accessible qu'aux administrateurs, donc pas besoin de vérifier les droits
    }

    // Méthode pour créer le formulaire d'ajout/modification
    private void creerFormulaire() {
        if (dialogFormulaire == null) {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                dialogFormulaire = new JDialog((JFrame) window, "", true);
            } else if (window instanceof JDialog) {
                dialogFormulaire = new JDialog((JDialog) window, "", true);
            } else {
                dialogFormulaire = new JDialog((JFrame) null, "", true);
            }

            dialogFormulaire.setSize(400, 300);
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

            // Section login/password
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

            // Password (uniquement pour l'ajout)
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

            // Ajouter les écouteurs aux boutons
            btnValider.addActionListener(e -> validerFormulaire());
            btnAnnuler.addActionListener(e -> dialogFormulaire.dispose());

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
        tfLogin.setText("");
        pfPassword.setText("");

        // Afficher le panneau de mot de passe pour l'ajout
        Container contentPane = dialogFormulaire.getContentPane();
        JPanel mainPanel = (JPanel) contentPane.getComponent(0);
        JPanel fieldsPanel = (JPanel) mainPanel.getComponent(0);
        JPanel loginPanel = (JPanel) fieldsPanel.getComponent(1);

        // Rendre le champ mot de passe visible
        Component[] components = loginPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JLabel && ((JLabel)components[i]).getText().equals("Mot de passe:")) {
                components[i].setVisible(true);
                if (i + 1 < components.length) {
                    components[i + 1].setVisible(true);
                }
                break;
            }
        }

        dialogFormulaire.setTitle("Ajouter un secrétaire");
        dialogFormulaire.setVisible(true);
    }

    // Méthode pour afficher le formulaire de modification
    public void afficherFormulaireModification(int id) {
        modeAjout = false;
        idSecretaireSelectionne = id;
        creerFormulaire();

        // Charger les données du secrétaire
        Secretaire secretaire = getSecretaireById(id);
        if (secretaire != null) {
            tfNom.setText(secretaire.getNom());
            tfPrenom.setText(secretaire.getPrenom());
            tfLogin.setText(secretaire.getLogin());
            tfLogin.setEnabled(false); // Le login ne peut pas être modifié

            // Cacher le champ mot de passe pour la modification
            Container contentPane = dialogFormulaire.getContentPane();
            JPanel mainPanel = (JPanel) contentPane.getComponent(0);
            JPanel fieldsPanel = (JPanel) mainPanel.getComponent(0);
            JPanel loginPanel = (JPanel) fieldsPanel.getComponent(1);

            // Cacher le champ mot de passe
            Component[] components = loginPanel.getComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i] instanceof JLabel && ((JLabel)components[i]).getText().equals("Mot de passe:")) {
                    components[i].setVisible(false);
                    if (i + 1 < components.length) {
                        components[i + 1].setVisible(false);
                    }
                    break;
                }
            }

            dialogFormulaire.setTitle("Modifier un secrétaire");
            dialogFormulaire.setVisible(true);
        } else {
            afficherMessage("Secrétaire introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode pour valider le formulaire
    private void validerFormulaire() {
        // Validation des champs
        if (tfNom.getText().trim().isEmpty() ||
                tfPrenom.getText().trim().isEmpty() ||
                tfLogin.getText().trim().isEmpty()) {

            afficherMessage("Veuillez remplir tous les champs obligatoires", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // En mode ajout, vérifier aussi le mot de passe
        if (modeAjout && pfPassword.getPassword().length == 0) {
            afficherMessage("Veuillez saisir un mot de passe", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean result;

            if (modeAjout) {
                // Ajouter un nouveau secrétaire
                Secretaire secretaire = new Secretaire(
                        tfNom.getText().trim(),
                        tfPrenom.getText().trim(),
                        tfLogin.getText().trim(),
                        new String(pfPassword.getPassword())
                );
                result = secretaire.enregistrer();

                if (result) {
                    afficherMessage("Secrétaire ajouté avec succès", "Ajout réussi", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    afficherMessage("Erreur lors de l'ajout du secrétaire", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Modifier un secrétaire existant
                Secretaire secretaire = getSecretaireById(idSecretaireSelectionne);
                if (secretaire != null) {
                    secretaire.setNom(tfNom.getText().trim());
                    secretaire.setPrenom(tfPrenom.getText().trim());
                    result = secretaire.enregistrer();

                    if (result) {
                        afficherMessage("Secrétaire modifié avec succès", "Modification réussie", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        afficherMessage("Erreur lors de la modification du secrétaire", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    afficherMessage("Secrétaire introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                    result = false;
                }
            }

            if (result) {
                dialogFormulaire.dispose();
                chargerDonnees(); // Rafraîchir la liste
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            afficherMessage("Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode utilitaire pour récupérer un secrétaire par son ID
    private Secretaire getSecretaireById(int id) {
        List<Secretaire> secretaires = Secretaire.getAllSecretaires();
        for (Secretaire s : secretaires) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    // Méthode pour charger les données dans le tableau
    private void chargerDonnees() {
        afficherDonnees(Secretaire.getAllSecretaires());
    }

    // Méthodes d'affichage
    public void afficherDonnees(List<Secretaire> secretaires) {
        modelTable.setRowCount(0);

        for (Secretaire secretaire : secretaires) {
            modelTable.addRow(new Object[] {
                    secretaire.getId(),
                    secretaire.getNom(),
                    secretaire.getPrenom(),
                    secretaire.getLogin()
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

    // Getters pour les composants d'interface utilisateur
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public JTable getTableSecretaires() {
        return tableSecretaires;
    }

    public DefaultTableModel getModelTable() {
        return modelTable;
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

    public int getIdSecretaireSelectionne() {
        return idSecretaireSelectionne;
    }

    public void setModeAjout(boolean modeAjout) {
        this.modeAjout = modeAjout;
    }

    public void setIdSecretaireSelectionne(int idSecretaireSelectionne) {
        this.idSecretaireSelectionne = idSecretaireSelectionne;
    }
}