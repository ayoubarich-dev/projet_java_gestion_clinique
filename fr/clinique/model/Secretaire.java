package fr.clinique.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une secrétaire.
 * Hérite de la classe Utilisateur.
 */
public class Secretaire extends Utilisateur {

    /**
     * Constructeur par défaut.
     */
    public Secretaire() {
        super();
        setRole(Role.SECRETAIRE);
    }

    /**
     * Constructeur avec les attributs de base.
     * @param nom Le nom de la secrétaire
     * @param prenom Le prénom de la secrétaire
     * @param login Le login de la secrétaire
     * @param password Le mot de passe de la secrétaire
     */
    public Secretaire(String nom, String prenom, String login, String password) {
        super(nom, prenom, login, password, Role.SECRETAIRE);
    }

    /**
     * Constructeur complet avec ID.
     * @param id L'identifiant de la secrétaire
     * @param nom Le nom de la secrétaire
     * @param prenom Le prénom de la secrétaire
     * @param login Le login de la secrétaire
     * @param password Le mot de passe de la secrétaire
     */
    public Secretaire(int id, String nom, String prenom, String login, String password) {
        super(id, nom, prenom, login, password, Role.SECRETAIRE);
    }

    /**
     * Récupère toutes les secrétaires de la base de données.
     * @return Liste de toutes les secrétaires
     */
    public static List<Secretaire> getAllSecretaires() {
        List<Secretaire> secretaires = new ArrayList<>();
        List<Utilisateur> utilisateurs = Utilisateur.rechercherParRole(Role.SECRETAIRE);

        for (Utilisateur utilisateur : utilisateurs) {
            if (utilisateur instanceof Secretaire) {
                secretaires.add((Secretaire) utilisateur);
            } else {
                // Si ce n'est pas déjà une instance de Secretaire, convertir
                Secretaire secretaire = new Secretaire();
                secretaire.setId(utilisateur.getId());
                secretaire.setNom(utilisateur.getNom());
                secretaire.setPrenom(utilisateur.getPrenom());
                secretaire.setLogin(utilisateur.getLogin());
                secretaire.setPassword(utilisateur.getPassword());
                secretaires.add(secretaire);
            }
        }

        return secretaires;
    }
}