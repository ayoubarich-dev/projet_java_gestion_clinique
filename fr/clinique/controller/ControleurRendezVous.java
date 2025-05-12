package fr.clinique.controller;

import fr.clinique.model.*;
import fr.clinique.observer.NotificationManager;
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
     * Constructeur par défaut pour les appels statiques
     */
    public ControleurRendezVous() {
        // Constructeur par défaut
    }

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
                rendezVousList = RendezVous.getRendezVousParMedecin(medecin.getId());
            } else {
                rendezVousList = new ArrayList<>(); // Liste vide si le médecin n'est pas trouvé
            }
        } else {
            // Pour les administrateurs et secrétaires, on affiche tous les rendez-vous
            rendezVousList = RendezVous.getTousRendezVous();
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
                System.out.println("Bouton Ajouter cliqué");
                afficherFormulaireAjout();
            }
        });

        // Bouton Modifier
        vue.getBtnModifier().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = vue.getTableRendezVous().getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                    afficherFormulaireModification(id);
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
                        boolean result = supprimerRendezVous(id);
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
                        afficherFormulaireModification(id);
                    }
                }
            }
        });
    }

    private void afficherFormulaireAjout() {
        System.out.println("=== Début ajout rendez-vous ===");

        try {
            // Récupérer les listes de patients et médecins
            List<Patient> patients = Patient.getAllPatients();
            List<Medecin> medecins = Medecin.getAllMedecins();

            System.out.println("Nombre de patients récupérés: " + patients.size());
            System.out.println("Nombre de médecins récupérés: " + medecins.size());

            // Debug: afficher les détails des patients
            for (Patient p : patients) {
                System.out.println("Patient: " + p.getId() + " - " + p.getNom() + " " + p.getPrenom());
            }

            // Debug: afficher les détails des médecins
            for (Medecin m : medecins) {
                System.out.println("Médecin: " + m.getId() + " - " + m.getNom() + " " + m.getPrenom() + " (" + m.getSpecialite() + ")");
            }

            // Vérifier qu'il y a des patients et médecins disponibles
            if (patients.isEmpty()) {
                vue.afficherMessage("Aucun patient enregistré. Veuillez d'abord ajouter un patient.", "Aucun patient", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (medecins.isEmpty()) {
                vue.afficherMessage("Aucun médecin enregistré. Veuillez d'abord ajouter un médecin.", "Aucun médecin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Créer le formulaire AVANT de remplir les comboboxes
            vue.setModeAjout(true);
            vue.creerFormulairePublic(); // Utiliser la méthode publique

            // Maintenant remplir les comboboxes
            vue.remplirComboBoxes(patients, medecins);

            // Réinitialiser les champs
            vue.getFtfDate().setText("");
            vue.getTfHeure().setText("");
            vue.getTfMotif().setText("");

            // Afficher le formulaire
            vue.getDialogFormulaire().setTitle("Ajouter un rendez-vous");
            vue.getDialogFormulaire().setVisible(true);

            System.out.println("Formulaire d'ajout affiché avec les données");

            // Attacher les écouteurs après l'affichage
            attacherEcouteursFormulaire();

        } catch (Exception ex) {
            System.err.println("Erreur lors de l'affichage du formulaire: " + ex.getMessage());
            ex.printStackTrace();
            vue.afficherMessage("Erreur lors du chargement des données: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void afficherFormulaireModification(int id) {
        try {
            // Récupérer les listes de patients et médecins
            List<Patient> patients = Patient.getAllPatients();
            List<Medecin> medecins = Medecin.getAllMedecins();

            // Récupérer le rendez-vous
            RendezVous rendezVous = RendezVous.getRendezVousById(id);
            if (rendezVous == null) {
                vue.afficherMessage("Rendez-vous introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Créer le formulaire AVANT de remplir les comboboxes
            vue.setModeAjout(false);
            vue.setIdRendezVousSelectionne(id);
            vue.creerFormulairePublic(); // Utiliser la méthode publique

            // Maintenant remplir les comboboxes
            vue.remplirComboBoxes(patients, medecins);

            // Remplir le formulaire avec les données du rendez-vous
            vue.remplirFormulaireModification(rendezVous);

            // Afficher le formulaire
            vue.getDialogFormulaire().setTitle("Modifier un rendez-vous");
            vue.getDialogFormulaire().setVisible(true);

            // Attacher les écouteurs après l'affichage
            attacherEcouteursFormulaire();

        } catch (Exception ex) {
            ex.printStackTrace();
            vue.afficherMessage("Erreur lors du chargement: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Valide le formulaire d'ajout ou de modification d'un rendez-vous.
     */
    public void validerFormulaire() {
        System.out.println("Début de la validation du formulaire rendez-vous");

        try {
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

            System.out.println("Patient sélectionné: " + patient.getNom() + " " + patient.getPrenom());
            System.out.println("Médecin sélectionné: " + medecin.getNom() + " " + medecin.getPrenom());

            boolean result;
            if (vue.isModeAjout()) {
                // Ajouter un nouveau rendez-vous
                System.out.println("Tentative d'ajout d'un nouveau rendez-vous");
                result = ajouterRendezVous(patient.getId(), medecin.getId(), date, heure, motif);

                if (result) {
                    System.out.println("Rendez-vous ajouté avec succès");
                    vue.afficherMessage("Rendez-vous ajouté avec succès", "Ajout réussi", JOptionPane.INFORMATION_MESSAGE);
                    vue.getDialogFormulaire().dispose();
                    chargerDonnees(); // Rafraîchir la liste
                } else {
                    System.out.println("Erreur lors de l'ajout du rendez-vous");
                    vue.afficherMessage("Erreur lors de l'ajout du rendez-vous", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Modifier un rendez-vous existant
                System.out.println("Tentative de modification du rendez-vous #" + vue.getIdRendezVousSelectionne());
                result = modifierRendezVous(vue.getIdRendezVousSelectionne(), patient.getId(), medecin.getId(), date, heure, motif);

                if (result) {
                    System.out.println("Rendez-vous modifié avec succès");
                    vue.afficherMessage("Rendez-vous modifié avec succès", "Modification réussie", JOptionPane.INFORMATION_MESSAGE);
                    vue.getDialogFormulaire().dispose();
                    chargerDonnees(); // Rafraîchir la liste
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

    // Méthodes de gestion des rendez-vous
    public boolean ajouterRendezVous(int idPatient, int idMedecin, Date date, String heure, String motif) {
        boolean result = RendezVous.ajouterRendezVous(idPatient, idMedecin, date, heure, motif);

        if (result) {
            // Notifier les changements
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Patient patient = Patient.getPatientById(idPatient);
            Medecin medecin = Medecin.getMedecinById(idMedecin);

            if (patient != null && medecin != null) {
                // Ajouter un message spécifique pour ce médecin
                String messageSpecifique = "Nouveau patient affecté: " +
                        patient.getPrenom() + " " + patient.getNom() +
                        " le " + sdf.format(date) + " à " + heure;
                Medecin.ajouterMessagePourMedecin(medecin.getId(), messageSpecifique);

                // Notification générale
                NotificationManager.getInstance().ajouterNotification(
                        "Nouveau rendez-vous créé pour le Dr. " + medecin.getPrenom() + " " +
                                medecin.getNom() + " avec " + patient.getPrenom() + " " +
                                patient.getNom() + " le " + sdf.format(date) + " à " + heure
                );
            }
        }

        return result;
    }

    public boolean modifierRendezVous(int id, int idPatient, int idMedecin, Date date, String heure, String motif) {
        boolean result = RendezVous.modifierRendezVous(id, idPatient, idMedecin, date, heure, motif);

        if (result) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Patient patient = Patient.getPatientById(idPatient);
            Medecin medecin = Medecin.getMedecinById(idMedecin);

            if (patient != null && medecin != null) {
                // Ajouter une notification
                NotificationManager.getInstance().ajouterNotification(
                        "Rendez-vous modifié pour Dr. " + medecin.getPrenom() + " " +
                                medecin.getNom() + " avec " + patient.getPrenom() + " " +
                                patient.getNom() + " le " + sdf.format(date) + " à " + heure
                );
            }
        }

        return result;
    }

    public boolean supprimerRendezVous(int id) {
        return RendezVous.supprimerRendezVous(id);
    }

    public List<RendezVous> afficherTousRendezVous() {
        return RendezVous.getTousRendezVous();
    }

    public List<RendezVous> afficherRendezVousParMedecin(int idMedecin) {
        return RendezVous.getRendezVousParMedecin(idMedecin);
    }

    public void attacherEcouteursFormulaire() {
        System.out.println("=== Attachement des écouteurs du formulaire ===");

        // Attendre un peu pour s'assurer que le formulaire est complètement initialisé
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Vérifier si les boutons du formulaire existent
        if (vue.getBtnValider() != null) {
            System.out.println("Bouton Valider trouvé - suppression de TOUS les écouteurs existants");

            // Supprimer TOUS les écouteurs existants, y compris les temporaires
            ActionListener[] listeners = vue.getBtnValider().getActionListeners();
            System.out.println("Nombre d'écouteurs actuels sur Valider: " + listeners.length);

            for (ActionListener al : listeners) {
                vue.getBtnValider().removeActionListener(al);
            }

            // Ajouter le nouvel écouteur
            ActionListener validerListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Valider cliqué - appel à validerFormulaire()");
                    validerFormulaire();
                }
            };

            vue.getBtnValider().addActionListener(validerListener);

            System.out.println("Écouteur attaché au bouton Valider - Nombre d'écouteurs après ajout: " +
                    vue.getBtnValider().getActionListeners().length);
        } else {
            System.err.println("ERREUR: getBtnValider() retourne null!");
        }

        if (vue.getBtnAnnuler() != null) {
            System.out.println("Bouton Annuler trouvé - suppression de TOUS les écouteurs existants");

            // Supprimer TOUS les écouteurs existants, y compris les temporaires
            ActionListener[] listeners = vue.getBtnAnnuler().getActionListeners();
            System.out.println("Nombre d'écouteurs actuels sur Annuler: " + listeners.length);

            for (ActionListener al : listeners) {
                vue.getBtnAnnuler().removeActionListener(al);
            }

            // Ajouter le nouvel écouteur
            ActionListener annulerListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Annuler cliqué - fermeture du formulaire");
                    if (vue.getDialogFormulaire() != null) {
                        vue.getDialogFormulaire().dispose();
                    }
                }
            };

            vue.getBtnAnnuler().addActionListener(annulerListener);

            System.out.println("Écouteur attaché au bouton Annuler - Nombre d'écouteurs après ajout: " +
                    vue.getBtnAnnuler().getActionListeners().length);
        } else {
            System.err.println("ERREUR: getBtnAnnuler() retourne null!");
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
            if (!cheminFichier.toLowerCase().endsWith(".xlsx")) {
                cheminFichier += ".xlsx";
            }

            try {
                List<RendezVous> rendezVousList = RendezVous.getTousRendezVous();
                boolean result = RendezVous.exporterExcel(rendezVousList, cheminFichier);

                if (result) {
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
            if (!cheminFichier.toLowerCase().endsWith(".pdf")) {
                cheminFichier += ".pdf";
            }

            try {
                List<RendezVous> rendezVousList = RendezVous.getTousRendezVous();
                boolean result = RendezVous.exporterPDF(rendezVousList, cheminFichier);

                if (result) {
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
