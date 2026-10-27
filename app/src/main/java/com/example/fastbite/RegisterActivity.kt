package com.example.fastbite

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fastbite.domain.AuthErrorMessages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonRegister: Button
    private lateinit var textViewLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("es")
        buttonRegister = findViewById(R.id.btnRegister)
        textViewLogin = findViewById(R.id.textViewLogin)

        val firstNameField = findViewById<EditText>(R.id.txtFirstName)
        val lastNameField = findViewById<EditText>(R.id.txtLastName)
        val emailField = findViewById<EditText>(R.id.txtEmail)
        val passwordField = findViewById<EditText>(R.id.txtPass)

        buttonRegister.setOnClickListener {
            val firstName = firstNameField.text.toString().trim()
            val lastName = lastNameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            // La contraseña se conserva exactamente como la escribió el usuario.
            val password = passwordField.text.toString()

            when {
                firstName.isEmpty() -> {
                    firstNameField.error = "Ingresa tu nombre"
                    firstNameField.requestFocus()
                }
                lastName.isEmpty() -> {
                    lastNameField.error = "Ingresa tu apellido"
                    lastNameField.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailField.error = "Ingresa un correo válido"
                    emailField.requestFocus()
                }
                password.length < 6 -> {
                    passwordField.error = "Usa al menos 6 caracteres"
                    passwordField.requestFocus()
                }
                else -> register(firstName, lastName, email, password)
            }
        }
        textViewLogin.setOnClickListener { goToLogin() }
    }

    private fun register(firstName: String, lastName: String, email: String, password: String) {
        setRegistering(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { accountTask ->
                if (!accountTask.isSuccessful) {
                    setRegistering(false)
                    Toast.makeText(this, AuthErrorMessages.from(accountTask.exception), Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener
                }

                val user = accountTask.result?.user
                if (user == null) {
                    setRegistering(false)
                    Toast.makeText(this, "No se pudo obtener el usuario. Intenta iniciar sesión.", Toast.LENGTH_LONG).show()
                    goToLogin()
                    return@addOnCompleteListener
                }

                val profile = UserProfileChangeRequest.Builder()
                    .setDisplayName("$firstName $lastName")
                    .build()
                // Esperamos a guardar el nombre completo antes de finalizar el registro.
                user.updateProfile(profile).addOnCompleteListener { profileTask ->
                    auth.signOut()
                    val message = if (profileTask.isSuccessful) {
                        "Cuenta creada con éxito. Inicia sesión."
                    } else {
                        "Tu cuenta fue creada, pero no se pudo guardar el nombre. Inicia sesión y guárdalo desde tu perfil."
                    }
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    goToLogin()
                }
            }
    }

    private fun setRegistering(registering: Boolean) {
        buttonRegister.isEnabled = !registering
        textViewLogin.isEnabled = !registering
        buttonRegister.text = if (registering) "Creando cuenta…" else "Registrar"
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
