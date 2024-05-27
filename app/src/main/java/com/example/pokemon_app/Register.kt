package com.example.pokemon_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import services.AuthService

class Register : AppCompatActivity() {

    private lateinit var etRegisterUsername: EditText
    private lateinit var etRegisterPassword: EditText
    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authService = AuthService(this)

        etRegisterUsername = findViewById(R.id.usernameFieldRegister)
        etRegisterPassword = findViewById(R.id.passwordFieldRegister)
    }

    public fun createAccount(www: View) {
        val username = etRegisterUsername.text.toString().trim()
        val password = etRegisterPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        } else {
            val success = authService.registerUser(username, password)
            if (success) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    public fun navigateToLogin(www: View) {
        val intent = Intent(this, Login::class.java);
        startActivity(intent)
        finish()
    }
}