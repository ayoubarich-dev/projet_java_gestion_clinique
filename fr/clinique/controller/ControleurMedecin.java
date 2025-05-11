package fr.clinique.controller;

import fr.clinique.model.*;
import fr.clinique.view.VueMedecin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Contrôleur pour gérer les médecins.
 */
public class ControleurMedecin {
    private VueMedecin vue;

    /**
     * Constructeur qui initialise le contrôleur avec la vue médecin.
     * @param vue La vue médecin
     */
    public ControleurMedecin(VueMedecin vue) {
        this.vue = vue;

        System.out.println("Initialisation du contrôleur médecin");

        // Initialiser la vue avec les données
        chargerDonnees();

        // Ajouter les écouteurs d'événements
        ajouterEcouteurs();
    }

    /**
     * Charge les données des médecins dans la vue.
     */
    private void chargerDonnees() {
        try {
            System.out.println("Chargement des données des médecins...");
            List<Medecin> medecins = Medecin.afficherTousMedecins();
            System.out.println("Nombre de médecins chargés: " + medecins.size());
            vue.afficherDonnees(medecins);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des médecins: " + e.getMessage());
            e.printStackTrace();
            vue.afficherMessage("Erreur lors du chargement des médecins: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ajoute les écouteurs d'événements aux composants de la vue.
     */
    private void ajouterEcouteurs() {
        System.out.println("Ajout des écouteurs pour la vue médecin");

        // Bouton Ajouter
        if (vue.getBtnAjouter() != null) {
            vue.getBtnAjouter().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Ajouter médecin cliqué");
                    vue.afficherFormulaireAjout();
                    attacherEcouteursFormulaire();
                }
            });
        } else {
            System.err.println("Le bouton Ajouter est null");
        }

        // Bouton Modifier
        if (vue.getBtnModifier() != null) {
            vue.getBtnModifier().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Modifier médecin cliqué");
                    int selectedRow = vue.getTableMedecins().getSelectedRow();
                    if (selectedRow >= 0) {
                        int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                        vue.setIdMedecinSelectionne(id);
                        vue.afficherFormulaireModification(id);
                        attacherEcouteursFormulaire();
                    } else {
                        vue.afficherMessage("Veuillez sélectionner un médecin à modifier", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
        } else {
            System.err.println("Le bouton Modifier est null");
        }

        // Bouton Supprimer
        if (vue.getBtnSupprimer() != null) {
            vue.getBtnSupprimer().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Supprimer médecin cliqué");
                    int selectedRow = vue.getTableMedecins().getSelectedRow();
                    if (selectedRow >= 0) {
                        int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                        int confirm = vue.afficherConfirmation("Êtes-vous sûr de vouloir supprimer ce médecin ?", "Confirmation de suppression");

                        if (confirm == JOptionPane.YES_OPTION) {
                            Medecin medecin = MedecinModel.getMedecinById(id);

                            if (medecin != null) {
                                boolean result = medecin.supprimer();
                                if (result) {
                                    vue.afficherMessage("Médecin supprimé avec succès", "Suppression réussie", JOptionPane.INFORMATION_MESSAGE);
                                    chargerDonnees();
                                } else {
                                    vue.afficherMessage("Erreur lors de la suppression du médecin", "Erreur", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                vue.afficherMessage("Médecin introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        vue.afficherMessage("Veuillez sélectionner un médecin à supprimer", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
        } else {
            System.err.println("Le bouton Supprimer est null");
        }

        // Bouton Rechercher
        if (vue.getBtnRecherche() != null) {
            vue.getBtnRecherche().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Rechercher médecin cliqué");
                    String recherche = vue.getTfRecherche().getText().trim().toLowerCase();
                    if (!recherche.isEmpty()) {
                        // Recherche simple dans le tableau
                        vue.getModelTable().setRowCount(0);
                        List<Medecin> medecins = Medecin.afficherTousMedecins();
                        for (Medecin medecin : medecins) {
                            if (medecin.getNom().toLowerCase().contains(recherche) ||
                                    medecin.getPrenom().toLowerCase().contains(recherche) ||
                                    medecin.getSpecialite().toLowerCase().contains(recherche)) {

                                vue.getModelTable().addRow(new Object[] {
                                        medecin.getId(),
                                        medecin.getNom(),
                                        medecin.getPrenom(),
                                        medecin.getSpecialite(),
                                        medecin.getHoraires()
                                });
                            }
                        }

                        if (vue.getModelTable().getRowCount() == 0) {
                            vue.afficherMessage("Aucun médecin trouvé avec ces critères", "Recherche", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        vue.afficherMessage("Veuillez saisir un critère de recherche", "Recherche", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
        } else {
            System.err.println("Le bouton Rechercher est null");
        }

        // Bouton Rafraîchir
        if (vue.getBtnRafraichir() != null) {
            vue.getBtnRafraichir().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Rafraîchir médecin cliqué");
                    vue.getTfRecherche().setText("");
                    chargerDonnees();
                }
            });
        } else {
            System.err.println("Le bouton Rafraîchir est null");
        }

        // Double-clic sur une ligne du tableau
        if (vue.getTableMedecins() != null) {
            vue.getTableMedecins().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        System.out.println("Double-clic sur un médecin");
                        int selectedRow = vue.getTableMedecins().getSelectedRow();
                        if (selectedRow >= 0 && vue.getUtilisateur().getRole() == Role.ADMINISTRATEUR) {
                            int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                            vue.setIdMedecinSelectionne(id);
                            vue.afficherFormulaireModification(id);
                            attacherEcouteursFormulaire();
                        }
                    }
                }
            });
        } else {
            System.err.println("La table des médecins est null");
        }
    }

    /**
     * Attache les écouteurs aux boutons du formulaire.
     */
    public void attacherEcouteursFormulaire() {
        System.out.println("Attachement des écouteurs au formulaire médecin");

        if (vue.getBtnValider() != null) {
            System.out.println("Le bouton Valider existe");

            // Supprimer tous les écouteurs existants pour éviter les doublons
            for (ActionListener al : vue.getBtnValider().getActionListeners()) {
                vue.getBtnValider().removeActionListener(al);
                System.out.println("Suppression d'un écouteur existant sur le bouton Valider");
            }

            // Ajouter un nouvel écouteur
            vue.getBtnValider().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Valider du formulaire médecin cliqué");
                    validerFormulaire();
                }
            });

            System.out.println("Écouteur ajouté sur le bouton Valider");
        } else {
            System.err.println("Le bouton Valider du formulaire est null");
        }

        if (vue.getBtnAnnuler() != null) {
            System.out.println("Le bouton Annuler existe");

            // Supprimer tous les écouteurs existants pour éviter les doublons
            for (ActionListener al : vue.getBtnAnnuler().getActionListeners()) {
                vue.getBtnAnnuler().removeActionListener(al);
                System.out.println("Suppression d'un écouteur existant sur le bouton Annuler");
            }

            // Ajouter un nouvel écouteur
            vue.getBtnAnnuler().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Bouton Annuler du formulaire médecin cliqué");
                    if (vue.getDialogFormulaire() != null) {
                        vue.getDialogFormulaire().dispose();
                    } else {
                        System.err.println("Le dialogue du formulaire est null");
                    }
                }
            });

            System.out.println("Écouteur ajouté sur le bouton Annuler");
        } else {
            System.err.println("Le bouton Annuler du formulaire est null");
        }
    }

    /**
     * Valide le formulaire d'ajout ou de modification d'un médecin.
     */
    private void validerFormulaire() {
        System.out.println("Début de la validation du formulaire médecin");

        // Vérifier que tous les champs obligatoires sont remplis
        if (vue.getTfNom().getText().trim().isEmpty() ||
                vue.getTfPrenom().getText().trim().isEmpty() ||
                vue.getTfSpecialite().getText().trim().isEmpty() ||
                vue.getTfHoraires().getText().trim().isEmpty()) {

            vue.afficherMessage("Veuillez remplir tous les champs obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // En mode ajout, vérifier aussi les champs de connexion
        if (vue.isModeAjout() && (vue.getTfLogin().getText().trim().isEmpty() || vue.getTfPassword().getText().isEmpty())) {
            vue.afficherMessage("Veuillez remplir les champs de connexion", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean result;
        try {
            if (vue.isModeAjout()) {
                // Ajouter un nouveau médecin avec compte utilisateur
                System.out.println("Tentative d'ajout d'un nouveau médecin");
                System.out.println("Nom: " + vue.getTfNom().getText().trim());
                System.out.println("Prénom: " + vue.getTfPrenom().getText().trim());
                System.out.println("Login: " + vue.getTfLogin().getText().trim());
                System.out.println("Spécialité: " + vue.getTfSpecialite().getText().trim());
                System.out.println("Horaires: " + vue.getTfHoraires().getText().trim());

                Medecin medecin = new Medecin(
                        vue.getTfNom().getText().trim(),
                        vue.getTfPrenom().getText().trim(),
                        vue.getTfLogin().getText().trim(),
                        vue.getTfPassword().getText(),
                        vue.getTfSpecialite().getText().trim(),
                        vue.getTfHoraires().getText().trim()
                );

                result = medecin.enregistrer();

                if (result) {
                    System.out.println("Médecin ajouté avec succès");
                    vue.afficherMessage("Médecin ajouté avec succès", "Ajout réussi", JOptionPane.INFORMATION_MESSAGE);
                    if (vue.getDialogFormulaire() != null) {
                        vue.getDialogFormulaire().dispose();
                    }
                    chargerDonnees();
                } else {
                    System.err.println("Erreur lors de l'ajout du médecin");
                    vue.afficherMessage("Erreur lors de l'ajout du médecin", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Modifier un médecin existant
                System.out.println("Tentative de modification du médecin #" + vue.getIdMedecinSelectionne());
                Medecin medecin = MedecinModel.getMedecinById(vue.getIdMedecinSelectionne());

                if (medecin != null) {
                    medecin.setNom(vue.getTfNom().getText().trim());
                    medecin.setPrenom(vue.getTfPrenom().getText().trim());
                    medecin.setSpecialite(vue.getTfSpecialite().getText().trim());
                    medecin.setHoraires(vue.getTfHoraires().getText().trim());

                    result = medecin.enregistrer();

                    if (result) {
                        System.out.println("Médecin modifié avec succès");
                        vue.afficherMessage("Médecin modifié avec succès", "Modification réussie", JOptionPane.INFORMATION_MESSAGE);
                        if (vue.getDialogFormulaire() != null) {
                            vue.getDialogFormulaire().dispose();
                        }
                        chargerDonnees();
                    } else {
                        System.err.println("Erreur lors de la modification du médecin");
                        vue.afficherMessage("Erreur lors de la modification du médecin", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.err.println("Médecin introuvable");
                    vue.afficherMessage("Médecin introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            System.err.println("Exception lors de l'opération sur médecin: " + e.getMessage());
            e.printStackTrace();
            vue.afficherMessage("Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}