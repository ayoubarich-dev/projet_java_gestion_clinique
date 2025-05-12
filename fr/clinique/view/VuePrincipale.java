package fr.clinique.view;

import fr.clinique.model.*;
import fr.clinique.controller.*;
import fr.clinique.observer.NotificationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VuePrincipale extends JFrame {
    private Utilisateur utilisateur;
    private VueAuthentification vueAuthentification; // Référence à la vue d'authentification

    private JMenuBar menuBar;
    private JMenu menuPatients, menuMedecins, menuSecretaires, menuRendezVous, menuQuitter, menuMessages;
    private JMenuItem miListePatients, miAjouterPatient;
    private JMenuItem miListeMedecins, miAjouterMedecin;
    private JMenuItem miListeSecretaires, miAjouterSecretaire;
    private JMenuItem miListeRendezVous, miAjouterRendezVous, miExporterExcel, miExporterPDF;
    private JMenuItem miDeconnexion, miQuitter;
    private JMenuItem miAfficherMessages;
    private JPanel contentPanel;
    private ControleurPatient controleurPatient;
    private ControleurMedecin controleurMedecin;
    private ControleurSecretaire controleurSecretaire;
    private ControleurRendezVous controleurRendezVous;

    public VuePrincipale(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        initUI();

        // Vérifier si le médecin a des messages à sa connexion
        if (utilisateur.getRole() == Role.MEDECIN) {
            if (utilisateur.getRole() == Role.MEDECIN) {
                Medecin medecin = Medecin.rechercherParIdUtilisateur(utilisateur.getId());
                if (medecin != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            List<String> messages = Medecin.getMessagesPourMedecin(medecin.getId());
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
    }
    public void setVueAuthentification(VueAuthentification vueAuthentification) {
        this.vueAuthentification = vueAuthentification;
    }
    private void initUI() {
        setTitle("Gestion de Clinique Médicale");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setTitle("Gestion de Clinique Médicale");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Changé pour gérer la fermeture
        setLocationRelativeTo(null);

        // Ajouter un gestionnaire de fermeture de fenêtre
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int confirmer = JOptionPane.showConfirmDialog(VuePrincipale.this,
                        "Êtes-vous sûr de vouloir quitter l'application?", "Confirmation de fermeture",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (confirmer == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
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

        add(contentPanel);


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
                    Medecin medecin = Medecin.rechercherParIdUtilisateur(utilisateur.getId());
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
        List<String> messages = Medecin.getMessagesPourMedecin(idMedecin);

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
                Medecin.clearMessagesPourMedecin(idMedecin);
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

    public void showAddPatientForm() {
        // Afficher un formulaire d'ajout de patient
        JDialog dialog = new JDialog(this, "Ajouter un patient", true);
        dialog.setSize(400, 350);
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

        // Date de naissance
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Date de naissance:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JFormattedTextField ftfDateNaissance = new JFormattedTextField();
        ftfDateNaissance.setToolTipText("Format: JJ/MM/AAAA");
        panel.add(ftfDateNaissance, gbc);

        // Téléphone
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("Téléphone:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfTelephone = new JTextField(20);
        panel.add(tfTelephone, gbc);

        // Boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnValider = new JButton("Valider");
        JButton btnAnnuler = new JButton("Annuler");

        btnValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tfNom.getText().trim().isEmpty() ||
                        tfPrenom.getText().trim().isEmpty() ||
                        ftfDateNaissance.getText().trim().isEmpty() ||
                        tfTelephone.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog,
                            "Veuillez remplir tous les champs",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Vérifier le format de la date
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                Date dateNaissance;

                try {
                    dateNaissance = sdf.parse(ftfDateNaissance.getText());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Format de date invalide. Utilisez JJ/MM/AAAA",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean result = false;

                try {
                    result = ControleurPatient.ajouterPatient(
                            tfNom.getText().trim(),
                            tfPrenom.getText().trim(),
                            dateNaissance,
                            tfTelephone.getText().trim()
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de l'ajout du patient: " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (result) {
                    JOptionPane.showMessageDialog(dialog,
                            "Patient ajouté avec succès",
                            "Ajout réussi",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();

                    // Rafraîchir la liste si elle est affichée
                    if (contentPanel.getComponentCount() > 0 && contentPanel.getComponent(0) instanceof VuePatient) {
                        showPatientList();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de l'ajout du patient",
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

        System.out.println("Formulaire d'ajout de patient affiché");
    }

    public void showEditPatientForm(int id) {
        // Rechercher le patient
        Patient patient = Patient.getPatientById(id);
        if (patient == null) {
            JOptionPane.showMessageDialog(this,
                    "Patient introuvable",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Afficher un formulaire de modification de patient
        JDialog dialog = new JDialog(this, "Modifier un patient", true);
        dialog.setSize(400, 350);
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
        JTextField tfNom = new JTextField(patient.getNom(), 20);
        panel.add(tfNom, gbc);

        // Prénom
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Prénom:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfPrenom = new JTextField(patient.getPrenom(), 20);
        panel.add(tfPrenom, gbc);

        // Date de naissance
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Date de naissance:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        JFormattedTextField ftfDateNaissance = new JFormattedTextField();
        ftfDateNaissance.setText(sdf.format(patient.getDateNaissance()));
        ftfDateNaissance.setToolTipText("Format: JJ/MM/AAAA");
        panel.add(ftfDateNaissance, gbc);

        // Téléphone
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("Téléphone:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfTelephone = new JTextField(patient.getTelephone(), 20);
        panel.add(tfTelephone, gbc);

        // Boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnValider = new JButton("Valider");
        JButton btnAnnuler = new JButton("Annuler");

        btnValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tfNom.getText().trim().isEmpty() ||
                        tfPrenom.getText().trim().isEmpty() ||
                        ftfDateNaissance.getText().trim().isEmpty() ||
                        tfTelephone.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog,
                            "Veuillez remplir tous les champs",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Vérifier le format de la date
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                Date dateNaissance;

                try {
                    dateNaissance = sdf.parse(ftfDateNaissance.getText());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Format de date invalide. Utilisez JJ/MM/AAAA",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean result = false;

                try {
                    result = Patient.modifierPatient(
                            id,
                            tfNom.getText().trim(),
                            tfPrenom.getText().trim(),
                            dateNaissance,
                            tfTelephone.getText().trim()
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de la modification du patient: " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (result) {
                    JOptionPane.showMessageDialog(dialog,
                            "Patient modifié avec succès",
                            "Modification réussie",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();

                    // Rafraîchir la liste si elle est affichée
                    if (contentPanel.getComponentCount() > 0 && contentPanel.getComponent(0) instanceof VuePatient) {
                        showPatientList();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de la modification du patient",
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

        System.out.println("Formulaire de modification de patient affiché");
    }


    private void showMedecinList() {
        contentPanel.removeAll();

        try {
            // Créer la vue
            VueMedecin vueMedecin = new VueMedecin(utilisateur);

            // Créer et stocker le contrôleur
            controleurMedecin = new ControleurMedecin(vueMedecin);

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

    public void showAddMedecinForm() {
        // Afficher un formulaire d'ajout de médecin
        JDialog dialog = new JDialog(this, "Ajouter un médecin", true);
        dialog.setSize(400, 400);
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

        // Spécialité
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        panel.add(new JLabel("Spécialité:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfSpecialite = new JTextField(20);
        panel.add(tfSpecialite, gbc);

        // Horaires
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        panel.add(new JLabel("Horaires:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfHoraires = new JTextField(20);
        panel.add(tfHoraires, gbc);

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
                        pfPassword.getPassword().length == 0 ||
                        tfSpecialite.getText().trim().isEmpty() ||
                        tfHoraires.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog,
                            "Veuillez remplir tous les champs",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean result = false;

                try {
                    result = ControleurMedecin.ajouterMedecin(
                            tfNom.getText().trim(),
                            tfPrenom.getText().trim(),
                            tfLogin.getText().trim(),
                            new String(pfPassword.getPassword()),
                            tfSpecialite.getText().trim(),
                            tfHoraires.getText().trim()
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de l'ajout du médecin: " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (result) {
                    JOptionPane.showMessageDialog(dialog,
                            "Médecin ajouté avec succès",
                            "Ajout réussi",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();

                    // Rafraîchir la liste si elle est affichée
                    if (contentPanel.getComponentCount() > 0 && contentPanel.getComponent(0) instanceof VueMedecin) {
                        showMedecinList();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de l'ajout du médecin",
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

        System.out.println("Formulaire d'ajout de médecin affiché");
    }

    public void showEditMedecinForm(int id) {
        // Rechercher le médecin
        Medecin medecin = Medecin.getMedecinById(id);
        if (medecin == null) {
            JOptionPane.showMessageDialog(this,
                    "Médecin introuvable",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Afficher un formulaire de modification de médecin
        JDialog dialog = new JDialog(this, "Modifier un médecin", true);
        dialog.setSize(400, 350);
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
        JTextField tfNom = new JTextField(medecin.getNom(), 20);
        panel.add(tfNom, gbc);

        // Prénom
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Prénom:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfPrenom = new JTextField(medecin.getPrenom(), 20);
        panel.add(tfPrenom, gbc);

        // Spécialité
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Spécialité:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfSpecialite = new JTextField(medecin.getSpecialite(), 20);
        panel.add(tfSpecialite, gbc);

        // Horaires
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("Horaires:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfHoraires = new JTextField(medecin.getHoraires(), 20);
        panel.add(tfHoraires, gbc);

        // Boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnValider = new JButton("Valider");
        JButton btnAnnuler = new JButton("Annuler");

        btnValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tfNom.getText().trim().isEmpty() ||
                        tfPrenom.getText().trim().isEmpty() ||
                        tfSpecialite.getText().trim().isEmpty() ||
                        tfHoraires.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog,
                            "Veuillez remplir tous les champs",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean result = false;

                try {
                    result = Medecin.modifierMedecin(
                            id,
                            tfNom.getText().trim(),
                            tfPrenom.getText().trim(),
                            tfSpecialite.getText().trim(),
                            tfHoraires.getText().trim()
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de la modification du médecin: " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (result) {
                    JOptionPane.showMessageDialog(dialog,
                            "Médecin modifié avec succès",
                            "Modification réussie",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();

                    // Rafraîchir la liste si elle est affichée
                    if (contentPanel.getComponentCount() > 0 && contentPanel.getComponent(0) instanceof VueMedecin) {
                        showMedecinList();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de la modification du médecin",
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

        System.out.println("Formulaire de modification de médecin affiché");
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
    public void showEditSecretaireForm(int id) {
        // Rechercher le secrétaire
        List<Secretaire> secretaires = Secretaire.getAllSecretaires();
        Secretaire secretaire = null;

        for (Secretaire s : secretaires) {
            if (s.getId() == id) {
                secretaire = s;
                break;
            }
        }

        if (secretaire == null) {
            JOptionPane.showMessageDialog(this,
                    "Secrétaire introuvable",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Afficher un formulaire de modification de secrétaire
        JDialog dialog = new JDialog(this, "Modifier un secrétaire", true);
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
        JTextField tfNom = new JTextField(secretaire.getNom(), 20);
        panel.add(tfNom, gbc);

        // Prénom
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Prénom:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfPrenom = new JTextField(secretaire.getPrenom(), 20);
        panel.add(tfPrenom, gbc);

        // Login (non modifiable)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Login:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfLogin = new JTextField(secretaire.getLogin(), 20);
        tfLogin.setEnabled(false); // Le login ne peut pas être modifié
        panel.add(tfLogin, gbc);

        // Note: Le mot de passe n'est pas modifiable dans le formulaire de modification

        // Boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnValider = new JButton("Valider");
        JButton btnAnnuler = new JButton("Annuler");

        final int idSecretaire = id;

        btnValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tfNom.getText().trim().isEmpty() ||
                        tfPrenom.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog,
                            "Veuillez remplir tous les champs",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean result = false;

                try {
                    // Récupérer le secrétaire et modifier ses propriétés
                    Secretaire secretaireAModifier = null;
                    List<Secretaire> secretaires = Secretaire.getAllSecretaires();

                    for (Secretaire s : secretaires) {
                        if (s.getId() == idSecretaire) {
                            secretaireAModifier = s;
                            break;
                        }
                    }

                    if (secretaireAModifier != null) {
                        secretaireAModifier.setNom(tfNom.getText().trim());
                        secretaireAModifier.setPrenom(tfPrenom.getText().trim());
                        result = secretaireAModifier.enregistrer();
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de la modification du secrétaire: " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (result) {
                    JOptionPane.showMessageDialog(dialog,
                            "Secrétaire modifié avec succès",
                            "Modification réussie",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();

                    // Rafraîchir la liste si elle est affichée
                    if (contentPanel.getComponentCount() > 0 && contentPanel.getComponent(0) instanceof VueSecretaire) {
                        showSecretaireList();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de la modification du secrétaire",
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

        System.out.println("Formulaire de modification de secrétaire affiché");
    }

    private void showRendezVousList() {
        contentPanel.removeAll();

        try {
            // Créer la vue
            VueRendezVous vueRendezVous = new VueRendezVous(utilisateur);

            // Créer et stocker le contrôleur
            controleurRendezVous = new ControleurRendezVous(vueRendezVous);

            // IMPORTANT: Lier le contrôleur à la vue
            vueRendezVous.setControleur(controleurRendezVous);

            // Initialiser les écouteurs du formulaire
            controleurRendezVous.attacherEcouteursFormulaire();

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
        if (contentPanel.getComponent(0) instanceof VueRendezVous) {
            VueRendezVous vueRendezVous = (VueRendezVous) contentPanel.getComponent(0);

            // Déclencher l'action d'ajout via le contrôleur
            if (vueRendezVous.getBtnAjouter() != null) {
                vueRendezVous.getBtnAjouter().doClick();
            }

            System.out.println("Formulaire d'ajout de rendez-vous déclenché");
        }
    }

    // Voici les méthodes modifiées dans VuePrincipale.java pour la gestion des rendez-vous

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
                List<RendezVous> rendezVousList = RendezVous.getTousRendezVous();

                boolean result = RendezVous.exporterExcel(rendezVousList, cheminFichier);

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
                List<RendezVous> rendezVousList = RendezVous.getTousRendezVous();

                boolean result = RendezVous.exporterPDF(rendezVousList, cheminFichier);

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

    // Implémentation de la méthode logout corrigée
    private void logout() {
        int confirmer = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir vous déconnecter?", "Confirmation de déconnexion",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmer == JOptionPane.YES_OPTION) {
            System.out.println("Déconnexion confirmée - Fermeture de la vue principale");

            // Nettoyer toutes les références aux contrôleurs
            if (controleurPatient != null) controleurPatient = null;
            if (controleurMedecin != null) controleurMedecin = null;
            if (controleurSecretaire != null) controleurSecretaire = null;
            if (controleurRendezVous != null) controleurRendezVous = null;

            // Effacer les notifications si c'est un médecin
            if (utilisateur.getRole() == Role.MEDECIN) {
                NotificationManager.getInstance().clearNotifications();
                NotificationManager.getInstance().setZoneNotifications(null);
            }

            // Vider le panel de contenu
            if (contentPanel != null) {
                contentPanel.removeAll();
                contentPanel = null;
            }

            // Réafficher la fenêtre d'authentification
            if (vueAuthentification != null) {
                System.out.println("Réaffichage de la vue d'authentification");
                vueAuthentification.afficherFenetreConnexion();
            } else {
                // Si la référence n'existe pas, créer une nouvelle instance
                System.out.println("Création d'une nouvelle vue d'authentification");
                new VueAuthentification();
            }
            this.dispose();
        }
    }

    private void exit() {
        int confirmer = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir quitter l'application?", "Confirmation de fermeture",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmer == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
}