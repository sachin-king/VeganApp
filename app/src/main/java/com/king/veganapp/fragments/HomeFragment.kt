package com.king.veganapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.king.veganapp.R
import com.king.veganapp.adaptor.RecipeAdaptor
import com.king.veganapp.adaptor.RestaurantAdapter
import com.king.veganapp.adaptor.SimpleTextAdapter
import com.king.veganapp.model.Recipe
import com.king.veganapp.model.Restaurant
import com.king.veganapp.ui.RestaurantDetailActivity
import android.widget.PopupMenu

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var restaurantRecycler: RecyclerView
    lateinit var recipeRecycler: RecyclerView
    lateinit var categoryRecycler: RecyclerView

    lateinit var restaurantList: ArrayList<Restaurant>
    lateinit var adapter: RestaurantAdapter

    private val db = FirebaseFirestore.getInstance()
    private val userId = "user1" // 🔥 temporary (later Firebase Auth)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurantRecycler = view.findViewById(R.id.restaurantRecycler)
        recipeRecycler = view.findViewById(R.id.recipeRecycler)
        categoryRecycler = view.findViewById(R.id.categoryRecycler)

        restaurantList = ArrayList()
        adapter = RestaurantAdapter(restaurantList)

        restaurantRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        restaurantRecycler.adapter = adapter

        // 🔥 Load data
        loadRestaurantsFromFirestore()


        // बाकी code same 👇
        val recipeList = ArrayList<Recipe>()
        recipeList.add(Recipe(R.drawable.recipe1, "Vegan Burger"))
        recipeList.add(Recipe(R.drawable.recipe2, "Tofu Curry"))
        recipeList.add(Recipe(R.drawable.recipe3, "Avocado Salad"))
        recipeList.add(Recipe(R.drawable.recipe4, "Vegan Burger"))

        val categories = listOf("Fast Food", "Healthy", "Desserts", "Drinks")

        recipeRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        categoryRecycler.layoutManager =
            GridLayoutManager(requireContext(), 2)

        recipeRecycler.adapter = RecipeAdaptor(recipeList)
        categoryRecycler.adapter = SimpleTextAdapter(categories)
    }

    // 🔥 FIRESTORE FUNCTION
    private fun loadRestaurantsFromFirestore() {

        db.collection("restaurants")
            .get()
            .addOnSuccessListener { result ->

                restaurantList.clear()

                for (document in result) {

                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val rating = document.getString("rating") ?: ""
                    val distance = document.getString("distance") ?: ""
                    val imageName = document.getString("image") ?: ""

                    val isVegan = document.getBoolean("isVegan") ?: false
                    val isOpen = document.getBoolean("isOpen") ?: false

                    // 🔥 convert drawable
                    val imageResId = resources.getIdentifier(
                        imageName,
                        "drawable",
                        requireContext().packageName
                    )

                    restaurantList.add(
                        Restaurant(
                            id = id,
                            image = imageResId,
                            name = name,
                            rating = rating,
                            distance = distance,
                            isVegan = isVegan,
                            isOpen = isOpen
                        )
                    )
                }

                // 🔥 Load favorites after restaurants loaded
                loadFavorites()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
    }

    // ❤️ LOAD FAVORITES FROM FIREBASE
    private fun loadFavorites() {

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .get()
            .addOnSuccessListener { result ->

                val favIds = result.map { it.id }

                for (restaurant in restaurantList) {
                    if (favIds.contains(restaurant.id)) {
                        restaurant.isFavorite = true
                    }
                }

                adapter.notifyDataSetChanged()
            }
    }
}*/

