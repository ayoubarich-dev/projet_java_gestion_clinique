package fr.clinique.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import fr.clinique.model.RendezVous;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class PDFExporter {

    public boolean exporter(List<RendezVous> rendezVous, String cheminFichier) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(cheminFichier));
            document.open();

            // Titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Paragraph title = new Paragraph("Liste des Rendez-vous", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Espace

            // Tableau
            PdfPTable table = new PdfPTable(6); // 6 colonnes
            table.setWidthPercentage(100);

            // En-tête du tableau
            String[] headers = {"ID", "Patient", "Médecin", "Date", "Heure", "Motif"};
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Contenu du tableau
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

            for (RendezVous rv : rendezVous) {
                table.addCell(new Phrase(String.valueOf(rv.getId()), contentFont));
                table.addCell(new Phrase(rv.getPatient().getPrenom() + " " + rv.getPatient().getNom(), contentFont));
                table.addCell(new Phrase("Dr. " + rv.getMedecin().getPrenom() + " " + rv.getMedecin().getNom(), contentFont));
                table.addCell(new Phrase(dateFormat.format(rv.getDate()), contentFont));
                table.addCell(new Phrase(rv.getHeure(), contentFont));
                table.addCell(new Phrase(rv.getMotif(), contentFont));
            }

            document.add(table);

            // Pied de page
            document.add(new Paragraph(" ")); // Espace
            Paragraph footer = new Paragraph("Document généré le " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}