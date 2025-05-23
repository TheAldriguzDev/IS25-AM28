package it.polimi.ingsw.is25am28.Utils;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;

public class ValidateIP {
    public static boolean validateIPAddress(String ipAddress) {
        String[] values;

        if (ipAddress != null && !ipAddress.isEmpty()) {
            values = ipAddress.trim().split("\\.");

            if (values.length != 4) return false;

            try {
                for (String value : values) {
                    int octet = Integer.parseInt(value);

                    if ((octet >> 8) != 0) {
                        return false;
                    }
                }
            }
            catch (NumberFormatException e) {
                System.out.println(
                        PrintUtils.addColor(
                                "[ERROR] [Invalid input] Please insert a number.",
                                ANSIColors.RED
                        )
                );

                return false;
            }

            return true;
        }

        return false;
    }
}
