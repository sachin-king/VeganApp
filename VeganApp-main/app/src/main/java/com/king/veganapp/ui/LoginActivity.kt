package com.king.veganapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.king.veganapp.R


/*class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 🔙 Back button enable
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()

        val login = findViewById<Button>(R.id.loginButton)
        val forget = findViewById<TextView>(R.id.forgotText)
        val email = findViewById<EditText>(R.id.emailInput)
        val password = findViewById<EditText>(R.id.passwordInput)

        forget.setOnClickListener {

            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)

        }

        login.setOnClickListener {

            val emailText = email.text.toString()
            val passText = password.text.toString()

            if(emailText.isEmpty()){
                email.error = "Enter Email"
            }
            else if(passText.isEmpty()){
                password.error = "Enter Password"
            }
            else{

                auth.signInWithEmailAndPassword(emailText, passText)
                    .addOnCompleteListener { task ->

                        if(task.isSuccessful){

                            Toast.makeText(this,
                                "Login Successful",
                                Toast.LENGTH_SHORT).show()


                            // 🔥 NEW CODE START (IMPORTANT)
                            val user = auth.currentUser

                            val db = FirebaseFirestore.getInstance()

                            val data = hashMapOf(
                                "name" to "User", // later change करू शकतोस
                                "email" to user?.email,
                                "location" to ""
                            )

                            db.collection("users")
                                .document(user!!.uid)
                                .set(data)

                            startActivity(Intent(this, MainActivity::class.java))
                            finish()

                        }else{

                            Toast.makeText(this,
                                "Login Failed",
                                Toast.LENGTH_SHORT).show()

                        }

                    }

            }

        }

    }

    // 🔙 Back button click
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}*/

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()

        val login = findViewById<Button>(R.id.loginButton)
        val forget = findViewById<TextView>(R.id.forgotText)
        val email = findViewById<EditText>(R.id.emailInput)
        val password = findViewById<EditText>(R.id.passwordInput)

        forget.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        login.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passText = password.text.toString().trim()

            if (emailText.isEmpty()) {
                email.error = "Enter Email"
                return@setOnClickListener
            }

            if (passText.isEmpty()) {
                password.error = "Enter Password"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                        val user = auth.currentUser
                        val userId = user?.uid ?: return@addOnCompleteListener

                        val db = FirebaseFirestore.getInstance()

                        // 🔥 CHECK IF USER EXISTS FIRST
                        val userRef = db.collection("users").document(userId)

                        userRef.get().addOnSuccessListener { document ->

                            if (!document.exists()) {

                                // 🆕 First time login → create user
                                val data = hashMapOf(
                                    "name" to "User",
                                    "email" to user.email,
                                    "location" to ""
                                )

                                userRef.set(data)
                            }

                            // 🚀 Go to Main
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }

                    } else {
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

