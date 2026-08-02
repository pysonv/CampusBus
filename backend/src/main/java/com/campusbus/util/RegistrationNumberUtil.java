package com.campusbus.util;

public class RegistrationNumberUtil {

    private RegistrationNumberUtil() {
        // Utility class — prevent instantiation
    }

    /**
     * Normalizes a vehicle registration number by:
     * 1. Trimming whitespace
     * 2. Converting to uppercase
     * 3. Removing all spaces
     * 4. Removing all hyphens
     *
     * Examples:
     *   "tn 01 ab 1234"   → "TN01AB1234"
     *   "TN-01-AB-1234"   → "TN01AB1234"
     *   "tn01ab1234"       → "TN01AB1234"
     */
    public static String normalize(String registrationNumber) {
        if (registrationNumber == null) {
            return null;
        }
        return registrationNumber
                .trim()
                .toUpperCase()
                .replace(" ", "")
                .replace("-", "");
    }
}
