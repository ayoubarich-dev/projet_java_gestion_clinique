package fr.clinique.view;

import fr.clinique.model.Medecin;
import fr.clinique.model.Patient;
import fr.clinique.model.RendezVous;
import fr.clinique.model.Role;
import fr.clinique.model.Utilisateur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class VueRendezVous extends JPanel {
    private Utilisateur utilisateur;

    private JTable tableRendezVous;
    private DefaultTableModel modelTable;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnExporterExcel, btnExporterPDF;

    // Formulaire d'ajout/modification
    private JDialog dialogFormulaire;
    private JComboBox<Patient> cbPatient;
    private JComboBox<Medecin> cbMedecin;
    private JFormattedTextField ftfDate;
    private JTextField tfHeure, tfMotif;
    private JButton btnValider, btnAnnuler;
    private boolean modeAjout = true;
    private int idRendezVousSelectionne = -1;

    public VueRendezVous(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Titre
        JLabel lblTitre = new JLabel("Gestion des Rendez-vous", JLabel.CENTER);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 16));

        // Tableau des rendez-vous
        String[] columnsNames = {"ID", "Patient", "Médecin", "Date", "Heure", "Motif"};
        modelTable = new DefaultTableModel(columnsNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre les cellules non éditables
            }
        };

        tableRendezVous = new JTable(modelTable);
        tableRendezVous.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableRendezVous);

        // Panel des boutons
        JPanel panelBoutons = new JPanel(new GridLayout(1, 5, 5, 5));
        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnExporterExcel = new JButton("Exporter Excel");
        btnExporterPDF = new JButton("Exporter PDF");

        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);
        panelBoutons.add(btnExporterExcel);
        panelBoutons.add(btnExporterPDF);

        // Ajout des composants au panel principal
        add(lblTitre, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBoutons, BorderLayout.SOUTH);

        // Gestion des droits selon le rôle
        if (utilisateur.getRole() == Role.MEDECIN) {
            btnAjouter.setEnabled(false);
            btnSupprimer.setEnabled(false);
        }

        // Double-clic sur une ligne du tableau
        tableRendezVous.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = tableRendezVous.getSelectedRow();
                    if (selectedRow >= 0) {
                        idRendezVousSelectionne = (int) tableRendezVous.getValueAt(selectedRow, 0);
                        if (utilisateur.getRole() != Role.MEDECIN) {
                            afficherFormulaireModification(idRendezVousSelectionne);
                        }
                    }
                }
            }
        });
    }

    // Méthodes d'affichage
   // Méthodes d'affichage
    public void afficherDonnees(List<RendezVous> rendezVousList) {
        modelTable.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (RendezVous rv : rendezVousList) {
            modelTable.addRow(new Object[] {
                    rv.getId(),
                    rv.getPatient().getPrenom() + " " + rv.getPatient().getNom(),
                    "Dr. " + rv.getMedecin().getPrenom() + " " + rv.getMedecin().getNom(),
                    sdf.format(rv.getDate()),
                    rv.getHeure(),
                    rv.getMotif()
            });
        }
    }

    public void afficherFormulaireAjout() {
        modeAjout = true;
        creerFormulaire();

        // Vider les champs
        ftfDate.setText("");
        tfHeure.setText("");
        tfMotif.setText("");

        dialogFormulaire.setTitle("Ajouter un rendez-vous");
        dialogFormulaire.setVisible(true);
    }

    public void afficherFormulaireModification(int id) {
        modeAjout = false;
        creerFormulaire();

        dialogFormulaire.setTitle("Modifier un rendez-vous");
        dialogFormulaire.setVisible(true);
    }

    public void remplirComboBoxes(List<Patient> patients, List<Medecin> medecins) {
        // Vider les combobox
        cbPatient.removeAllItems();
        cbMedecin.removeAllItems();

        // Remplir avec les données actuelles
        for (Patient patient : patients) {
            cbPatient.addItem(patient);
        }

        for (Medecin medecin : medecins) {
            cbMedecin.addItem(medecin);
        }
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

            dialogFormulaire.setSize(500, 350);
            dialogFormulaire.setLocationRelativeTo(window);
            dialogFormulaire.setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Patient
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Patient:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            cbPatient = new JComboBox<>();
            panel.add(cbPatient, gbc);

            // Médecin
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0;
            panel.add(new JLabel("Médecin:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            cbMedecin = new JComboBox<>();
            panel.add(cbMedecin, gbc);

            // Date
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0;
            panel.add(new JLabel("Date:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            ftfDate = new JFormattedTextField();
            ftfDate.setToolTipText("Format: JJ/MM/AAAA");
            panel.add(ftfDate, gbc);

            // Heure
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.weightx = 0;
            panel.add(new JLabel("Heure:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            tfHeure = new JTextField();
            tfHeure.setToolTipText("Format: HH:MM");
            panel.add(tfHeure, gbc);

            // Motif
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.weightx = 0;
            panel.add(new JLabel("Motif:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.gridheight = 2;
            tfMotif = new JTextField();
            panel.add(tfMotif, gbc);

            JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnValider = new JButton("Valider");
            btnAnnuler = new JButton("Annuler");

            // Ajout de débogage sur les boutons
            btnValider.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("DEBUG: Bouton Valider du formulaire rendez-vous cliqué");
                }
            });

            btnAnnuler.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("DEBUG: Bouton Annuler du formulaire rendez-vous cliqué");
                    if (dialogFormulaire != null) {
                        dialogFormulaire.dispose();
                    }
                }
            });

            panelBoutons.add(btnValider);
            panelBoutons.add(btnAnnuler);

            dialogFormulaire.add(panel, BorderLayout.CENTER);
            dialogFormulaire.add(panelBoutons, BorderLayout.SOUTH);
        }
    }

    // Méthode pour remplir le formulaire avec les données d'un rendez-vous existant
    public void remplirFormulaireModification(RendezVous rv) {
        // Sélectionner le patient et le médecin dans les combobox
        for (int i = 0; i < cbPatient.getItemCount(); i++) {
            if (cbPatient.getItemAt(i).getId() == rv.getPatient().getId()) {
                cbPatient.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < cbMedecin.getItemCount(); i++) {
            if (cbMedecin.getItemAt(i).getId() == rv.getMedecin().getId()) {
                cbMedecin.setSelectedIndex(i);
                break;
            }
        }

        // Remplir les autres champs
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        ftfDate.setText(sdf.format(rv.getDate()));
        tfHeure.setText(rv.getHeure());
        tfMotif.setText(rv.getMotif());
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
    
    public JTable getTableRendezVous() {
        return tableRendezVous;
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

    public JButton getBtnExporterExcel() {
        return btnExporterExcel;
    }

    public JButton getBtnExporterPDF() {
        return btnExporterPDF;
    }

    public JDialog getDialogFormulaire() {
        return dialogFormulaire;
    }

    public JComboBox<Patient> getCbPatient() {
        return cbPatient;
    }

    public JComboBox<Medecin> getCbMedecin() {
        return cbMedecin;
    }

    public JFormattedTextField getFtfDate() {
        return ftfDate;
    }

    public JTextField getTfHeure() {
        return tfHeure;
    }

    public JTextField getTfMotif() {
        return tfMotif;
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

    public int getIdRendezVousSelectionne() {
        return idRendezVousSelectionne;
    }

    public void setModeAjout(boolean modeAjout) {
        this.modeAjout = modeAjout;
    }

    public void setIdRendezVousSelectionne(int idRendezVousSelectionne) {
        this.idRendezVousSelectionne = idRendezVousSelectionne;
    }
}