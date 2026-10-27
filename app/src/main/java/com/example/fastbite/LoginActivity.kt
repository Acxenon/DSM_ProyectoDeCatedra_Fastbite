package com.example.fastbite

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fastbite.domain.AuthErrorMessages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var tvForgot: TextView
    private var recoveryDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("es")
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        tvForgot = findViewById(R.id.tvForgot)
        val emailField = findViewById<EditText>(R.id.txtEmailAddress)
        val passwordField = findViewById<EditText>(R.id.txtPassword)

        btnLogin.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()
            when {
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailField.error = "Ingresa un correo electrónico válido"
                    emailField.requestFocus()
                }
                password.isEmpty() -> {
                    passwordField.error = "Ingresa tu contraseña"
                    passwordField.requestFocus()
                }
                else -> login(email, password)
            }
        }
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        tvForgot.setOnClickListener { showPasswordRecovery(emailField.text.toString().trim()) }
    }

    private fun login(email: String, password: String) {
        setSigningIn(true)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (isFinishing || isDestroyed) return@addOnCompleteListener
            setSigningIn(false)
            if (task.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, AuthErrorMessages.from(task.exception), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setSigningIn(signingIn: Boolean) {
        btnLogin.isEnabled = !signingIn
        tvRegister.isEnabled = !signingIn
        tvForgot.isEnabled = !signingIn
        btnLogin.text = if (signingIn) "Iniciando sesión…" else getString(R.string.login_button)
    }

    private fun showPasswordRecovery(currentEmail: String) {
        if (recoveryDialog?.isShowing == true) return
        val view = layoutInflater.inflate(R.layout.dialog_password_recovery, null, false)
        val email = view.findViewById<EditText>(R.id.recovery_email)
        email.setText(currentEmail)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Recuperar contraseña")
            .setView(view)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Enviar enlace", null)
            .create()
        recoveryDialog = dialog
        dialog.setOnDismissListener { recoveryDialog = null }
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val address = email.text.toString().trim()
                if (!Patterns.EMAIL_ADDRESS.matcher(address).matches()) {
                    email.error = "Ingresa un correo electrónico válido"
                    email.requestFocus()
                    return@setOnClickListener
                }
                val send = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                send.isEnabled = false
                cancel.isEnabled = false
                email.isEnabled = false
                send.text = "Enviando…"
                dialog.setCancelable(false)
                auth.sendPasswordResetEmail(address).addOnCompleteListener { task ->
                    if (isFinishing || isDestroyed) return@addOnCompleteListener
                    val unknownEmail = (task.exception as? FirebaseAuthException)?.errorCode == "ERROR_USER_NOT_FOUND"
                    if (task.isSuccessful || unknownEmail) {
                        dialog.dismiss()
                        AlertDialog.Builder(this)
                            .setTitle("Revisa tu correo")
                            .setMessage("Si existe una cuenta con ese correo, recibirás un enlace para cambiar tu contraseña. Revisa también la carpeta de correo no deseado.")
                            .setPositiveButton("Entendido", null)
                            .show()
                    } else {
                        send.isEnabled = true
                        cancel.isEnabled = true
                        email.isEnabled = true
                        send.text = "Enviar enlace"
                        dialog.setCancelable(true)
                        Toast.makeText(this, AuthErrorMessages.from(task.exception), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        dialog.show()
    }

    override fun onDestroy() {
        recoveryDialog?.dismiss()
        super.onDestroy()
    }
}
