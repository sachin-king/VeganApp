package com.king.veganapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.king.veganapp.R
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import java.io.IOException


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val btn = findViewById<Button>(R.id.btnStart)

        val card = findViewById<CardView>(R.id.cardFood)


        val splashImage = findViewById<ImageView>(R.id.splashImage) // Add this in XML

        // 🔹 Load image from assets folder
        try {
            val inputStream = assets.open("malai_kofta.jpg") // तुझ्या image name नुसार
            val bitmap = BitmapFactory.decodeStream(inputStream)
            splashImage.setImageBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        card.alpha = 0f
        card.animate().alpha(1f).setDuration(1200).start()


        // Button click → Home  Activity
        btn.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

        // Auto move after 3 seconds (optional)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }, 5000)
    }
}
