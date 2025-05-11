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

        // Initialiser la vue avec les données
        chargerDonnees();

        // Ajouter les écouteurs d'événements
        ajouterEcouteurs();
    }

    /**
     * Charge les données des médecins dans la vue.
     */
    private void chargerDonnees() {
        List<Medecin> medecins = Medecin.afficherTousMedecins();
        vue.afficherDonnees(medecins);
    }

    /**
     * Ajoute les écouteurs d'événements aux composants de la vue.
     */
    private void ajouterEcouteurs() {
        // Bouton Ajouter
        vue.getBtnAjouter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vue.afficherFormulaireAjout();
            }
        });

        // Bouton Modifier
        vue.getBtnModifier().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = vue.getTableMedecins().getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                    vue.setIdMedecinSelectionne(id);
                    vue.afficherFormulaireModification(id);
                } else {
                    vue.afficherMessage("Veuillez sélectionner un médecin à modifier", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Bouton Supprimer
        vue.getBtnSupprimer().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

        // Bouton Rechercher
        vue.getBtnRecherche().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

        // Bouton Rafraîchir
        vue.getBtnRafraichir().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vue.getTfRecherche().setText("");
                chargerDonnees();
            }
        });

        // Double-clic sur une ligne du tableau
        vue.getTableMedecins().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = vue.getTableMedecins().getSelectedRow();
                    if (selectedRow >= 0 && vue.getUtilisateur().getRole() == Role.ADMINISTRATEUR) {
                        int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                        vue.setIdMedecinSelectionne(id);
                        vue.afficherFormulaireModification(id);
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
     * Valide le formulaire d'ajout ou de modification d'un médecin.
     */
    private void validerFormulaire() {
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
        if (vue.isModeAjout()) {
            // Ajouter un nouveau médecin avec compte utilisateur
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
                vue.afficherMessage("Médecin ajouté avec succès", "Ajout réussi", JOptionPane.INFORMATION_MESSAGE);
                vue.getDialogFormulaire().dispose();
                chargerDonnees();
            } else {
                vue.afficherMessage("Erreur lors de l'ajout du médecin", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Modifier un médecin existant
            Medecin medecin = MedecinModel.getMedecinById(vue.getIdMedecinSelectionne());

            if (medecin != null) {
                medecin.setNom(vue.getTfNom().getText().trim());
                medecin.setPrenom(vue.getTfPrenom().getText().trim());
                medecin.setSpecialite(vue.getTfSpecialite().getText().trim());
                medecin.setHoraires(vue.getTfHoraires().getText().trim());

                result = medecin.enregistrer();

                if (result) {
                    vue.afficherMessage("Médecin modifié avec succès", "Modification réussie", JOptionPane.INFORMATION_MESSAGE);
                    vue.getDialogFormulaire().dispose();
                    chargerDonnees();
                } else {
                    vue.afficherMessage("Erreur lors de la modification du médecin", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                vue.afficherMessage("Médecin introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}