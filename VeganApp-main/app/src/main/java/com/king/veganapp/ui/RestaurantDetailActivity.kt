package com.king.veganapp.ui






import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.king.veganapp.R
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import com.king.veganapp.FavoriteManager
import com.king.veganapp.model.Restaurant

/*class RestaurantDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val image = findViewById<ImageView>(R.id.detailImage)
        val name = findViewById<TextView>(R.id.detailName)
        val rating = findViewById<TextView>(R.id.detailRating)
        val favBtn = findViewById<Button>(R.id.favBtn)

        val restName = intent.getStringExtra("name")
        val restImage = intent.getIntExtra("image", 0)
        val restRating = intent.getStringExtra("rating")
        val restDistance = intent.getStringExtra("distance")

        image.setImageResource(restImage)
        name.text = restName
        rating.text = restRating

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

        // 🔙 System back (optional safety)
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}*/
/*class RestaurantDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val image = findViewById<ImageView>(R.id.detailImage)
        val name = findViewById<TextView>(R.id.detailName)
        val rating = findViewById<TextView>(R.id.detailRating)
        val distance = findViewById<TextView>(R.id.detailDistance) // 🔥 ADD THIS IN XML ALSO
        val favBtn = findViewById<Button>(R.id.favBtn)

        // 🔥 Get data safely
        val restId = intent.getStringExtra("id") ?: ""
        val restName = intent.getStringExtra("name") ?: ""
        val restImage = intent.getIntExtra("image", 0)
        val restRating = intent.getStringExtra("rating") ?: ""
        val restDistance = intent.getStringExtra("distance") ?: ""

        // 🔥 Set data
        image.setImageResource(restImage)
        name.text = restName
        rating.text = restRating
        distance.text = restDistance

        // 🔥 Check already favorite or not
        var isFavorite = FavoriteManager.favoriteList.any { it.id == restId }

        updateFavButton(favBtn, isFavorite)

        // ❤️ Favorite Toggle
        favBtn.setOnClickListener {

            if (isFavorite) {
                // 🔥 Remove from favorites
                FavoriteManager.favoriteList.removeAll { it.id == restId }
                Toast.makeText(this, "Removed from Favorites ❌", Toast.LENGTH_SHORT).show()
                isFavorite = false
            } else {
                // 🔥 Add to favorites
                val restaurant = Restaurant(
                    id = restId,
                    image = restImage,
                    name = restName,
                    rating = restRating,
                    distance = restDistance
                )

                FavoriteManager.favoriteList.add(restaurant)
                Toast.makeText(this, "Added to Favorites ❤️", Toast.LENGTH_SHORT).show()
                isFavorite = true
            }

            updateFavButton(favBtn, isFavorite)
        }

        // 🔙 Back safety
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    // 🔥 Button UI change
    private fun updateFavButton(button: Button, isFav: Boolean) {
        if (isFav) {
            button.text = "Remove Favorite ❌"
        } else {
            button.text = "Add to Favorite ❤️"
        }
    }

    // 🔙 Toolbar back
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}*/

class RestaurantDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val image = findViewById<ImageView>(R.id.detailImage)
        val name = findViewById<TextView>(R.id.detailName)
        val rating = findViewById<TextView>(R.id.detailRating)
        val distance = findViewById<TextView>(R.id.detailDistance)
        val address = findViewById<TextView>(R.id.detailAddress)
        val favBtn = findViewById<Button>(R.id.favBtn)

        // 🔥 SAFE DATA GET
        val restId = intent.getStringExtra("id") ?: System.currentTimeMillis().toString()
        val restName = intent.getStringExtra("name") ?: "Unknown Restaurant"
        val restRating = intent.getStringExtra("rating") ?: "⭐ Not Available"
        val restDistance = intent.getStringExtra("distance") ?: "Distance N/A"
        val restAddress = intent.getStringExtra("address") ?: "Address not available"

        // 🔥 IMAGE FIX (DEFAULT IMAGE)
        image.setImageResource(R.drawable.rest1)

        // 🔥 SET DATA
        name.text = restName
        rating.text = restRating
        distance.text = restDistance
        address.text = restAddress

        // 🔥 FAVORITE CHECK
        var isFavorite = FavoriteManager.favoriteList.any { it.id == restId }

        updateFavButton(favBtn, isFavorite)

        // ❤️ FAVORITE BUTTON
        favBtn.setOnClickListener {

            if (isFavorite) {

                FavoriteManager.favoriteList.removeAll { it.id == restId }
                Toast.makeText(this, "Removed from Favorites ❌", Toast.LENGTH_SHORT).show()
                isFavorite = false

            } else {

                val restaurant = Restaurant(
                    id = restId,
                    image = R.drawable.rest1, // 🔥 default image
                    name = restName,
                    rating = restRating,
                    distance = restDistance
                )

                FavoriteManager.favoriteList.add(restaurant)
                Toast.makeText(this, "Added to Favorites ❤️", Toast.LENGTH_SHORT).show()
                isFavorite = true
            }

            updateFavButton(favBtn, isFavorite)
        }

        // 🔙 BACK HANDLE
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    // 🔥 BUTTON UI
    private fun updateFavButton(button: Button, isFav: Boolean) {
        if (isFav) {
            button.text = "Remove Favorite ❌"
        } else {
            button.text = "Add to Favorite ❤️"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}



