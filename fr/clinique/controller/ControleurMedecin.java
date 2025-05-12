package fr.clinique.controller;

import fr.clinique.model.Medecin;
import fr.clinique.model.Utilisateur;
import fr.clinique.view.VueMedecin;
import fr.clinique.view.VuePrincipale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        // Initialiser la vue avec les données
        chargerDonnees();

        // Ajouter les écouteurs d'événements
        ajouterEcouteurs();
    }

    /**
     * Charge les données des médecins dans la vue.
     */
    private void chargerDonnees() {
        List<Medecin> medecins = Medecin.getAllMedecins();
        vue.afficherDonnees(medecins);
    }

    /**
     * Ajoute les écouteurs d'événements aux composants de la vue.
     */
    private void ajouterEcouteurs() {
        // Bouton Supprimer
        vue.getBtnSupprimer().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = vue.getTableMedecins().getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                    int confirm = vue.afficherConfirmation("Êtes-vous sûr de vouloir supprimer ce médecin ?", "Confirmation de suppression");

                    if (confirm == JOptionPane.YES_OPTION) {
                        Utilisateur medecin = new Medecin();
                        medecin.setId(id);
                        boolean result = medecin.supprimer();

                        if (result) {
                            vue.afficherMessage("Médecin supprimé avec succès", "Suppression réussie", JOptionPane.INFORMATION_MESSAGE);
                            chargerDonnees();
                        } else {
                            vue.afficherMessage("Erreur lors de la suppression du médecin", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    vue.afficherMessage("Veuillez sélectionner un médecin à supprimer", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Bouton Rafraîchir
        vue.getBtnRafraichir().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chargerDonnees();
            }
        });

        // Bouton Ajouter
        vue.getBtnAjouter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vue.afficherFormulaireAjout();
                attacherEcouteursFormulaire();
            }
        });

        // Bouton Modifier
        vue.getBtnModifier().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = vue.getTableMedecins().getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                    vue.afficherFormulaireModification(id);
                    attacherEcouteursFormulaire();
                } else {
                    vue.afficherMessage("Veuillez sélectionner un médecin à modifier", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Bouton Rechercher
        vue.getBtnRecherche().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recherche = vue.getTfRecherche().getText().trim().toLowerCase();
                if (!recherche.isEmpty()) {
                    // Recherche simple dans le tableau
                    vue.getModelTable().setRowCount(0);
                    List<Medecin> medecins = Medecin.getAllMedecins();
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
                    chargerDonnees(); // Si la recherche est vide, afficher tous les médecins
                }
            }
        });
    }

    /**
     * Attache les écouteurs aux boutons du formulaire
     */
    private void attacherEcouteursFormulaire() {
        // Bouton Valider
        if (vue.getBtnValider() != null) {
            // Supprimer les écouteurs existants
            for (ActionListener al : vue.getBtnValider().getActionListeners()) {
                vue.getBtnValider().removeActionListener(al);
            }

            vue.getBtnValider().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    validerFormulaire();
                }
            });
        }

        // Bouton Annuler
        if (vue.getBtnAnnuler() != null) {
            // Supprimer les écouteurs existants
            for (ActionListener al : vue.getBtnAnnuler().getActionListeners()) {
                vue.getBtnAnnuler().removeActionListener(al);
            }

            vue.getBtnAnnuler().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (vue.getDialogFormulaire() != null) {
                        vue.getDialogFormulaire().dispose();
                    }
                }
            });
        }
    }

    /**
     * Valide et traite le formulaire d'ajout/modification
     */
    private void validerFormulaire() {
        // Validation des champs
        if (vue.getTfNom().getText().trim().isEmpty() ||
                vue.getTfPrenom().getText().trim().isEmpty() ||
                vue.getTfSpecialite().getText().trim().isEmpty() ||
                vue.getTfHoraires().getText().trim().isEmpty()) {

            vue.afficherMessage("Veuillez remplir tous les champs obligatoires", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // En mode ajout, vérifier aussi les champs de connexion
        if (vue.isModeAjout() &&
                (vue.getTfLogin().getText().trim().isEmpty() ||
                        vue.getPfPassword().getPassword().length == 0)) {

            vue.afficherMessage("Veuillez remplir les champs de connexion", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean result;

            if (vue.isModeAjout()) {
                // Ajouter un nouveau médecin
                result = ajouterMedecin(
                        vue.getTfNom().getText().trim(),
                        vue.getTfPrenom().getText().trim(),
                        vue.getTfLogin().getText().trim(),
                        new String(vue.getPfPassword().getPassword()),
                        vue.getTfSpecialite().getText().trim(),
                        vue.getTfHoraires().getText().trim()
                );

                if (result) {
                    vue.afficherMessage("Médecin ajouté avec succès", "Ajout réussi", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    vue.afficherMessage("Erreur lors de l'ajout du médecin", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Modifier un médecin existant
                result = Medecin.modifierMedecin(
                        vue.getIdMedecinSelectionne(),
                        vue.getTfNom().getText().trim(),
                        vue.getTfPrenom().getText().trim(),
                        vue.getTfSpecialite().getText().trim(),
                        vue.getTfHoraires().getText().trim()
                );

                if (result) {
                    vue.afficherMessage("Médecin modifié avec succès", "Modification réussie", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    vue.afficherMessage("Erreur lors de la modification du médecin", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (result) {
                vue.getDialogFormulaire().dispose();
                chargerDonnees();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            vue.afficherMessage("Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ajoute un nouveau médecin.
     * @param nom Le nom du médecin
     * @param prenom Le prénom du médecin
     * @param login Le login du médecin
     * @param password Le mot de passe du médecin
     * @param specialite La spécialité du médecin
     * @param horaires Les horaires du médecin
     * @return true si l'ajout a réussi, false sinon
     */
    public static boolean ajouterMedecin(String nom, String prenom, String login, String password, String specialite, String horaires) {
        Medecin medecin = new Medecin(nom, prenom, login, password, specialite, horaires);
        return medecin.enregistrer();
    }
}