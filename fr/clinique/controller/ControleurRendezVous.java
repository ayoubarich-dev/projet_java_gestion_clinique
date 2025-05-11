package fr.clinique.controller;

import fr.clinique.model.*;
import fr.clinique.observer.NotificationManager;
import fr.clinique.util.ExcelExporter;
import fr.clinique.util.PDFExporter;
import fr.clinique.view.VueRendezVous;

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
 * Contrôleur pour gérer les rendez-vous.
 */
public class ControleurRendezVous {
    private VueRendezVous vue;

    /**
     * Constructeur qui initialise le contrôleur avec la vue rendez-vous.
     * @param vue La vue rendez-vous
     */
    public ControleurRendezVous(VueRendezVous vue) {
        this.vue = vue;

        System.out.println("Initialisation du contrôleur rendez-vous");

        // Initialiser la vue avec les données
        chargerDonnees();

        // Ajouter les écouteurs d'événements
        ajouterEcouteurs();

        // Réattacher les écouteurs aux boutons du formulaire
        attacherEcouteursFormulaire();
    }

    /**
     * Charge les données des rendez-vous dans la vue.
     */
    private void chargerDonnees() {
        List<RendezVous> rendezVousList;

        // Selon le rôle, on charge tous les rendez-vous ou seulement ceux du médecin
        if (vue.getUtilisateur().getRole() == Role.MEDECIN) {
            // Trouver le médecin associé à cet utilisateur
            Medecin medecin = Medecin.rechercherParIdUtilisateur(vue.getUtilisateur().getId());

            if (medecin != null) {
                // Récupérer uniquement les rendez-vous de ce médecin
                rendezVousList = RendezVousModel.getRendezVousParMedecin(medecin.getId());
            } else {
                rendezVousList = new ArrayList<>(); // Liste vide si le médecin n'est pas trouvé
            }
        } else {
            // Pour les administrateurs et secrétaires, on affiche tous les rendez-vous
            rendezVousList = RendezVousModel.getTousRendezVous();
        }

        vue.afficherDonnees(rendezVousList);
    }

