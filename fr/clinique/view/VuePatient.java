package fr.clinique.view;

import fr.clinique.model.Patient;
import fr.clinique.model.Role;
import fr.clinique.model.Utilisateur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class VuePatient extends JPanel {
    private Utilisateur utilisateur;

    private JTable tablePatients;
    private DefaultTableModel modelTable;
    private JTextField tfRecherche;
    private JButton btnRecherche, btnAjouter, btnModifier, btnSupprimer, btnRafraichir;

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
            btnModifier.setEnabled(false);
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
}