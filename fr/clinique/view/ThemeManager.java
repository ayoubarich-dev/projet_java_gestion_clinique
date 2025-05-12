package fr.clinique.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ThemeManager {

    // Couleurs du thème
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);  // Bleu médical
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219); // Bleu clair
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96);   // Vert succès
    public static final Color DANGER_COLOR = new Color(231, 76, 60);    // Rouge danger
    public static final Color WARNING_COLOR = new Color(241, 196, 15);  // Jaune avertissement
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Gris très clair
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);     // Gris foncé
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141); // Gris moyen
    public static final Color WHITE = Color.WHITE;

    // Polices
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    // Dimensions
    public static final Dimension BUTTON_SIZE = new Dimension(120, 35);
    public static final Dimension FIELD_SIZE = new Dimension(250, 30);
    public static final int BORDER_PADDING = 20;
    public static final int COMPONENT_SPACING = 10;

    /**
     * Applique le thème à toute l'application
     */
    public static void applyTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Configuration des couleurs
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", WHITE);
            UIManager.put("Button.font", BUTTON_FONT);
            UIManager.put("Label.font", NORMAL_FONT);
            UIManager.put("TextField.font", NORMAL_FONT);
            UIManager.put("Table.font", NORMAL_FONT);
            UIManager.put("Table.gridColor", new Color(218, 223, 225));
            UIManager.put("Table.alternateRowColor", new Color(245, 247, 249));
            UIManager.put("TableHeader.background", PRIMARY_COLOR);
            UIManager.put("TableHeader.foreground", WHITE);
            UIManager.put("TableHeader.font", BUTTON_FONT);

            // Bordures
            UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                    new LineBorder(SECONDARY_COLOR, 1),
                    new EmptyBorder(5, 10, 5, 10)
            ));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crée un bouton stylisé
     */
    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(WHITE);
        button.setFont(BUTTON_FONT);
        button.setPreferredSize(BUTTON_SIZE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = bgColor;
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(brighter(originalColor));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    /**
     * Crée un champ de texte stylisé
     */
    public static JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setPreferredSize(FIELD_SIZE);
        field.setFont(NORMAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SECONDARY_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    /**
     * Crée un champ de mot de passe stylisé
     */
    public static JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setPreferredSize(FIELD_SIZE);
        field.setFont(NORMAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SECONDARY_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    /**
     * Crée un label de titre
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT_PRIMARY);
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    /**
     * Crée un label de sous-titre
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUBTITLE_FONT);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /**
     * Crée un panneau avec titre
     */
    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                SUBTITLE_FONT,
                TEXT_PRIMARY
        ));
        panel.setBackground(WHITE);
        return panel;
    }

    /**
     * Crée une table stylisée
     */
    public static JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(NORMAL_FONT);
        table.setRowHeight(30);
        table.setGridColor(new Color(218, 223, 225));
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(WHITE);

        // Header style
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(WHITE);
        header.setFont(BUTTON_FONT);
        header.setPreferredSize(new Dimension(0, 35));

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? WHITE : new Color(245, 247, 249));
                }
                return c;
            }
        });

        return table;
    }

    /**
     * Éclaircit une couleur
     */
    private static Color brighter(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hsb[2] = Math.min(1.0f, hsb[2] * 1.2f); // Augmente la luminosité
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    /**
     ��� Crée une barre de recherche stylisée
     */
    public static JPanel createSearchBar() {
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.setBackground(WHITE);
        searchBar.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setFont(NORMAL_FONT);

        JTextField searchField = createStyledTextField(20);
        JButton searchButton = createStyledButton("Rechercher", PRIMARY_COLOR);
        JButton refreshButton = createStyledButton("Rafraîchir", SECONDARY_COLOR);

        searchBar.add(searchLabel);
        searchBar.add(searchField);
        searchBar.add(searchButton);
        searchBar.add(refreshButton);

        return searchBar;
    }
}