    /**
     * Ajoute les écouteurs d'événements aux composants de la vue.
     */
    private void ajouterEcouteurs() {
        // Bouton Ajouter
        vue.getBtnAjouter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Patient> patients = Patient.getAllPatients();
                List<Medecin> medecins = Medecin.afficherTousMedecins();

                // Vérifier qu'il y a des patients et médecins disponibles
                if (patients.isEmpty()) {
                    vue.afficherMessage("Aucun patient enregistré. Veuillez d'abord ajouter un patient.", "Aucun patient", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (medecins.isEmpty()) {
                    vue.afficherMessage("Aucun médecin enregistré. Veuillez d'abord ajouter un médecin.", "Aucun médecin", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                vue.remplirComboBoxes(patients, medecins);
                vue.afficherFormulaireAjout();

                // Réattacher les écouteurs aux boutons du formulaire
                attacherEcouteursFormulaire();
            }
        });

        // Bouton Modifier
        vue.getBtnModifier().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = vue.getTableRendezVous().getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                    vue.setIdRendezVousSelectionne(id);

                    // Récupérer les listes de patients et médecins
                    List<Patient> patients = Patient.getAllPatients();
                    List<Medecin> medecins = Medecin.afficherTousMedecins();
                    vue.remplirComboBoxes(patients, medecins);

                    // Récupérer le rendez-vous
                    RendezVous rendezVous = RendezVousModel.getRendezVousById(id);
                    if (rendezVous != null) {
                        vue.afficherFormulaireModification(id);
                        vue.remplirFormulaireModification(rendezVous);
                    } else {
                        vue.afficherMessage("Rendez-vous introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    vue.afficherMessage("Veuillez sélectionner un rendez-vous à modifier", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Bouton Supprimer
        vue.getBtnSupprimer().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = vue.getTableRendezVous().getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                    int confirm = vue.afficherConfirmation("Êtes-vous sûr de vouloir supprimer ce rendez-vous ?", "Confirmation de suppression");

                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean result = RendezVousModel.supprimerRendezVous(id);
                        if (result) {
                            vue.afficherMessage("Rendez-vous supprimé avec succès", "Suppression réussie", JOptionPane.INFORMATION_MESSAGE);
                            chargerDonnees();
                        } else {
                            vue.afficherMessage("Erreur lors de la suppression du rendez-vous", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    vue.afficherMessage("Veuillez sélectionner un rendez-vous à supprimer", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Bouton Exporter Excel
        vue.getBtnExporterExcel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exporterExcel();
            }
        });

        // Bouton Exporter PDF
        vue.getBtnExporterPDF().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exporterPDF();
            }
        });

        // Double-clic sur une ligne du tableau
        vue.getTableRendezVous().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = vue.getTableRendezVous().getSelectedRow();
                    if (selectedRow >= 0) {
                        int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                        vue.setIdRendezVousSelectionne(id);

                        // Récupérer les listes de patients et médecins
                        List<Patient> patients = Patient.getAllPatients();
                        List<Medecin> medecins = Medecin.afficherTousMedecins();
                        vue.remplirComboBoxes(patients, medecins);

                        // Récupérer le rendez-vous
                        RendezVous rendezVous = RendezVousModel.getRendezVousById(id);
                        if (rendezVous != null) {
                            vue.afficherFormulaireModification(id);
                            vue.remplirFormulaireModification(rendezVous);
                        }
                    }
                }
            }
        });

        // Bouton Valider du formulaire
        if (vue.getBtnValider() != null) {
            vue.getBtnValider().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    validerFormulaire();
                }
            });
        }

        // Bouton Annuler du formulaire
        if (vue.getBtnAnnuler() != null) {
            vue.getBtnAnnuler().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    vue.getDialogFormulaire().dispose();
                }
            });
        }
    }

    /**
     * Valide le formulaire d'ajout ou de modification d'un rendez-vous.
     */
    /**
     * Valide le formulaire d'ajout ou de modification d'un rendez-vous.
     */
    private void validerFormulaire() {
        System.out.println("Début de la validation du formulaire rendez-vous");

        // Vérifier que tous les champs sont remplis
        if (vue.getCbPatient().getSelectedIndex() == -1 ||
                vue.getCbMedecin().getSelectedIndex() == -1 ||
                vue.getFtfDate().getText().trim().isEmpty() ||
                vue.getTfHeure().getText().trim().isEmpty()) {

            vue.afficherMessage("Veuillez remplir tous les champs obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier le format de la date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        Date date;

        try {
            date = sdf.parse(vue.getFtfDate().getText());
        } catch (ParseException e) {
            vue.afficherMessage("Format de date invalide. Utilisez JJ/MM/AAAA", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier le format de l'heure (HH:MM)
        String heure = vue.getTfHeure().getText().trim();
        if (!heure.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            vue.afficherMessage("Format d'heure invalide. Utilisez HH:MM", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Patient patient = (Patient) vue.getCbPatient().getSelectedItem();
        Medecin medecin = (Medecin) vue.getCbMedecin().getSelectedItem();
        String motif = vue.getTfMotif().getText().trim();

        boolean result;
        try {
            if (vue.isModeAjout()) {
                // Ajouter un nouveau rendez-vous
                System.out.println("Tentative d'ajout d'un nouveau rendez-vous");
                result = RendezVousModel.ajouterRendezVous(patient.getId(), medecin.getId(), date, heure, motif);

                if (result) {
                    System.out.println("Rendez-vous ajouté avec succès");
                    // Ajouter un message spécifique pour ce médecin
                    String messageSpecifique = "Nouveau patient affecté: " +
                            patient.getPrenom() + " " + patient.getNom() +
                            " le " + sdf.format(date) + " à " + heure;
                    Medecin.ajouterMessagePourMedecin(medecin.getId(), messageSpecifique);

                    // Notification générale
                    NotificationManager.getInstance().ajouterNotification(
                            "Nouveau rendez-vous créé pour le Dr. " + medecin.getPrenom() + " " +
                                    medecin.getNom() + " avec " + patient.getPrenom() + " " +
                                    patient.getNom() + " le " + vue.getFtfDate().getText() + " à " + heure
                    );

                    vue.afficherMessage("Rendez-vous ajouté avec succès", "Ajout réussi", JOptionPane.INFORMATION_MESSAGE);
                    vue.getDialogFormulaire().dispose();
                    chargerDonnees(); // Recharger la liste des rendez-vous
                } else {
                    System.out.println("Erreur lors de l'ajout du rendez-vous");
                    vue.afficherMessage("Erreur lors de l'ajout du rendez-vous", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Modifier un rendez-vous existant
                System.out.println("Tentative de modification du rendez-vous #" + vue.getIdRendezVousSelectionne());
                result = RendezVousModel.modifierRendezVous(vue.getIdRendezVousSelectionne(), patient.getId(), medecin.getId(), date, heure, motif);

                if (result) {
                    System.out.println("Rendez-vous modifié avec succès");
                    // Ajouter une notification
                    NotificationManager.getInstance().ajouterNotification(
                            "Rendez-vous modifié pour Dr. " + medecin.getPrenom() + " " +
                                    medecin.getNom() + " avec " + patient.getPrenom() + " " +
                                    patient.getNom() + " le " + sdf.format(date) + " à " + heure
                    );

                    vue.afficherMessage("Rendez-vous modifié avec succès", "Modification réussie", JOptionPane.INFORMATION_MESSAGE);
                    vue.getDialogFormulaire().dispose();
                    chargerDonnees(); // Recharger la liste des rendez-vous
                } else {
                    System.out.println("Erreur lors de la modification du rendez-vous");
                    vue.afficherMessage("Erreur lors de la modification du rendez-vous", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            System.err.println("Exception lors de l'opération sur rendez-vous: " + e.getMessage());
            e.printStackTrace();
            vue.afficherMessage("Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void attacherEcouteursFormulaire() {
        // Vérifier si les boutons du formulaire existent
        if (vue.getBtnValider() != null) {
            // Supprimer tous les écouteurs existants pour éviter les doublons
            for (ActionListener al : vue.getBtnValider().getActionListeners()) {
                vue.getBtnValider().removeActionListener(al);
            }

            // Ajouter le nouvel écouteur
            vue.getBtnValider().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Valider cliqué - appel à validerFormulaire()");
                    validerFormulaire();
                }
            });
        }

        if (vue.getBtnAnnuler() != null) {
            // Supprimer tous les écouteurs existants pour éviter les doublons
            for (ActionListener al : vue.getBtnAnnuler().getActionListeners()) {
                vue.getBtnAnnuler().removeActionListener(al);
            }

            // Ajouter le nouvel écouteur
            vue.getBtnAnnuler().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Annuler cliqué - fermeture du formulaire");
                    if (vue.getDialogFormulaire() != null) {
                        vue.getDialogFormulaire().dispose();
                    }
                }
            });
        }
    }
    /**
     * Exporte les rendez-vous au format Excel.
     */
    private void exporterExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les rendez-vous en Excel");

        int userSelection = fileChooser.showSaveDialog(vue);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String cheminFichier = fileChooser.getSelectedFile().getAbsolutePath();
            // Ajouter l'extension si nécessaire
            if (!cheminFichier.toLowerCase().endsWith(".xlsx")) {
                cheminFichier += ".xlsx";
            }

            // Récupérer la liste des rendez-vous
            List<RendezVous> rendezVousList = RendezVousModel.getTousRendezVous();

            try {
                boolean result = RendezVousModel.exporterExcel(rendezVousList, cheminFichier);

                if (result) {
                    // Ajouter une notification
                    NotificationManager.getInstance().ajouterNotification(
                            "Les rendez-vous ont été exportés en Excel dans le fichier: " + cheminFichier
                    );

                    vue.afficherMessage("Les rendez-vous ont été exportés avec succès en Excel.", "Exportation réussie", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    vue.afficherMessage("Une erreur est survenue lors de l'exportation.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                vue.afficherMessage("Erreur: " + e.getMessage(), "Erreur d'exportation", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Exporte les rendez-vous au format PDF.
     */
    private void exporterPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les rendez-vous en PDF");

        int userSelection = fileChooser.showSaveDialog(vue);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String cheminFichier = fileChooser.getSelectedFile().getAbsolutePath();
            // Ajouter l'extension si nécessaire
            if (!cheminFichier.toLowerCase().endsWith(".pdf")) {
                cheminFichier += ".pdf";
            }

            // Récupérer la liste des rendez-vous
            List<RendezVous> rendezVousList = RendezVousModel.getTousRendezVous();

            try {
                boolean result = RendezVousModel.exporterPDF(rendezVousList, cheminFichier);

                if (result) {
                    // Ajouter une notification
                    NotificationManager.getInstance().ajouterNotification(
                            "Les rendez-vous ont été exportés en PDF dans le fichier: " + cheminFichier
                    );

                    vue.afficherMessage("Les rendez-vous ont été exportés avec succès en PDF.", "Exportation réussie", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    vue.afficherMessage("Une erreur est survenue lors de l'exportation.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                vue.afficherMessage("Erreur: " + e.getMessage(), "Erreur d'exportation", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}