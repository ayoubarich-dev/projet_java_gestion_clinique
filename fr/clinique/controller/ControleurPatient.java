package fr.clinique.controller;

import fr.clinique.model.*;
import fr.clinique.view.VuePatient;
import fr.clinique.view.VuePrincipale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contrôleur pour gérer les patients.
 */
public class ControleurPatient {
    private VuePatient vue;

    /**
     * Constructeur qui initialise le contrôleur avec la vue patient.
     * @param vue La vue patient
     */
    public ControleurPatient(VuePatient vue) {
        this.vue = vue;

        // Initialiser la vue avec les données
        chargerDonnees();

        // Ajouter les écouteurs d'événements
        ajouterEcouteurs();
    }

    /**
     * Charge les données des patients dans la vue.
     */
    private void chargerDonnees() {
        try {
            List<Patient> patients;

            // Si l'utilisateur est un médecin, afficher seulement ses patients
            if (vue.getUtilisateur().getRole() == Role.MEDECIN) {
                // Trouver le médecin associé à cet utilisateur
                Medecin medecin = Medecin.rechercherParIdUtilisateur(vue.getUtilisateur().getId());

                if (medecin != null) {
                    // Récupérer uniquement les patients de ce médecin
                    patients = Patient.getPatientsParMedecin(medecin.getId());
                } else {
                    patients = new ArrayList<>(); // Liste vide si le médecin n'est pas trouvé
                }
            } else {
                // Pour les administrateurs et secrétaires, on affiche tous les patients
                patients = Patient.getAllPatients();
            }

            vue.afficherDonnees(patients);
        } catch (Exception e) {
            e.printStackTrace();
            vue.afficherMessage("Erreur lors du chargement des patients: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ajoute les écouteurs d'événements aux composants de la vue.
     */
    private void ajouterEcouteurs() {
        // Bouton Supprimer
        vue.getBtnSupprimer().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = vue.getTablePatients().getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                    int confirm = vue.afficherConfirmation("Êtes-vous sûr de vouloir supprimer ce patient ?", "Confirmation de suppression");

                    if (confirm == JOptionPane.YES_OPTION) {
                        Patient patient = new Patient();
                        patient.setId(id);
                        boolean result = patient.supprimer();

                        if (result) {
                            vue.afficherMessage("Patient supprimé avec succès", "Suppression réussie", JOptionPane.INFORMATION_MESSAGE);
                            chargerDonnees();
                        } else {
                            vue.afficherMessage("Erreur lors de la suppression du patient", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    vue.afficherMessage("Veuillez sélectionner un patient à supprimer", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Bouton Rafraîchir
        vue.getBtnRafraichir().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vue.getTfRecherche().setText("");
                chargerDonnees();
            }
        });

        // Bouton Ajouter - redirige vers la vue principale pour afficher le formulaire d'ajout
        vue.getBtnAjouter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ce bouton est géré par la VuePrincipale
                java.awt.Window window = SwingUtilities.getWindowAncestor(vue);
                if (window instanceof VuePrincipale) {
                    ((VuePrincipale) window).showAddPatientForm();
                }
            }
        });

        // Bouton Modifier - redirige vers la vue principale pour afficher le formulaire de modification
        vue.getBtnModifier().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = vue.getTablePatients().getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                    java.awt.Window window = SwingUtilities.getWindowAncestor(vue);
                    if (window instanceof VuePrincipale) {
                        ((VuePrincipale) window).showEditPatientForm(id);
                    }
                } else {
                    vue.afficherMessage("Veuillez sélectionner un patient à modifier", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Bouton Rechercher
        vue.getBtnRecherche().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numeroRecherche = vue.getTfRecherche().getText().trim();
                if (!numeroRecherche.isEmpty()) {
                    Patient patient = Patient.rechercherParNumeroDossier(numeroRecherche);
                    if (patient != null) {
                        vue.getModelTable().setRowCount(0);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        vue.getModelTable().addRow(new Object[] {
                                patient.getId(),
                                patient.getNom(),
                                patient.getPrenom(),
                                sdf.format(patient.getDateNaissance()),
                                patient.getTelephone(),
                                patient.getNumeroDossier()
                        });
                    } else {
                        vue.afficherMessage("Aucun patient trouvé avec ce numéro de dossier", "Recherche", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    chargerDonnees(); // Si la recherche est vide, afficher tous les patients
                }
            }
        });
    }


    /**
     * Ajoute un nouveau patient.
     * @param nom Le nom du patient
     * @param prenom Le prénom du patient
     * @param dateNaissance La date de naissance du patient
     * @param telephone Le numéro de téléphone du patient
     * @return true si l'ajout a réussi, false sinon
     */
    public static boolean ajouterPatient(String nom, String prenom, Date dateNaissance, String telephone) {
        Patient patient = new Patient(nom, prenom, dateNaissance, telephone);
        return patient.enregistrer();
    }

    // Méthode publique pour pouvoir être appelée depuis VuePrincipale
    public void attacherEcouteursFormulaire() {
        // Cette méthode n'est plus nécessaire car les formulaires sont gérés dans VuePrincipale
        // Mais on la garde pour la compatibilité
    }
}