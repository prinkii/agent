import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.file.Files;

import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PageRanges;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;

public class PrintAgentFromUrl {

    public static void main(String[] args) {

        // ===============================
        // INPUT (later from n8n job)
        // ===============================
        String fileUrl = "https://drive.google.com/uc?export=download&id=1k9CIAejU35__ApbTjfziUMSyBY8eOn0q";

        boolean isColor = false;
        boolean customPage = false;
        int startPage = 1;
        int endPage = 2;
        int copies = 1;

        String COLOR_PRINTER = "Kiosk_ColorOnly";
        String BW_PRINTER    = "Kiosk_BlackOnly";

        // ===============================
        // PROCESS
        // ===============================
        try {
            File downloadedPdf = downloadPdf(fileUrl);

            printPdf(
                downloadedPdf,
                isColor,
                customPage,
                startPage,
                endPage,
                copies,
                COLOR_PRINTER,
                BW_PRINTER
            );

            // Optional: delete file after print
            downloadedPdf.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================================
    // DOWNLOAD PDF FROM URL
    // ======================================================
    private static File downloadPdf(String fileUrl) throws Exception {

        URL url = new URL(fileUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException(
                "Failed to download file. HTTP " + conn.getResponseCode()
            );
        }

        File tempFile = Files.createTempFile("print-job-", ".pdf").toFile();

        try (InputStream in = conn.getInputStream();
             FileOutputStream out = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }

        System.out.println("Downloaded PDF to: " + tempFile.getAbsolutePath());
        return tempFile;
    }

    // ======================================================
    // PRINT PDF
    // ======================================================
    private static void printPdf(
            File pdfFile,
            boolean isColor,
            boolean customPage,
            int startPage,
            int endPage,
            int copies,
            String COLOR_PRINTER,
            String BW_PRINTER
    ) throws Exception {

        PDDocument document = PDDocument.load(pdfFile);

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

        // ===============================
        // JOB MONITOR (your existing logic)
        // ===============================
        boolean wasInQueue = false;
        while (true) {
            boolean inQueue =
                    PrintJobMonitor.isJobInQueue(printer.getName());

            if (inQueue) {
                wasInQueue = true;
                System.out.println("IN_QUEUE");
            } else if (wasInQueue) {
                System.out.println("COMPLETED");
                break;
            }
            Thread.sleep(1000);
        }

        System.out.println(
            "Printed successfully via: " + selectedPrinter
        );
    }

    // ======================================================
    // FIND PRINTER
    // ======================================================
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
