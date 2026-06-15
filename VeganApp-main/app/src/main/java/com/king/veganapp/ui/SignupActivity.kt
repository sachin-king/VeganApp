package com.king.veganapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.king.veganapp.R
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 🔙 Back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()

        val signBtn = findViewById<Button>(R.id.signupButton)
        val email = findViewById<EditText>(R.id.emailInput)
        val password = findViewById<EditText>(R.id.passwordInput)
        val name = findViewById<EditText>(R.id.nameInput)

        signBtn.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passText = password.text.toString().trim()
            val nameText = name.text.toString().trim()

            // ✅ VALIDATION
            if (nameText.isEmpty()) {
                name.error = "Name required"
                name.requestFocus()
                return@setOnClickListener
            }

            if (emailText.isEmpty()) {
                email.error = "Email required"
                email.requestFocus()
                return@setOnClickListener
            }

            if (!isValidEmail(emailText)) {
                email.error = "Enter valid email"
                email.requestFocus()
                return@setOnClickListener
            }

            if (!isValidPassword(passText)) {
                password.error =
                    "Password must contain uppercase, number & special character"
                password.requestFocus()
                return@setOnClickListener
            }

            // 🔥 FIREBASE AUTH
            auth.createUserWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        val db = FirebaseFirestore.getInstance()

                        // ✅ FIXED LINE (MOST IMPORTANT)
                        val data = hashMapOf(
                            "name" to nameText,
                            "email" to user?.email,
                            "location" to ""
                        )

                        db.collection("users")
                            .document(user!!.uid)
                            .set(data)
                            .addOnSuccessListener {

                                Toast.makeText(
                                    this,
                                    "Signup Successful",
                                    Toast.LENGTH_LONG
                                ).show()

                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Firestore Error: ${it.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                    } else {
                        Toast.makeText(
                            this,
                            "Auth Error: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    // 📧 Email validation
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // 🔐 Password validation
    private fun isValidPassword(password: String): Boolean {
        val pattern = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^&+=!]).{8,}$")
        return password.matches(pattern)
    }
}



