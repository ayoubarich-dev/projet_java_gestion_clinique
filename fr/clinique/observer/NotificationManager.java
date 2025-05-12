package fr.clinique.observer;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Gestionnaire de notifications pour l'application.
 * Implémente le patron de conception Singleton.
 */
public class NotificationManager {
    // Instance unique du gestionnaire de notifications
    private static NotificationManager instance;

    // Liste des notifications
    private List<String> notifications = new ArrayList<>();

    // Zone de texte pour afficher les notifications dans l'interface
    private JTextArea zoneNotifications;

    /**
     * Constructeur privé pour empêcher l'instanciation directe (patron Singleton).
     */
    private NotificationManager() {
        // Constructeur privé
    }

    public boolean notificationExiste(String message) {
        for (String notification : notifications) {
            // Extraire le message sans le timestamp pour la comparaison
            if (notification.contains("] ")) {
                String msgSansTimestamp = notification.substring(notification.indexOf("] ") + 2);
                if (msgSansTimestamp.equals(message)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retourne l'instance unique du gestionnaire de notifications.
     * Si l'instance n'existe pas, elle est créée.
     * @return L'instance unique du gestionnaire de notifications
     */
    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    /**
     * Définit la zone de texte où afficher les notifications.
     * @param zoneNotifications La zone de texte où afficher les notifications
     */
    public void setZoneNotifications(JTextArea zoneNotifications) {
        this.zoneNotifications = zoneNotifications;
    }

    /**
     * Ajoute une notification et l'affiche dans la zone de notifications si disponible.
     * @param message Le message de la notification
     */
    public void ajouterNotification(String message) {
        // Vérifier si la notification existe déjà
        if (notificationExiste(message)) {
            System.out.println("Notification déjà existante, ignorée: " + message);
            return;
        }

        // Obtenir la date et l'heure actuelles
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timestamp = sdf.format(new Date());

        // Préparer le message complet de la notification
        String notificationComplete = "[" + timestamp + "] " + message;

        // Stocker la notification dans la liste
        notifications.add(notificationComplete);

        // Afficher dans la zone de notifications si disponible
        if (zoneNotifications != null) {
            SwingUtilities.invokeLater(() -> {
                Color oldColor = zoneNotifications.getForeground();
                zoneNotifications.setForeground(Color.BLUE);
                zoneNotifications.append(notificationComplete + "\n");
                zoneNotifications.setForeground(oldColor);
                zoneNotifications.setCaretPosition(zoneNotifications.getDocument().getLength());
            });
        }

        // Afficher aussi dans la console (pour débogage)
        System.out.println("NOTIFICATION: " + notificationComplete);
    }


    /**
     * Retourne la liste des notifications.
     * @return Une copie de la liste des notifications
     */
    public List<String> getNotifications() {
        return new ArrayList<>(notifications); // Retourne une copie
    }

    /**
     * Efface toutes les notifications.
     */
    public void clearNotifications() {
        notifications.clear();

        if (zoneNotifications != null) {
            SwingUtilities.invokeLater(() -> {
                zoneNotifications.setText("");
            });
        }
    }
}