package com.king.veganapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import com.google.firebase.auth.FirebaseAuth
import com.king.veganapp.R

class AuthActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        auth = FirebaseAuth.getInstance()

        val emailBtn = findViewById<Button>(R.id.emailSignupBtn)
        val loginText = findViewById<TextView>(R.id.loginText)

        emailBtn.setOnClickListener {

            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)

        }

        loginText.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        /*if(auth.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()*/
        Handler(Looper.getMainLooper()).postDelayed({
            if (auth.currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }, 500) // 0.5 sec delay → AuthActivity visible first
    }
}
