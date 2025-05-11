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
    private JButton btnAjouter, btnSupprimer, btnRafraichir;

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
        btnSupprimer = new JButton("Supprimer");
        btnRafraichir = new JButton("Rafraîchir");

        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnSupprimer);
        panelBoutons.add(btnRafraichir);

        // Ajout des composants au panel principal
        add(lblTitre, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBoutons, BorderLayout.SOUTH);
        
        // Cette vue n'est accessible qu'aux administrateurs, donc pas besoin de vérifier les droits
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
    
    public JButton getBtnSupprimer() {
        return btnSupprimer;
    }
    
    public JButton getBtnRafraichir() {
        return btnRafraichir;
    }
}