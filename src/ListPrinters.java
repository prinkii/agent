import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class ListPrinters {

    public static void main(String[] args) {
        PrintService[] printers =
                PrintServiceLookup.lookupPrintServices(null, null);

        System.out.println("Available printers (Java view):");

        if (printers.length == 0) {
            System.out.println("No printers found.");
            return;
        }

        for (int i = 0; i < printers.length; i++) {
            System.out.println(
                (i + 1) + ". " + printers[i].getName()
            );
        }
    }
}
