import java.io.File;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.Chromaticity;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;

public class PrintAgent {

    public static void main(String[] args) {
        try {
            File pdfFile = new File("D:/Printer/Codes/agent/pdf/1.pdf");
            PDDocument document = PDDocument.load(pdfFile);

            PrintService printer = PrintServiceLookup.lookupDefaultPrintService();

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(printer);
            job.setPrintable(new PDFPrintable(document));

            boolean customPage = true;
            int startPage = 1;
            int endPage = 4;

            PrintRequestAttributeSet attributes =
                    new HashPrintRequestAttributeSet();

            attributes.add(new Copies(1));

            if (customPage) {
                attributes.add(new PageRanges(startPage, endPage));
            }



            job.print(attributes);
            document.close();

            System.out.println("Printed with copies");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
