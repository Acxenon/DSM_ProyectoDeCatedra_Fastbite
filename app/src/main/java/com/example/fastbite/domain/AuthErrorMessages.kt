package com.example.fastbite.domain

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import java.util.Locale

/** El texto técnico de Firebase nunca se muestra directamente al usuario. */
object AuthErrorMessages {
    fun from(exception: Exception?): String = when (exception) {
        is FirebaseNetworkException -> forCode("NETWORK_REQUEST_FAILED")
        is FirebaseTooManyRequestsException -> forCode("TOO_MANY_REQUESTS")
        is FirebaseAuthException -> forCode(exception.errorCode)
        else -> forCode(null)
    }

    fun forCode(code: String?): String = when (code?.uppercase(Locale.ROOT)?.removePrefix("ERROR_")) {
        "INVALID_EMAIL", "MISSING_EMAIL" -> "Ingresa un correo electrónico válido."
        "WRONG_PASSWORD", "INVALID_CREDENTIAL", "INVALID_LOGIN_CREDENTIALS", "USER_NOT_FOUND" ->
            "El correo o la contraseña no son correctos. Revisa los datos e intenta de nuevo."
        "USER_DISABLED" -> "Esta cuenta está deshabilitada. Comunícate con el administrador."
        "EMAIL_ALREADY_IN_USE", "ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
            "Ya existe una cuenta con este correo. Inicia sesión o recupera tu contraseña."
        "WEAK_PASSWORD" -> "La contraseña no cumple los requisitos. Usa al menos 6 caracteres."
        "TOO_MANY_REQUESTS", "TOO_MANY_ATTEMPTS_TRY_LATER" ->
            "Se realizaron demasiados intentos. Espera unos minutos y vuelve a intentarlo."
        "NETWORK_REQUEST_FAILED", "NETWORK_ERROR" -> "No se pudo conectar. Revisa tu conexión a internet."
        "OPERATION_NOT_ALLOWED", "APP_NOT_AUTHORIZED", "INVALID_API_KEY", "API_NOT_AVAILABLE" ->
            "El servicio de acceso no está disponible en este momento. Intenta más tarde."
        "REQUIRES_RECENT_LOGIN", "USER_TOKEN_EXPIRED", "INVALID_USER_TOKEN" ->
            "Tu sesión venció. Inicia sesión nuevamente."
        else -> "No se pudo completar la solicitud. Intenta de nuevo en unos momentos."
    }
}
