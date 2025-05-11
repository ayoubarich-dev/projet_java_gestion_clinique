package fr.clinique.controller;

import fr.clinique.model.*;
import fr.clinique.view.VuePatient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

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

        System.out.println("Initialisation du contrôleur patient");

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
            System.out.println("Chargement des données des patients...");
            List<Patient> patients;

            // Si l'utilisateur est un médecin, afficher seulement ses patients
            if (vue.getUtilisateur().getRole() == Role.MEDECIN) {
                // Trouver le médecin associé à cet utilisateur
                Medecin medecin = Medecin.rechercherParIdUtilisateur(vue.getUtilisateur().getId());

                if (medecin != null) {
                    // Récupérer uniquement les patients de ce médecin
                    patients = Patient.afficherParMedecin(medecin.getId());
                    System.out.println("Nombre de patients pour le médecin " + medecin.getId() + ": " + patients.size());
                } else {
                    System.out.println("Médecin non trouvé pour l'utilisateur " + vue.getUtilisateur().getId());
                    patients = new ArrayList<>(); // Liste vide si le médecin n'est pas trouvé
                }
            } else {
                // Pour les administrateurs et secrétaires, on affiche tous les patients
                System.out.println("Chargement de tous les patients...");
                patients = Patient.getAllPatients();
                System.out.println("Nombre total de patients: " + patients.size());
            }

            vue.afficherDonnees(patients);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des patients: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(vue,
                    "Erreur lors du chargement des patients: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ajoute les écouteurs d'événements aux composants de la vue.
     */
    private void ajouterEcouteurs() {
        // Bouton Ajouter
        vue.getBtnAjouter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Bouton Ajouter patient cliqué");
                vue.afficherFormulaireAjout();
                attacherEcouteursFormulaire();
            }
        });

        // Bouton Modifier
        vue.getBtnModifier().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Bouton Modifier patient cliqué");
                int selectedRow = vue.getTablePatients().getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                    vue.setIdPatientSelectionne(id);
                    vue.afficherFormulaireModification(id);
                    attacherEcouteursFormulaire();
                } else {
                    vue.afficherMessage("Veuillez sélectionner un patient à modifier", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Bouton Supprimer
        vue.getBtnSupprimer().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Bouton Supprimer patient cliqué");
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

        // Bouton Rechercher
        vue.getBtnRecherche().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Bouton Rechercher patient cliqué");
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
                    vue.afficherMessage("Veuillez saisir un numéro de dossier", "Recherche", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Bouton Rafraîchir
        vue.getBtnRafraichir().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Bouton Rafraîchir patient cliqué");
                vue.getTfRecherche().setText("");
                chargerDonnees();
            }
        });

        // Double-clic sur une ligne du tableau
        vue.getTablePatients().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println("Double-clic sur un patient");
                    int selectedRow = vue.getTablePatients().getSelectedRow();
                    if (selectedRow >= 0) {
                        int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                        vue.setIdPatientSelectionne(id);
                        vue.afficherFormulaireModification(id);
                        attacherEcouteursFormulaire();
                    }
                }
            }
        });
    }

    /**
     * Attache les écouteurs aux boutons du formulaire.
     * Cette méthode doit être publique pour être accessible depuis VuePrincipale.
     */
    public void attacherEcouteursFormulaire() {
        System.out.println("Attachement des écouteurs au formulaire patient");

        if (vue.getBtnValider() != null) {
            // Supprimer tous les écouteurs existants
            for (ActionListener al : vue.getBtnValider().getActionListeners()) {
                vue.getBtnValider().removeActionListener(al);
            }

            // Ajouter un nouvel écouteur
            vue.getBtnValider().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Valider du formulaire patient cliqué");
                    validerFormulaire();
                }
            });
        } else {
            System.err.println("Le bouton Valider du formulaire est null");
        }

        if (vue.getBtnAnnuler() != null) {
            // Supprimer tous les écouteurs existants
            for (ActionListener al : vue.getBtnAnnuler().getActionListeners()) {
                vue.getBtnAnnuler().removeActionListener(al);
            }

            // Ajouter un nouvel écouteur
            vue.getBtnAnnuler().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Annuler du formulaire patient cliqué");
                    if (vue.getDialogFormulaire() != null) {
                        vue.getDialogFormulaire().dispose();
                    }
                }
            });
        } else {
            System.err.println("Le bouton Annuler du formulaire est null");
        }
    }

    /**
     * Valide le formulaire d'ajout ou de modification d'un patient.
     */
    private void validerFormulaire() {
        System.out.println("Début de la validation du formulaire patient");

        // Vérifier que tous les champs sont remplis
        if (vue.getTfNom().getText().trim().isEmpty() ||
                vue.getTfPrenom().getText().trim().isEmpty() ||
                vue.getFtfDateNaissance().getText().trim().isEmpty() ||
                vue.getTfTelephone().getText().trim().isEmpty()) {

            vue.afficherMessage("Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier le format de la date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        Date dateNaissance;

        try {
            dateNaissance = sdf.parse(vue.getFtfDateNaissance().getText());
        } catch (ParseException e) {
            vue.afficherMessage("Format de date invalide. Utilisez JJ/MM/AAAA", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean result;
        try {
            if (vue.isModeAjout()) {
                // Ajouter un nouveau patient
                System.out.println("Tentative d'ajout d'un nouveau patient");
                System.out.println("Nom: " + vue.getTfNom().getText().trim());
                System.out.println("Prénom: " + vue.getTfPrenom().getText().trim());
                System.out.println("Date de naissance: " + vue.getFtfDateNaissance().getText().trim());
                System.out.println("Téléphone: " + vue.getTfTelephone().getText().trim());

                Patient patient = new Patient(
                        vue.getTfNom().getText().trim(),
                        vue.getTfPrenom().getText().trim(),
                        dateNaissance,
                        vue.getTfTelephone().getText().trim()
                );

                result = patient.enregistrer();

                if (result) {
                    System.out.println("Patient ajouté avec succès");
                    vue.afficherMessage("Patient ajouté avec succès", "Ajout réussi", JOptionPane.INFORMATION_MESSAGE);
                    vue.getDialogFormulaire().dispose();
                    chargerDonnees(); // Recharger la liste des patients
                } else {
                    System.out.println("Erreur lors de l'ajout du patient");
                    vue.afficherMessage("Erreur lors de l'ajout du patient", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Modifier un patient existant
                System.out.println("Tentative de modification du patient #" + vue.getIdPatientSelectionne());
                Patient patient = new Patient();
                Personne personne = patient.rechercherParId(vue.getIdPatientSelectionne());

                if (personne instanceof Patient) {
                    patient = (Patient) personne;
                    patient.setNom(vue.getTfNom().getText().trim());
                    patient.setPrenom(vue.getTfPrenom().getText().trim());
                    patient.setDateNaissance(dateNaissance);
                    patient.setTelephone(vue.getTfTelephone().getText().trim());

                    result = patient.enregistrer();

                    if (result) {
                        System.out.println("Patient modifié avec succès");
                        vue.afficherMessage("Patient modifié avec succès", "Modification réussie", JOptionPane.INFORMATION_MESSAGE);
                        vue.getDialogFormulaire().dispose();
                        chargerDonnees(); // Recharger la liste des patients
                    } else {
                        System.out.println("Erreur lors de la modification du patient");
                        vue.afficherMessage("Erreur lors de la modification du patient", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println("Patient introuvable");
                    vue.afficherMessage("Patient introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            System.err.println("Exception lors de l'opération sur patient: " + e.getMessage());
            e.printStackTrace();
            vue.afficherMessage("Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}