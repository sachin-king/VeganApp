package com.king.veganapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.king.veganapp.R
import android.widget.EditText
import android.widget.Toast

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
       val forget = findViewById<Button>(R.id.resetButton)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val newPassword = findViewById<EditText>(R.id.newPasswordInput)

        forget.setOnClickListener {
            val emailText = emailInput.text.toString()
            val newPassText = newPassword.text.toString()

            val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)

            val savedEmail = sharedPref.getString("email","")

            if(emailText == savedEmail){

                val editor = sharedPref.edit()
                editor.putString("password", newPassText)
                editor.apply()

                Toast.makeText(this,"Password Reset Successful",Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, LoginActivity::class.java))

            }else{

                Toast.makeText(this,"Email not found",Toast.LENGTH_SHORT).show()

            }

        }
    }
}