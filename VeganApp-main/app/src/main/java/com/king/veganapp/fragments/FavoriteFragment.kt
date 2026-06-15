package com.king.veganapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.king.veganapp.FavoriteManager
import com.king.veganapp.R
import com.king.veganapp.adaptor.RestaurantAdapter
import com.king.veganapp.model.Restaurant
import com.king.veganapp.ui.RestaurantDetailActivity

/*class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.favRecycler)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = RestaurantAdapter(
            FavoriteManager.favoriteList,

            onFavoriteClick = { restaurant, position ->
                // ❤ favorite remove कर
                FavoriteManager.favoriteList.removeAt(position)
                recyclerView.adapter?.notifyItemRemoved(position)
            },

            onItemClick = { restaurant ->
                val intent = Intent(requireContext(), RestaurantDetailActivity::class.java)

                intent.putExtra("name", restaurant.name)
                intent.putExtra("image", restaurant.image)
                intent.putExtra("rating", restaurant.rating)
                intent.putExtra("distance", restaurant.distance)
                intent.putExtra("isVegan", restaurant.isVegan)
                intent.putExtra("isOpen", restaurant.isOpen)

                startActivity(intent)
            }
        )

    }
}*/

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter
    private val favList = ArrayList<Restaurant>()

    private val db = FirebaseFirestore.getInstance()
    private val userId
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.favRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = RestaurantAdapter(
            favList,

            // ❤️ REMOVE FROM FIRESTORE
            /*onFavoriteClick = { restaurant, _ ->
                removeFromFavorite(restaurant)
            },*/

            // 📄 OPEN DETAIL
            onItemClick = { restaurant ->
                val intent = Intent(requireContext(), RestaurantDetailActivity::class.java)

                intent.putExtra("id", restaurant.id)
                intent.putExtra("name", restaurant.name)
                intent.putExtra("image", restaurant.image)
                intent.putExtra("rating", restaurant.rating)
                intent.putExtra("distance", restaurant.distance)
                intent.putExtra("isVegan", restaurant.isVegan)
                intent.putExtra("isOpen", restaurant.isOpen)

                startActivity(intent)
            },
            onFavoriteClick = { restaurant, position, callback ->
                removeFromFavorite(restaurant)
                callback(true) // or false depending on success
            }

        )

        recyclerView.adapter = adapter

        // 🔥 LOAD FAVORITES FROM FIRESTORE
        listenFavorites()
    }

    // 🔥 REAL-TIME FAVORITES
    private fun listenFavorites() {

        if (userId.isEmpty()) return

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .addSnapshotListener { snapshot, _ ->

                favList.clear()

                for (doc in snapshot!!) {

                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val rating = doc.getString("rating") ?: ""
                    val distance = doc.getString("distance") ?: ""

                    val image = doc.getLong("image")?.toInt() ?: getRandomImage()

                    val restaurant = Restaurant(
                        id = id,
                        image = image,
                        name = name,
                        rating = rating,
                        distance = distance,
                        isVegan = false,
                        isOpen = true
                    )

                    restaurant.isFavorite = true

                    favList.add(restaurant)
                }

                adapter.notifyDataSetChanged()
            }
    }

    // ❌ REMOVE FAVORITE
    private fun removeFromFavorite(restaurant: Restaurant) {

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .document(restaurant.id)
            .delete()
    }

    // 🎲 RANDOM IMAGE (fallback)
    private fun getRandomImage(): Int {
        val list = listOf(
            R.drawable.rest1,
            R.drawable.rest2,
            R.drawable.rest3,
            R.drawable.rest4
        )
        return list.random()
    }
}

