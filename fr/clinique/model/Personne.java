package fr.clinique.model;

import java.sql.*;

/**
 * Classe abstraite représentant une personne avec ses attributs de base.
 * Sert de classe parent pour Patient, Utilisateur, etc.
 */
public abstract class Personne implements Model<Personne> {
    protected int id;
    protected String nom;
    protected String prenom;

    /**
     * Constructeur par défaut.
     */
    public Personne() {
        // Constructeur par défaut
    }

    /**
     * Constructeur avec nom et prénom.
     * @param nom Le nom de la personne
     * @param prenom Le prénom de la personne
     */
    public Personne(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    /**
     * Constructeur complet avec ID.
     * @param id L'identifiant de la personne
     * @param nom Le nom de la personne
     * @param prenom Le prénom de la personne
     */
    public Personne(int id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }

    // Getters et setters
    /**
     * Obtient l'identifiant de la personne.
     * @return L'identifiant
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant de la personne.
     * @param id Le nouvel identifiant
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtient le nom de la personne.
     * @return Le nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom de la personne.
     * @param nom Le nouveau nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Obtient le prénom de la personne.
     * @return Le prénom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Définit le prénom de la personne.
     * @param prenom Le nouveau prénom
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * Retourne une représentation textuelle de la personne.
     * @return Le prénom et le nom de la personne
     */
    @Override
    public String toString() {
        return prenom + " " + nom;
    }

    // Les sous-classes doivent implémenter les méthodes de l'interface Model
}