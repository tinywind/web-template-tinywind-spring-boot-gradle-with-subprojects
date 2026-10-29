package org.tinywind.server.security;

import org.junit.jupiter.api.Test;
import org.tinywind.server.util.NetworkInfo;

import static org.junit.jupiter.api.Assertions.*;

class NetworkInfoSecurityTest {
    @Test void getsHardwareAddressWithoutPlatformCommands() throws Exception {
        String originalOs = System.getProperty("os.name");
        try {
            System.setProperty("os.name", "unrecognized-platform");
            String address = NetworkInfo.getMacAddress();
            assertTrue(address.matches("[0-9A-F]{2}(-[0-9A-F]{2}){5}"));
            assertEquals(12, NetworkInfo.getShortMacAddress().length());
        } finally { System.setProperty("os.name", originalOs); }
    }
}
