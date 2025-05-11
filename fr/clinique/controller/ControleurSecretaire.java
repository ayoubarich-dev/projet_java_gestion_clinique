package fr.clinique.controller;

import fr.clinique.model.Role;
import fr.clinique.model.Secretaire;
import fr.clinique.model.Utilisateur;
import fr.clinique.view.VueSecretaire;
import fr.clinique.view.VuePrincipale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Contrôleur pour gérer les secrétaires.
 */
public class ControleurSecretaire {
    private VueSecretaire vue;

    /**
     * Constructeur qui initialise le contrôleur avec la vue secrétaire.
     * @param vue La vue secrétaire
     */
    public ControleurSecretaire(VueSecretaire vue) {
        this.vue = vue;

        // Initialiser la vue avec les données
        chargerDonnees();

        // Ajouter les écouteurs d'événements
        ajouterEcouteurs();
    }

    /**
     * Charge les données des secrétaires dans la vue.
     */
    private void chargerDonnees() {
        List<Secretaire> secretaires = Secretaire.getAllSecretaires();
        vue.afficherDonnees(secretaires);
    }

    /**
     * Ajoute les écouteurs d'événements aux composants de la vue.
     */
    private void ajouterEcouteurs() {
        // Bouton Supprimer
        vue.getBtnSupprimer().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = vue.getTableSecretaires().getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) vue.getModelTable().getValueAt(selectedRow, 0);
                    int confirm = vue.afficherConfirmation("Êtes-vous sûr de vouloir supprimer ce secrétaire ?", "Confirmation de suppression");

                    if (confirm == JOptionPane.YES_OPTION) {
                        Utilisateur secretaire = new Secretaire();
                        secretaire.setId(id);
                        boolean result = secretaire.supprimer();

                        if (result) {
                            vue.afficherMessage("Secrétaire supprimé avec succès", "Suppression réussie", JOptionPane.INFORMATION_MESSAGE);
                            chargerDonnees();
                        } else {
                            vue.afficherMessage("Erreur lors de la suppression du secrétaire", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    vue.afficherMessage("Veuillez sélectionner un secrétaire à supprimer", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
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

        // Bouton Ajouter - redirige vers la vue principale pour afficher le formulaire d'ajout
        vue.getBtnAjouter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ce bouton est géré par la VuePrincipale
                java.awt.Window window = SwingUtilities.getWindowAncestor(vue);
                if (window instanceof VuePrincipale) {
                    ((VuePrincipale) window).showAddSecretaireForm();
                }
            }
        });
    }

    /**
     * Ajoute un nouveau secrétaire.
     * @param nom Le nom du secrétaire
     * @param prenom Le prénom du secrétaire
     * @param login Le login du secrétaire
     * @param password Le mot de passe du secrétaire
     * @return true si l'ajout a réussi, false sinon
     */
    public static boolean ajouterSecretaire(String nom, String prenom, String login, String password) {
        Secretaire secretaire = new Secretaire(nom, prenom, login, password);
        return secretaire.enregistrer();
    }
}