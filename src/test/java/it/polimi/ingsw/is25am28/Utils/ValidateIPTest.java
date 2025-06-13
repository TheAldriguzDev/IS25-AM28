package it.polimi.ingsw.is25am28.Utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidateIPTest {

    @Test
    void testValidIPAddress() {
        assertTrue(ValidateIP.validateIPAddress("192.168.1.1"));
    }

    @Test
    void testInvalidIPAddressBecauseOfLetter() {
        assertFalse(ValidateIP.validateIPAddress("192.168.1.a"));
    }

    @Test
    void testInvalidIPAddressBecauseOfExtraOctet() {
        assertFalse(ValidateIP.validateIPAddress("192.168.1.1.1"));
    }

    @Test
    void testInvalidIPAddressBecauseOfMissingOctet() {
        assertFalse(ValidateIP.validateIPAddress("192.168.1"));
    }

    @Test
    void testValidIPAddressWithExtraSpaces() {
        assertTrue(ValidateIP.validateIPAddress(" 192 . 168 . 1 . 1 "));
    }

    @Test
    void testInvalidIPAddressBecauseOctetIsTooLarge() {
        assertFalse(ValidateIP.validateIPAddress("192.168.1.300"));
    }

    @Test
    void testInvalidIPAddressBecauseOfNull() {
        assertFalse(ValidateIP.validateIPAddress(null));
    }

    @Test
    void testInvalidIPAddressBecauseOfEmptyString() {
        assertFalse(ValidateIP.validateIPAddress(""));
    }
}