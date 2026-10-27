package com.example.fastbite

import com.example.fastbite.domain.SalvadorPhone
import org.junit.Assert.*
import org.junit.Test

class SalvadorPhoneTest {
    @Test fun acceptsExactlyEightAsciiDigits() { assertTrue(SalvadorPhone.isValid("76543210")) }
    @Test fun rejectsIncompleteOrLongNumbers() {
        assertFalse(SalvadorPhone.isValid(""))
        assertFalse(SalvadorPhone.isValid("7654321"))
        assertFalse(SalvadorPhone.isValid("765432109"))
    }
    @Test fun rejectsLettersSpacesAndCountryCodeInsideLocalNumber() {
        listOf("7654a210", "7654 3210", "+50376543210", "50376543210").forEach { assertFalse(SalvadorPhone.isValid(it)) }
    }
    @Test fun recognizesLegacyInternationalNumbers() {
        assertEquals("76543210", SalvadorPhone.forEditing("+503 7654-3210"))
        assertEquals("76543210", SalvadorPhone.forEditing("50376543210"))
    }
    @Test fun doesNotTruncateInvalidSavedNumbers() { assertEquals("", SalvadorPhone.forEditing("457975231564")) }
    @Test fun displaysCountryCodeWithoutChangingStoredValue() { assertEquals("+503 76543210", SalvadorPhone.display("76543210")) }
}
