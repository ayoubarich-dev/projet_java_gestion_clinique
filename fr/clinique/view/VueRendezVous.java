package fr.clinique.view;

import fr.clinique.controller.ControleurRendezVous;
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

    // Contrôleur
    private ControleurRendezVous controleur;

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
        System.out.println("=== Remplissage des ComboBoxes ===");
        System.out.println("Nombre de patients reçus: " + patients.size());
        System.out.println("Nombre de médecins reçus: " + medecins.size());

        // S'assurer que les comboboxes existent
        if (cbPatient == null || cbMedecin == null) {
            System.err.println("ERREUR: Les ComboBoxes ne sont pas encore initialisées!");
            System.err.println("cbPatient: " + cbPatient);
            System.err.println("cbMedecin: " + cbMedecin);
            System.err.println("Création du formulaire nécessaire avant le remplissage des comboboxes");
            creerFormulaire();
        }

        // Vider les combobox
        cbPatient.removeAllItems();
        cbMedecin.removeAllItems();

        // Remplir avec les données actuelles
        for (Patient patient : patients) {
            if (patient != null) {
                cbPatient.addItem(patient);
                System.out.println("Ajout patient: " + patient.getId() + " - " + patient.getNom() + " " + patient.getPrenom());
            }
        }

        for (Medecin medecin : medecins) {
            if (medecin != null) {
                cbMedecin.addItem(medecin);
                System.out.println("Ajout médecin: " + medecin.getId() + " - " + medecin.getNom() + " " + medecin.getPrenom());
            }
        }

        System.out.println("ComboBox patient - Nombre d'items: " + cbPatient.getItemCount());
        System.out.println("ComboBox médecin - Nombre d'items: " + cbMedecin.getItemCount());
    }

    // Méthode publique pour créer le formulaire (appelée par le contrôleur)
    public void creerFormulairePublic() {
        creerFormulaire();
    }

    private void creerFormulaire() {
        if (dialogFormulaire == null) {
            System.out.println("=== Création du formulaire de rendez-vous ===");

            // Créer la boîte de dialogue une seule fois
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                dialogFormulaire = new JDialog((JFrame) window, "", true);
            } else if (window instanceof JDialog) {
                dialogFormulaire = new JDialog((JDialog) window, "", true);
            } else {
                // Fallback si le parent n'est pas trouvé
                dialogFormulaire = new JDialog((JFrame) null, "", true);
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
            // Renderer personnalisé pour afficher correctement les patients
            cbPatient.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value != null && value instanceof Patient) {
                        Patient patient = (Patient) value;
                        setText(patient.getPrenom() + " " + patient.getNom());
                    } else if (value == null) {
                        setText("");
                    }
                    return this;
                }
            });
            panel.add(cbPatient, gbc);

            // Médecin
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0;
            panel.add(new JLabel("Médecin:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            cbMedecin = new JComboBox<>();
            // Renderer personnalisé pour afficher correctement les médecins
            cbMedecin.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value != null && value instanceof Medecin) {
                        Medecin medecin = (Medecin) value;
                        setText("Dr. " + medecin.getPrenom() + " " + medecin.getNom() + " (" + medecin.getSpecialite() + ")");
                    } else if (value == null) {
                        setText("");
                    }
                    return this;
                }
            });
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

            System.out.println("Création des boutons du formulaire");
            System.out.println("btnValider créé: " + (btnValider != null));
            System.out.println("btnAnnuler créé: " + (btnAnnuler != null));

            // Ajouter les écouteurs aux boutons directement ici (comme dans VuePatient)
            btnValider.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    validerFormulaire();
                }
            });

            btnAnnuler.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialogFormulaire.dispose();
                }
            });

            panelBoutons.add(btnValider);
            panelBoutons.add(btnAnnuler);

            dialogFormulaire.add(panel, BorderLayout.CENTER);
            dialogFormulaire.add(panelBoutons, BorderLayout.SOUTH);

            // Forcer la mise à jour de l'interface
            dialogFormulaire.pack();
            dialogFormulaire.setSize(500, 350);

            System.out.println("Formulaire créé avec succès");
            System.out.println("cbPatient initialisé: " + (cbPatient != null));
            System.out.println("cbMedecin initialisé: " + (cbMedecin != null));
        }
    }

    // Méthode privée pour valider le formulaire (comme dans VuePatient)
    private void validerFormulaire() {
        if (controleur != null) {
            controleur.validerFormulaire();
        } else {
            System.err.println("Contrôleur null - impossible de valider le formulaire");
        }
    }

    // Méthode pour remplir le formulaire avec les données d'un rendez-vous existant
    public void remplirFormulaireModification(RendezVous rv) {
        if (rv == null || rv.getPatient() == null || rv.getMedecin() == null) {
            System.err.println("Rendez-vous, patient ou médecin null!");
            return;
        }

        System.out.println("=== Remplissage du formulaire pour modification ===");
        System.out.println("Rendez-vous ID: " + rv.getId());
        System.out.println("Patient: " + rv.getPatient().getNom() + " " + rv.getPatient().getPrenom());
        System.out.println("Médecin: " + rv.getMedecin().getNom() + " " + rv.getMedecin().getPrenom());

        // Sélectionner le patient et le médecin dans les combobox
        for (int i = 0; i < cbPatient.getItemCount(); i++) {
            Patient p = cbPatient.getItemAt(i);
            if (p != null && p.getId() == rv.getPatient().getId()) {
                cbPatient.setSelectedIndex(i);
                System.out.println("Patient sélectionné à l'index: " + i);
                break;
            }
        }

        for (int i = 0; i < cbMedecin.getItemCount(); i++) {
            Medecin m = cbMedecin.getItemAt(i);
            if (m != null && m.getId() == rv.getMedecin().getId()) {
                cbMedecin.setSelectedIndex(i);
                System.out.println("Médecin sélectionné à l'index: " + i);
                break;
            }
        }

        // Remplir les autres champs
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        ftfDate.setText(sdf.format(rv.getDate()));
        tfHeure.setText(rv.getHeure());
        tfMotif.setText(rv.getMotif());

        System.out.println("Formulaire rempli avec les données du rendez-vous");
    }

    // Méthodes pour le contrôleur
    public void afficherMessage(String message, String titre, int messageType) {
        JOptionPane.showMessageDialog(this, message, titre, messageType);
    }

    public int afficherConfirmation(String message, String titre) {
        return JOptionPane.showConfirmDialog(this, message, titre, JOptionPane.YES_NO_OPTION);
    }

    // Setter pour le contrôleur
    public void setControleur(ControleurRendezVous controleur) {
        this.controleur = controleur;
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
        System.out.println("getBtnValider() appelé - retourne: " + btnValider);
        return btnValider;
    }

    public JButton getBtnAnnuler() {
        System.out.println("getBtnAnnuler() appelé - retourne: " + btnAnnuler);
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