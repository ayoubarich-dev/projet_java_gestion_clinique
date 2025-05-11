package fr.clinique.view;

import fr.clinique.model.Patient;
import fr.clinique.model.Role;
import fr.clinique.model.Utilisateur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class VuePatient extends JPanel {
    private Utilisateur utilisateur;

    private JTable tablePatients;
    private DefaultTableModel modelTable;
    private JTextField tfRecherche;
    private JButton btnRecherche, btnAjouter, btnModifier, btnSupprimer, btnRafraichir;

    // Formulaire d'ajout/modification
    private JDialog dialogFormulaire;
    private JTextField tfNom, tfPrenom, tfTelephone;
    private JFormattedTextField ftfDateNaissance;
    private JButton btnValider, btnAnnuler;
    private boolean modeAjout = true;
    private int idPatientSelectionne = -1;

    public VuePatient(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de recherche en haut
        JPanel panelRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblRecherche = new JLabel("Rechercher un patient:");
        tfRecherche = new JTextField(20);
        btnRecherche = new JButton("Rechercher");
        btnRafraichir = new JButton("Rafraîchir");

        panelRecherche.add(lblRecherche);
        panelRecherche.add(tfRecherche);
        panelRecherche.add(btnRecherche);
        panelRecherche.add(btnRafraichir);

        // Tableau des patients
        String[] columnsNames = {"ID", "Nom", "Prénom", "Date de naissance", "Téléphone", "N° Dossier"};
        modelTable = new DefaultTableModel(columnsNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre les cellules non éditables
            }
        };

        tablePatients = new JTable(modelTable);
        tablePatients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tablePatients);

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
        if (utilisateur.getRole() == Role.SECRETAIRE) {
            btnSupprimer.setEnabled(false);
        }
        
        if (utilisateur.getRole() == Role.MEDECIN) {
            btnAjouter.setEnabled(false);
            btnSupprimer.setEnabled(false);
        }
    }

    // Méthodes d'affichage
    public void afficherDonnees(List<Patient> patients) {
        modelTable.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Patient patient : patients) {
            modelTable.addRow(new Object[] {
                    patient.getId(),
                    patient.getNom(),
                    patient.getPrenom(),
                    sdf.format(patient.getDateNaissance()),
                    patient.getTelephone(),
                    patient.getNumeroDossier()
            });
        }
    }

    public void afficherFormulaireAjout() {
        modeAjout = true;
        creerFormulaire();
        tfNom.setText("");
        tfPrenom.setText("");
        tfTelephone.setText("");
        ftfDateNaissance.setText("");

        dialogFormulaire.setTitle("Ajouter un patient");
        dialogFormulaire.setVisible(true);
    }

    public void afficherFormulaireModification(int id) {
        modeAjout = false;

        // Rechercher le patient dans le tableau
        for (int i = 0; i < modelTable.getRowCount(); i++) {
            if ((int) modelTable.getValueAt(i, 0) == id) {
                creerFormulaire();

                tfNom.setText((String) modelTable.getValueAt(i, 1));
                tfPrenom.setText((String) modelTable.getValueAt(i, 2));
                ftfDateNaissance.setText((String) modelTable.getValueAt(i, 3));
                tfTelephone.setText((String) modelTable.getValueAt(i, 4));

                dialogFormulaire.setTitle("Modifier un patient");
                dialogFormulaire.setVisible(true);
                return;
            }
        }

        JOptionPane.showMessageDialog(this,
                "Patient introuvable",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
    }

    private void creerFormulaire() {
        if (dialogFormulaire == null) {
            // Créer la boîte de dialogue une seule fois
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                dialogFormulaire = new JDialog((JFrame) window, "", true);
            } else {
                dialogFormulaire = new JDialog((Dialog) window, "", true);
            }

            dialogFormulaire.setSize(400, 250);
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

            // Date de naissance
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0;
            panel.add(new JLabel("Date de naissance:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            ftfDateNaissance = new JFormattedTextField();
            ftfDateNaissance.setToolTipText("Format: JJ/MM/AAAA");
            panel.add(ftfDateNaissance, gbc);

            // Téléphone
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.weightx = 0;
            panel.add(new JLabel("Téléphone:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            tfTelephone = new JTextField(20);
            panel.add(tfTelephone, gbc);

            // Boutons
            JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnValider = new JButton("Valider");
            btnAnnuler = new JButton("Annuler");

            panelBoutons.add(btnValider);
            panelBoutons.add(btnAnnuler);

            dialogFormulaire.add(panel, BorderLayout.CENTER);
            dialogFormulaire.add(panelBoutons, BorderLayout.SOUTH);
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
    
    public JTable getTablePatients() {
        return tablePatients;
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

    public JTextField getTfTelephone() {
        return tfTelephone;
    }

    public JFormattedTextField getFtfDateNaissance() {
        return ftfDateNaissance;
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

    public int getIdPatientSelectionne() {
        return idPatientSelectionne;
    }

    public void setModeAjout(boolean modeAjout) {
        this.modeAjout = modeAjout;
    }

    public void setIdPatientSelectionne(int idPatientSelectionne) {
        this.idPatientSelectionne = idPatientSelectionne;
    }
}