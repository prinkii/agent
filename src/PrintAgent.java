import java.io.File;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PageRanges;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;

public class PrintAgent {

    public static void main(String[] args) {
        try {
            File pdfFile = new File("D:/Printer/Codes/agent/pdf/test1.pdf");
            PDDocument document = PDDocument.load(pdfFile);

            boolean isColor = false;       // true = Color, false = B/W
            boolean customPage = true;
            int startPage = 1;
            int endPage = 2;
            int copies = 1;

            String COLOR_PRINTER = "Kiosk_ColorOnly";
            String BW_PRINTER    = "Kiosk_BlackOnly";

            String selectedPrinter =
                    isColor ? COLOR_PRINTER : BW_PRINTER;

            PrintService printer = findPrinter(selectedPrinter);

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(printer);
            job.setPrintable(new PDFPrintable(document));

            PrintRequestAttributeSet attrs =
                    new HashPrintRequestAttributeSet();

            attrs.add(new Copies(copies));

            if (customPage) {
                attrs.add(new PageRanges(startPage, endPage));
            }

            job.print(attrs);
            document.close();

            System.out.println(
                "Printed successfully via: " + selectedPrinter
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PrintService findPrinter(String name) {
        for (PrintService ps :
                PrintServiceLookup.lookupPrintServices(null, null)) {
            if (ps.getName().equalsIgnoreCase(name)) {
                return ps;
            }
        }
        throw new RuntimeException("Printer not found: " + name);
    }
}
