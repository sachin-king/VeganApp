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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignupActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val signBtn = findViewById<Button>(R.id.signupButton)
        val email = findViewById<EditText>(R.id.emailInput)
        val password = findViewById<EditText>(R.id.passwordInput)

        signBtn.setOnClickListener {

            val emailText = email.text.toString()
            val passText = password.text.toString()

            if (emailText.isEmpty()) {
                email.error = "Email required"
            }
            else if (!isValidEmail(emailText)) {
                email.error = "Enter valid email"
            }
            else if (!isValidPassword(passText)) {
                password.error =
                    "Password must contain uppercase, number & special character"
            }
            else {

                auth.createUserWithEmailAndPassword(emailText, passText)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            val user = auth.currentUser
                            val db = FirebaseFirestore.getInstance()

                            // 🔥 SAVE USER DATA TO FIRESTORE
                            val data = hashMapOf(
                                "name" to "User", // 🔥 later input घेऊ शकतोस
                                "email" to user?.email,
                                "location" to ""
                            )

                            db.collection("users")
                                .document(user!!.uid)
                                .set(data)

                            Toast.makeText(
                                this,
                                "Signup Successful",
                                Toast.LENGTH_LONG
                            ).show()

                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()

                        } else {

                            Toast.makeText(
                                this,
                                "Error: " + task.exception?.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        val passwordPattern =
            Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^&+=!]).{8,}$")
        return password.matches(passwordPattern)
    }
}


