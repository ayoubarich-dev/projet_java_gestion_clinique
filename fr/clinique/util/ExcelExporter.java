package fr.clinique.util;

import fr.clinique.model.RendezVous;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExcelExporter {

    public boolean exporter(List<RendezVous> rendezVous, String cheminFichier) {
        // Utiliser HSSF (.xls) au lieu de XSSF (.xlsx) pour éviter les problèmes de dépendances
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Rendez-vous");

            // Créer l'en-tête
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Patient", "Médecin", "Date", "Heure", "Motif"};

            // Style pour l'en-tête
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Remplir les données
            int rowNum = 1;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            for (RendezVous rv : rendezVous) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(rv.getId());
                row.createCell(1).setCellValue(rv.getPatient().getPrenom() + " " + rv.getPatient().getNom());
                row.createCell(2).setCellValue("Dr. " + rv.getMedecin().getPrenom() + " " + rv.getMedecin().getNom());
                row.createCell(3).setCellValue(dateFormat.format(rv.getDate()));
                row.createCell(4).setCellValue(rv.getHeure());
                row.createCell(5).setCellValue(rv.getMotif());
            }

            // Ajuster la largeur des colonnes
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // S'assurer que le nom de fichier a la bonne extension
            if (!cheminFichier.endsWith(".xls")) {
                cheminFichier = cheminFichier.replaceAll("\\.[^.]*$", "") + ".xls";
            }

            // Écrire le résultat dans un fichier
            try (FileOutputStream fileOut = new FileOutputStream(cheminFichier)) {
                workbook.write(fileOut);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}