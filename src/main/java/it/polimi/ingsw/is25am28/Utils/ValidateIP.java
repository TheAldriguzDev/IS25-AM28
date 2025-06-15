package it.polimi.ingsw.is25am28.Utils;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;

public class ValidateIP {
    /**
     * Validates whether the given string is a valid IPv4 address.
     * A valid IPv4 address consists of four octets separated by dots, with each octet being a number
     * in the range of 0-255.
     *
     * @param ipAddress the string to be validated as an IPv4 address
     * @return true if the input string is a valid IPv4 address; false otherwise
     */
    public static boolean validateIPAddress(String ipAddress) {
        if (ipAddress == null) return false;

        ipAddress = ipAddress.replaceAll("\\s+", "");
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
