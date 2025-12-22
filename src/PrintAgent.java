import java.io.File;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;

public class PrintAgent {

    public static void main(String[] args) {
        try {
            File pdfFile = new File("pdf/test.pdf");

            PDDocument document = PDDocument.load(pdfFile);

            PrintService printer = PrintServiceLookup.lookupDefaultPrintService();
            if (printer == null) {
                System.out.println("No printer found");
                return;
            }

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(printer);
            job.setPrintable(new PDFPrintable(document));

            job.print();

            document.close();
            System.out.println("Print job sent successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
