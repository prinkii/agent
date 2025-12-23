import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PrintJobMonitor {

    public static boolean isJobInQueue(String printerName) {
        try {
            String command =
                "powershell.exe -Command " +
                "\"Get-PrintJob -PrinterName '" +
                printerName +
                "' | Out-String\"";

            Process process =
                Runtime.getRuntime().exec(command);

            BufferedReader reader =
                new BufferedReader(
                    new InputStreamReader(
                        process.getInputStream()
                    )
                );

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    return true; // at least one job exists
                }
            }

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
