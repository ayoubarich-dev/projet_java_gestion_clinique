package fr.clinique.view;

import fr.clinique.model.*;
import fr.clinique.controller.*;
import fr.clinique.observer.NotificationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VuePrincipale extends JFrame {
    private Utilisateur utilisateur;

    private JMenuBar menuBar;
    private JMenu menuPatients, menuMedecins, menuSecretaires, menuRendezVous, menuQuitter, menuMessages;
    private JMenuItem miListePatients, miAjouterPatient;
    private JMenuItem miListeMedecins, miAjouterMedecin;
    private JMenuItem miListeSecretaires, miAjouterSecretaire;
    private JMenuItem miListeRendezVous, miAjouterRendezVous, miExporterExcel, miExporterPDF;
    private JMenuItem miDeconnexion, miQuitter;
    private JMenuItem miAfficherMessages;
    private JTextArea zoneNotifications;
    private JPanel contentPanel;

    // Stocker les contrôleurs actifs
    private ControleurPatient controleurPatient;
    private ControleurMedecin controleurMedecin;
    private ControleurSecretaire controleurSecretaire;
    private ControleurRendezVous controleurRendezVous;

    public VuePrincipale(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        initUI();

        // Vérifier si le médecin a des messages à sa connexion
        if (utilisateur.getRole() == Role.MEDECIN) {
            Medecin medecin = MedecinModel.getMedecinByUtilisateurId(utilisateur.getId());
            if (medecin != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        List<String> messages = MedecinModel.getMessagesPourMedecin(medecin.getId());
                        if (!messages.isEmpty()) {
                            int reponse = JOptionPane.showConfirmDialog(VuePrincipale.this,
                                    "Vous avez " + messages.size() + " nouveau(x) message(s). Souhaitez-vous les consulter maintenant?",
                                    "Nouveaux messages",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.INFORMATION_MESSAGE);

                            if (reponse == JOptionPane.YES_OPTION) {
                                afficherMessages(medecin.getId());
                            }
                        }
                    }
                });
            }
        }
    }

    private void initUI() {
        setTitle("Gestion de Clinique Médicale");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Menu
        menuBar = new JMenuBar();

        // Menu Patients
        menuPatients = new JMenu("Patients");
        miListePatients = new JMenuItem("Liste des patients");
        miAjouterPatient = new JMenuItem("Ajouter un patient");

        menuPatients.add(miListePatients);
        menuPatients.add(miAjouterPatient);

        // Menu Médecins (uniquement pour l'administrateur)
        menuMedecins = new JMenu("Médecins");
        miListeMedecins = new JMenuItem("Liste des médecins");
        miAjouterMedecin = new JMenuItem("Ajouter un médecin");

        menuMedecins.add(miListeMedecins);
        menuMedecins.add(miAjouterMedecin);

        // Menu Secrétaires (uniquement pour l'administrateur)
        menuSecretaires = new JMenu("Secrétaires");
        miListeSecretaires = new JMenuItem("Liste des secrétaires");
        miAjouterSecretaire = new JMenuItem("Ajouter un secrétaire");

        menuSecretaires.add(miListeSecretaires);
        menuSecretaires.add(miAjouterSecretaire);

        // Menu Rendez-vous
        menuRendezVous = new JMenu("Rendez-vous");
        miListeRendezVous = new JMenuItem("Liste des rendez-vous");
        miAjouterRendezVous = new JMenuItem("Ajouter un rendez-vous");
        miExporterExcel = new JMenuItem("Exporter en Excel");
        miExporterPDF = new JMenuItem("Exporter en PDF");

        menuRendezVous.add(miListeRendezVous);
        menuRendezVous.add(miAjouterRendezVous);
        menuRendezVous.addSeparator();
        menuRendezVous.add(miExporterExcel);
        menuRendezVous.add(miExporterPDF);

        // Menu Messages (uniquement pour les médecins)
        if (utilisateur.getRole() == Role.MEDECIN) {
            menuMessages = new JMenu("Messages");
            miAfficherMessages = new JMenuItem("Voir mes messages");

            menuMessages.add(miAfficherMessages);
        }

        // Menu Quitter
        menuQuitter = new JMenu("Quitter");
        miDeconnexion = new JMenuItem("Déconnexion");
        miQuitter = new JMenuItem("Quitter l'application");

        menuQuitter.add(miDeconnexion);
        menuQuitter.add(miQuitter);

        // Ajout des menus à la barre de menu selon le rôle
        menuBar.add(menuPatients);

        // Seul l'admin peut gérer les médecins et secrétaires
        if (utilisateur.getRole() == Role.ADMINISTRATEUR) {
            menuBar.add(menuMedecins);
            menuBar.add(menuSecretaires);
        }

        menuBar.add(menuRendezVous);

        if (utilisateur.getRole() == Role.MEDECIN) {
            menuBar.add(menuMessages);
        }

        menuBar.add(menuQuitter);

        setJMenuBar(menuBar);

        // Panneau de contenu
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        // Panneau de bienvenue
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblWelcome = new JLabel("Bienvenue dans l'application de Gestion de Clinique Médicale");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblUser = new JLabel("Utilisateur connecté: " + utilisateur.getLogin() + " (" + utilisateur.getRole() + ")");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 20, 0);
        welcomePanel.add(lblWelcome, gbc);

        gbc.gridy = 1;
        welcomePanel.add(lblUser, gbc);

        contentPanel.add(welcomePanel, BorderLayout.CENTER);

        // Zone de notifications (uniquement pour les médecins)
        if (utilisateur.getRole() == Role.MEDECIN) {
            zoneNotifications = new JTextArea(5, 50);
            zoneNotifications.setEditable(false);
            zoneNotifications.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollNotifications = new JScrollPane(zoneNotifications);
            scrollNotifications.setBorder(BorderFactory.createTitledBorder("Notifications"));

            // Enregistrer la zone de notifications dans le gestionnaire
            NotificationManager.getInstance().setZoneNotifications(zoneNotifications);

            // Panneau divisé entre le contenu principal et les notifications
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    contentPanel,
                    scrollNotifications);
            splitPane.setResizeWeight(0.8); // 80% pour le contenu, 20% pour les notifications

            add(splitPane);
        } else {
            // Pour les autres rôles, pas de zone de notifications
            add(contentPanel);
        }

        // Configuration selon les rôles
        configureMenuAccess();

        // Ajouter les écouteurs d'événements
        setupEventListeners();

        setVisible(true);
    }

    private void configureMenuAccess() {
        if (utilisateur.getRole() == Role.MEDECIN) {
            // Un médecin ne peut pas gérer d'autres médecins ou ajouter des patients
            menuMedecins.setEnabled(false);
            miAjouterPatient.setEnabled(false);

            // Un médecin ne peut voir que ses propres rendez-vous
            miAjouterRendezVous.setEnabled(false);
        } else if (utilisateur.getRole() == Role.SECRETAIRE) {
            // Une secrétaire ne peut pas gérer les médecins
            menuMedecins.setEnabled(false);
            menuSecretaires.setEnabled(false);
        }
    }

    private void setupEventListeners() {
        // Patients
        miListePatients.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPatientList();
            }
        });

        miAjouterPatient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddPatientForm();
            }
        });

        // Médecins (si disponible selon le rôle)
        if (utilisateur.getRole() == Role.ADMINISTRATEUR) {
            miListeMedecins.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showMedecinList();
                }
            });

            miAjouterMedecin.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showAddMedecinForm();
                }
            });

            // Secrétaires
            miListeSecretaires.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showSecretaireList();
                }
            });

            miAjouterSecretaire.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showAddSecretaireForm();
                }
            });
        }

        // Rendez-vous
        miListeRendezVous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRendezVousList();
            }
        });

        miAjouterRendezVous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddRendezVousForm();
            }
        });

        miExporterExcel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportRendezVousToExcel();
            }
        });

        miExporterPDF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportRendezVousToPDF();
            }
        });

        // Messages (seulement pour les médecins)
        if (utilisateur.getRole() == Role.MEDECIN) {
            miAfficherMessages.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Medecin medecin = MedecinModel.getMedecinByUtilisateurId(utilisateur.getId());
                    if (medecin != null) {
                        afficherMessages(medecin.getId());
                    }
                }
            });
        }

        // Quitter
        miDeconnexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();

            }
        });

        miQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
    }

    // Méthode pour afficher les messages du médecin
    public void afficherMessages(int idMedecin) {
        // Récupérer tous les messages du médecin
        List<String> messages = MedecinModel.getMessagesPourMedecin(idMedecin);

        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vous n'avez pas de nouveaux messages.",
                    "Messages",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Créer un panneau pour afficher les messages
        JDialog dialogMessages = new JDialog(this, "Mes messages", true);
        dialogMessages.setSize(500, 300);
        dialogMessages.setLocationRelativeTo(this);
        dialogMessages.setLayout(new BorderLayout());

        // Liste de messages
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String message : messages) {
            listModel.addElement(message);
        }

        JList<String> listMessages = new JList<>(listModel);
        listMessages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(listMessages);

        // Bouton pour marquer comme lu
        JButton btnMarquerLu = new JButton("Marquer tous comme lus");
        btnMarquerLu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MedecinModel.clearMessagesPourMedecin(idMedecin);
                dialogMessages.dispose();

                JOptionPane.showMessageDialog(VuePrincipale.this,
                        "Tous les messages ont été marqués comme lus.",
                        "Messages",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        dialogMessages.add(scrollPane, BorderLayout.CENTER);
        dialogMessages.add(btnMarquerLu, BorderLayout.SOUTH);

        dialogMessages.setVisible(true);
    }

    // Méthodes pour changer le contenu du panneau principal
    private void showPatientList() {
        contentPanel.removeAll();

        try {
            // Créer la vue
            VuePatient vuePatient = new VuePatient(utilisateur);

            // Créer et stocker le contrôleur
            controleurPatient = new ControleurPatient(vuePatient);

            // Appeler directement la méthode publique
            controleurPatient.attacherEcouteursFormulaire();

            // Ajouter la vue au panneau
            contentPanel.add(vuePatient, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();

            System.out.println("Vue patient affichée et contrôleur initialisé");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la vue patient: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'initialisation de la vue patient: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddPatientForm() {
        // Vérifier si la vue patient est déjà affichée, sinon l'afficher
        if (contentPanel.getComponentCount() == 0 || !(contentPanel.getComponent(0) instanceof VuePatient)) {
            showPatientList();
        }

        // Récupérer la vue patient
        VuePatient vuePatient = (VuePatient) contentPanel.getComponent(0);

        // Afficher le formulaire d'ajout
        vuePatient.afficherFormulaireAjout();

        System.out.println("Formulaire d'ajout de patient affiché");
    }


    private void showMedecinList() {
        contentPanel.removeAll();

        try {
            System.out.println("Initialisation de la vue médecin...");

            // Créer la vue
            VueMedecin vueMedecin = new VueMedecin(utilisateur);

            // Créer et stocker le contrôleur
            controleurMedecin = new ControleurMedecin(vueMedecin);

            // Appeler directement la méthode publique
            // si elle existe dans le contrôleur
            if (controleurMedecin != null) {
                controleurMedecin.attacherEcouteursFormulaire();
            }

            // Ajouter la vue au panneau
            contentPanel.add(vueMedecin, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();

            System.out.println("Vue médecin affichée et contrôleur initialisé");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la vue médecin: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'initialisation de la vue médecin: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddMedecinForm() {
        try {
            // Vérifier si la vue médecin est déjà affichée, sinon l'afficher
            if (contentPanel.getComponentCount() == 0 || !(contentPanel.getComponent(0) instanceof VueMedecin)) {
                showMedecinList();
            }

            // Récupérer la vue médecin
            if (contentPanel.getComponent(0) instanceof VueMedecin) {
                VueMedecin vueMedecin = (VueMedecin) contentPanel.getComponent(0);

                // Afficher le formulaire d'ajout
                vueMedecin.afficherFormulaireAjout();

                // Réattacher les écouteurs aux boutons du formulaire
                if (controleurMedecin != null) {
                    controleurMedecin.attacherEcouteursFormulaire();
                }

                System.out.println("Formulaire d'ajout de médecin affiché");
            } else {
                System.err.println("Impossible de récupérer la vue médecin");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage du formulaire d'ajout médecin: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'affichage du formulaire d'ajout médecin: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSecretaireList() {
        contentPanel.removeAll();

        try {
            // Créer la vue
            VueSecretaire vueSecretaire = new VueSecretaire(utilisateur);

            // Créer et stocker le contrôleur
            controleurSecretaire = new ControleurSecretaire(vueSecretaire);

            // Ajouter la vue au panneau
            contentPanel.add(vueSecretaire, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();

            System.out.println("Vue secrétaire affichée et contrôleur initialisé");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la vue secrétaire: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'initialisation de la vue secrétaire: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showAddSecretaireForm() {
        // Afficher un formulaire d'ajout de secrétaire
        JDialog dialog = new JDialog(this, "Ajouter un secrétaire", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nom:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfNom = new JTextField(20);
        panel.add(tfNom, gbc);

        // Prénom
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Prénom:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfPrenom = new JTextField(20);
        panel.add(tfPrenom, gbc);

        // Login
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Login:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfLogin = new JTextField(20);
        panel.add(tfLogin, gbc);

        // Mot de passe
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("Mot de passe:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JPasswordField pfPassword = new JPasswordField(20);
        panel.add(pfPassword, gbc);

        // Boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnValider = new JButton("Valider");
        JButton btnAnnuler = new JButton("Annuler");

        btnValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tfNom.getText().trim().isEmpty() ||
                        tfPrenom.getText().trim().isEmpty() ||
                        tfLogin.getText().trim().isEmpty() ||
                        pfPassword.getPassword().length == 0) {

                    JOptionPane.showMessageDialog(dialog,
                            "Veuillez remplir tous les champs",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean result = false;

                try {
                    result = ControleurSecretaire.ajouterSecretaire(
                            tfNom.getText().trim(),
                            tfPrenom.getText().trim(),
                            tfLogin.getText().trim(),
                            new String(pfPassword.getPassword())
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de l'ajout du secrétaire: " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (result) {
                    JOptionPane.showMessageDialog(dialog,
                            "Secrétaire ajouté avec succès",
                            "Ajout réussi",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();

                    // Rafraîchir la liste si elle est affichée
                    if (contentPanel.getComponentCount() > 0 && contentPanel.getComponent(0) instanceof VueSecretaire) {
                        showSecretaireList();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de l'ajout du secrétaire",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnAnnuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        panelBoutons.add(btnValider);
        panelBoutons.add(btnAnnuler);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(panelBoutons, BorderLayout.SOUTH);
        dialog.setVisible(true);

        System.out.println("Formulaire d'ajout de secrétaire affiché");
    }

    private void showRendezVousList() {
        contentPanel.removeAll();

        try {
            // Créer la vue
            VueRendezVous vueRendezVous = new VueRendezVous(utilisateur);

            // Créer et stocker le contrôleur
            controleurRendezVous = new ControleurRendezVous(vueRendezVous);

            // S'assurer que le contrôleur attache correctement les écouteurs aux boutons du formulaire
            // Cette méthode doit être ajoutée à ControleurRendezVous
            controleurRendezVous.getClass().getDeclaredMethod("attacherEcouteursFormulaire").invoke(controleurRendezVous);

            // Ajouter la vue au panneau
            contentPanel.add(vueRendezVous, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();

            System.out.println("Vue rendez-vous affichée et contrôleur initialisé");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la vue rendez-vous: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'initialisation de la vue rendez-vous: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddRendezVousForm() {
        // Vérifier si la vue rendez-vous est déjà affichée, sinon l'afficher
        if (contentPanel.getComponentCount() == 0 || !(contentPanel.getComponent(0) instanceof VueRendezVous)) {
            showRendezVousList();
        }

        // Récupérer la vue rendez-vous
        VueRendezVous vueRendezVous = (VueRendezVous) contentPanel.getComponent(0);

        // Afficher le formulaire d'ajout
        vueRendezVous.afficherFormulaireAjout();

        System.out.println("Formulaire d'ajout de rendez-vous affiché");
    }

    private void exportRendezVousToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les rendez-vous en Excel");

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String cheminFichier = fileChooser.getSelectedFile().getAbsolutePath();
            // Ajouter l'extension si nécessaire
            if (!cheminFichier.toLowerCase().endsWith(".xlsx")) {
                cheminFichier += ".xlsx";
            }

            try {
                // Récupérer la liste des rendez-vous
                List<RendezVous> rendezVousList = RendezVousModel.getTousRendezVous();

                boolean result = RendezVousModel.exporterExcel(rendezVousList, cheminFichier);

                if (result) {
                    JOptionPane.showMessageDialog(this,
                            "Les rendez-vous ont été exportés avec succès en Excel.",
                            "Exportation réussie",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Une erreur est survenue lors de l'exportation.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Une erreur est survenue lors de l'exportation: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportRendezVousToPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les rendez-vous en PDF");

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String cheminFichier = fileChooser.getSelectedFile().getAbsolutePath();
            // Ajouter l'extension si nécessaire
            if (!cheminFichier.toLowerCase().endsWith(".pdf")) {
                cheminFichier += ".pdf";
            }

            try {
                // Récupérer la liste des rendez-vous
                List<RendezVous> rendezVousList = RendezVousModel.getTousRendezVous();

                boolean result = RendezVousModel.exporterPDF(rendezVousList, cheminFichier);

                if (result) {
                    JOptionPane.showMessageDialog(this,
                            "Les rendez-vous ont été exportés avec succès en PDF.",
                            "Exportation réussie",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Une erreur est survenue lors de l'exportation.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Une erreur est survenue lors de l'exportation: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        dispose();
        new VueAuthentification();
    }

    private void exit() {
        System.exit(0);
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
}