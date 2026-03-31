package com.king.veganapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.king.veganapp.R


class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

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
                            // 🔥 NEW CODE END

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
}
