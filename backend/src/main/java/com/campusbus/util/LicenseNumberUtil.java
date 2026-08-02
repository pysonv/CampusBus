package com.campusbus.util;

public class LicenseNumberUtil {

    private LicenseNumberUtil() {
        // Utility class — prevent instantiation
    }

    /**
     * Normalizes a driver's license number by:
     * 1. Trimming whitespace
     * 2. Converting to uppercase
     * 3. Removing all spaces
     * 4. Removing all hyphens
     *
     * Examples:
     *   "tn 01 2026 0001234"  → "TN0120260001234"
     *   "TN-01-2026-0001234"  → "TN0120260001234"
     */
    public static String normalize(String licenseNumber) {
        if (licenseNumber == null) {
            return null;
        }
        return licenseNumber
                .trim()
                .toUpperCase()
                .replace(" ", "")
                .replace("-", "");
    }
}
