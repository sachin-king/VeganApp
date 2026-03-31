package com.king.veganapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.king.veganapp.R
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import com.king.veganapp.FavoriteManager
import com.king.veganapp.model.Restaurant

class RestaurantDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        val image = findViewById<ImageView>(R.id.detailImage)
        val name = findViewById<TextView>(R.id.detailName)
        val rating = findViewById<TextView>(R.id.detailRating)
        val favBtn = findViewById<Button>(R.id.favBtn)

        // Get Data
        val restName = intent.getStringExtra("name")
        val restImage = intent.getIntExtra("image", 0)
        val restRating = intent.getStringExtra("rating")
        val restDistance = intent.getStringExtra("distance")

        // Set Data
        image.setImageResource(restImage)
        name.text = restName
        rating.text = restRating

        // ❤️ Favorite Button Logic
        favBtn.setOnClickListener {
            val restaurant = Restaurant(
                id = "1",
                image = restImage,
                name = restName ?: "",
                rating = restRating ?: "",
                distance = restDistance ?: ""
            )


            FavoriteManager.favoriteList.add(restaurant)
            Toast.makeText(this, "Added to Favorites ❤️", Toast.LENGTH_SHORT).show()
        }
    }
}

