package com.example.fastbite

import com.example.fastbite.domain.AuthErrorMessages
import com.example.fastbite.domain.OrderTransitions
import com.google.firebase.auth.FirebaseAuthException
import org.junit.Assert.*
import org.junit.Test

class AuthAndOrderFlowTest {
    @Test fun invalidEmailIsTranslated() {
        val error = FirebaseAuthException("ERROR_INVALID_EMAIL", "The email address is badly formatted.")
        assertEquals("Ingresa un correo electrónico válido.", AuthErrorMessages.from(error))
    }
    @Test fun wrongCredentialsAreTranslated() {
        assertEquals(AuthErrorMessages.forCode("ERROR_WRONG_PASSWORD"), AuthErrorMessages.forCode("ERROR_INVALID_LOGIN_CREDENTIALS"))
        assertTrue(AuthErrorMessages.forCode("ERROR_WRONG_PASSWORD").contains("contraseña"))
    }
    @Test fun networkAndRateLimitAreTranslated() {
        assertTrue(AuthErrorMessages.forCode("ERROR_NETWORK_REQUEST_FAILED").contains("conexión"))
        assertTrue(AuthErrorMessages.forCode("ERROR_TOO_MANY_REQUESTS").contains("Espera"))
    }
    @Test fun unknownErrorsNeverExposeEnglishSdkMessages() {
        val message = AuthErrorMessages.from(Exception("Technical service error in English"))
        assertEquals("No se pudo completar la solicitud. Intenta de nuevo en unos momentos.", message)
    }
    @Test fun registeredEmailAndWeakPasswordHaveHelpfulMessages() {
        assertTrue(AuthErrorMessages.forCode("ERROR_EMAIL_ALREADY_IN_USE").contains("recupera"))
        assertTrue(AuthErrorMessages.forCode("ERROR_WEAK_PASSWORD").contains("6 caracteres"))
    }
    @Test fun pendingAndConfirmedOrdersCanBeDelivered() {
        assertTrue(OrderTransitions.canChange("PENDING", "DELIVERED"))
        assertTrue(OrderTransitions.canChange("CONFIRMED", "DELIVERED"))
    }
    @Test fun deliveredOrdersCannotBeCancelledOrDeliveredAgain() {
        assertFalse(OrderTransitions.canChange("DELIVERED", "CANCELLED"))
        assertFalse(OrderTransitions.canChange("DELIVERED", "DELIVERED"))
    }
    @Test fun cancelledOrdersCannotBeDelivered() {
        assertFalse(OrderTransitions.canChange("CANCELLED", "DELIVERED"))
    }
    @Test fun onlyPendingOrdersCanBeCancelled() {
        assertTrue(OrderTransitions.canChange("PENDING", "CANCELLED"))
        assertFalse(OrderTransitions.canChange("CONFIRMED", "CANCELLED"))
    }
    @Test fun completedOrdersCannotBeReopened() {
        assertFalse(OrderTransitions.canChange("DELIVERED", "PENDING"))
        assertFalse(OrderTransitions.canChange("CANCELLED", "PENDING"))
    }
}
