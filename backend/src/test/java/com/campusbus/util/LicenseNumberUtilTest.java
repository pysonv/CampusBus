package com.campusbus.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LicenseNumberUtilTest {

    @Test
    void shouldNormalizeWithSpaces() {
        assertEquals("TN0120260001234", LicenseNumberUtil.normalize("tn 01 2026 0001234"));
    }

    @Test
    void shouldNormalizeWithHyphens() {
        assertEquals("TN0120260001234", LicenseNumberUtil.normalize("TN-01-2026-0001234"));
    }

    @Test
    void shouldTrimWhitespace() {
        assertEquals("TN0120260001234", LicenseNumberUtil.normalize("  TN0120260001234  "));
    }

    @Test
    void shouldNormalizeMixedFormatting() {
        assertEquals("TN0120260001234", LicenseNumberUtil.normalize(" tn-01 2026-0001234 "));
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(LicenseNumberUtil.normalize(null));
    }
}
