package fr.clinique.controller;

import fr.clinique.model.Utilisateur;
import fr.clinique.view.VueAuthentification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Contrôleur pour gérer l'authentification des utilisateurs.
 */
public class ControleurAuthentification {
    private VueAuthentification vue;

    /**
     * Constructeur qui initialise le contrôleur avec la vue d'authentification.
     * @param vue La vue d'authentification
     */
    public ControleurAuthentification(VueAuthentification vue) {
        this.vue = vue;

        System.out.println("Initialisation du contrôleur d'authentification");

        // Ajout des écouteurs d'événements
        this.vue.getBtnConnexion().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Bouton de connexion cliqué");
                authentifier();
            }
        });

        this.vue.getPfPassword().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Touche Entrée détectée dans le champ mot de passe");
                authentifier();
            }
        });
    }

    /**
     * Méthode qui gère l'authentification de l'utilisateur.
     */
    private void authentifier() {
        try {
            System.out.println("Début de la méthode authentifier()");
            String login = vue.getTfLogin().getText();
            String password = new String(vue.getPfPassword().getPassword());

            System.out.println("Tentative d'authentification pour: " + login);

            if (login.isEmpty() || password.isEmpty()) {
                System.out.println("Champs vides - affichage de l'erreur");
                vue.afficherErreur("Veuillez remplir tous les champs");
                return;
            }

            Utilisateur utilisateur = Utilisateur.authentifier(login, password);

            System.out.println("Résultat de l'authentification: " + (utilisateur != null ? "succès" : "échec"));

            if (utilisateur != null) {
                // Ouvrir la vue principale avec le rôle de l'utilisateur
                System.out.println("Utilisateur authentifié avec succès - Rôle: " + utilisateur.getRole());
                vue.ouvrirVuePrincipale(utilisateur);
            } else {
                System.out.println("Authentification échouée - affichage de l'erreur");
                vue.afficherErreur("Identifiant ou mot de passe incorrect");
            }
        } catch (Exception e) {
            System.err.println("Exception pendant l'authentification: " + e.getMessage());
            e.printStackTrace();
            vue.afficherErreur("Une erreur est survenue: " + e.getMessage());
        }
    }
}