class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var restaurantRecycler: RecyclerView
    lateinit var recipeRecycler: RecyclerView
    lateinit var categoryRecycler: RecyclerView

    lateinit var restaurantList: ArrayList<Restaurant>   // display list
    lateinit var fullList: ArrayList<Restaurant>         // original list
    lateinit var adapter: RestaurantAdapter

    lateinit var btnFilter: Button


    private val db = FirebaseFirestore.getInstance()
    private val userId = "user1"

    // 🔥 FILTER BUTTONS
    lateinit var btnAll: TextView
    lateinit var btnVegan: TextView
    lateinit var btnOpen: TextView
    lateinit var btnClosed: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurantRecycler = view.findViewById(R.id.restaurantRecycler)
        recipeRecycler = view.findViewById(R.id.recipeRecycler)
        categoryRecycler = view.findViewById(R.id.categoryRecycler)

        // 🔥 FILTER BUTTONS INIT
        btnAll = view.findViewById(R.id.btnAll)
        btnVegan = view.findViewById(R.id.btnVegan)
        btnOpen = view.findViewById(R.id.btnOpen)
        btnClosed = view.findViewById(R.id.btnClosed)
        btnFilter = view.findViewById(R.id.btnFilter)


        restaurantList = ArrayList()
        fullList = ArrayList()

        // 🔥 ADAPTER WITH CLICK
        adapter = RestaurantAdapter(
            restaurantList,
            onFavoriteClick = { restaurant, position ->
                toggleFavorite(restaurant, position)
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

        restaurantRecycler.layoutManager =
            LinearLayoutManager(requireContext())

        restaurantRecycler.adapter = adapter

        // 🔥 Load data
        loadRestaurantsFromFirestore()

        btnFilter.setOnClickListener {

            val popup = PopupMenu(requireContext(), btnFilter)

            popup.menu.add("Rating")
            popup.menu.add("Distance")

            popup.setOnMenuItemClickListener {

                when (it.title) {
                    "Rating" -> showRatingFilter()
                    "Distance" -> showDistanceFilter()
                }

                true
            }

            popup.show()
        }


        // 🔥 FILTER CLICK
        btnAll.setOnClickListener { filterData("ALL") }
        btnVegan.setOnClickListener { filterData("VEGAN") }
        btnOpen.setOnClickListener { filterData("OPEN") }
        btnClosed.setOnClickListener { filterData("CLOSED") }

        // बाकी code 👇
        val recipeList = ArrayList<Recipe>()
        recipeList.add(Recipe(R.drawable.recipe1, "Vegan Burger"))
        recipeList.add(Recipe(R.drawable.recipe2, "Tofu Curry"))
        recipeList.add(Recipe(R.drawable.recipe3, "Avocado Salad"))
        recipeList.add(Recipe(R.drawable.recipe4, "Vegan Burger"))

        val categories = listOf("Fast Food", "Healthy", "Desserts", "Drinks")

        recipeRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        categoryRecycler.layoutManager =
            GridLayoutManager(requireContext(), 2)

        recipeRecycler.adapter = RecipeAdaptor(recipeList)
        categoryRecycler.adapter = SimpleTextAdapter(categories)
    }

    // 🔥 FIRESTORE
    private fun loadRestaurantsFromFirestore() {

        db.collection("restaurants")
            .get()
            .addOnSuccessListener { result ->

                restaurantList.clear()
                fullList.clear()

                for (document in result) {

                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val rating = document.getString("rating") ?: ""
                    val distance = document.getString("distance") ?: ""
                    val imageName = document.getString("image") ?: ""

                    val isVegan = document.getBoolean("isVegan") ?: false
                    val isOpen = document.getBoolean("isOpen") ?: false

                    val imageResId = resources.getIdentifier(
                        imageName,
                        "drawable",
                        requireContext().packageName
                    )

                    val restaurant = Restaurant(
                        id = id,
                        image = imageResId,
                        name = name,
                        rating = rating,
                        distance = distance,
                        isVegan = isVegan,
                        isOpen = isOpen
                    )

                    fullList.add(restaurant)
                    restaurantList.add(restaurant)
                }

                loadFavorites()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
    }

    // ❤️ FAVORITE TOGGLE
    private fun toggleFavorite(restaurant: Restaurant, position: Int) {

        val favRef = db.collection("users")
            .document(userId)
            .collection("favorites")
            .document(restaurant.id)

        if (restaurant.isFavorite) {

            favRef.delete().addOnSuccessListener {
                restaurant.isFavorite = false
                adapter.notifyItemChanged(position)
            }

        } else {

            favRef.set(mapOf("added" to true)).addOnSuccessListener {
                restaurant.isFavorite = true
                adapter.notifyItemChanged(position)
            }
        }
    }

    // ❤️ LOAD FAVORITES
    private fun loadFavorites() {

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .get()
            .addOnSuccessListener { result ->

                val favIds = result.map { it.id }

                for (restaurant in restaurantList) {
                    if (favIds.contains(restaurant.id)) {
                        restaurant.isFavorite = true
                    }
                }

                adapter.notifyDataSetChanged()
            }
    }

    // 🔥 FILTER LOGIC
    private fun filterData(type: String) {

        restaurantList.clear()

        when (type) {

            "ALL" -> restaurantList.addAll(fullList)

            "VEGAN" -> {
                for (item in fullList) {
                    if (item.isVegan) restaurantList.add(item)
                }
            }

            "OPEN" -> {
                for (item in fullList) {
                    if (item.isOpen) restaurantList.add(item)
                }
            }

            "CLOSED" -> {
                for (item in fullList) {
                    if (!item.isOpen) restaurantList.add(item)
                }
            }
        }

        adapter.notifyDataSetChanged()
    }

    private fun showRatingFilter() {

        val popup = PopupMenu(requireContext(), btnFilter)

        popup.menu.add("4.6+")
        popup.menu.add("4.0 - 4.5")

        popup.setOnMenuItemClickListener {

            when (it.title) {
                "4.6+" -> filterByRating(4.6, 5.0)
                "4.0 - 4.5" -> filterByRating(4.0, 4.5)
            }

            true
        }

        popup.show()
    }
    private fun showDistanceFilter() {

        val popup = PopupMenu(requireContext(), btnFilter)

        popup.menu.add("Under 2 km")
        popup.menu.add("2 - 5 km")

        popup.setOnMenuItemClickListener {

            when (it.title) {
                "Under 2 km" -> filterByDistance(0.0, 2.0)
                "2 - 5 km" -> filterByDistance(2.0, 5.0)
            }

            true
        }

        popup.show()
    }

    private fun filterByRating(min: Double, max: Double) {

        restaurantList.clear()

        for (item in fullList) {

            val ratingValue =
                item.rating.replace("⭐", "").toDoubleOrNull() ?: 0.0

            if (ratingValue in min..max) {
                restaurantList.add(item)
            }
        }

        adapter.notifyDataSetChanged()
    }

    private fun filterByDistance(min: Double, max: Double) {

        restaurantList.clear()

        for (item in fullList) {

            val dist =
                item.distance.replace("km", "").trim().toDoubleOrNull() ?: 0.0

            if (dist in min..max) {
                restaurantList.add(item)
            }
        }

        adapter.notifyDataSetChanged()
    }




}



