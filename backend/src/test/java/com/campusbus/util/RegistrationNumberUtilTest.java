package com.campusbus.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationNumberUtilTest {

    @Test
    void shouldNormalizeWithSpaces() {
        assertEquals("TN01AB1234", RegistrationNumberUtil.normalize("tn 01 ab 1234"));
    }

    @Test
    void shouldNormalizeWithHyphens() {
        assertEquals("TN01AB1234", RegistrationNumberUtil.normalize("TN-01-AB-1234"));
    }

    @Test
    void shouldNormalizeLowercaseWithoutDelimiters() {
        assertEquals("TN01AB1234", RegistrationNumberUtil.normalize("tn01ab1234"));
    }

    @Test
    void shouldTrimWhitespace() {
        assertEquals("TN01AB1234", RegistrationNumberUtil.normalize("  TN01AB1234  "));
    }

    @Test
    void shouldNormalizeMixedFormatting() {
        assertEquals("TN01AB1234", RegistrationNumberUtil.normalize(" tn-01 ab-1234 "));
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(RegistrationNumberUtil.normalize(null));
    }
}
