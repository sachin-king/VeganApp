package com.king.veganapp.fragments

import android.Manifest
import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.king.veganapp.R
import com.king.veganapp.adaptor.RecipeAdaptor
import com.king.veganapp.adaptor.RestaurantAdapter
import com.king.veganapp.adaptor.SimpleTextAdapter
import com.king.veganapp.model.Recipe
import com.king.veganapp.model.Restaurant
import com.king.veganapp.ui.RestaurantDetailActivity
import android.widget.PopupMenu
import android.text.TextWatcher
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.collections.filter




class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var restaurantRecycler: RecyclerView
    lateinit var restaurantList: ArrayList<Restaurant>
    lateinit var fullList: ArrayList<Restaurant>
    lateinit var adapter: RestaurantAdapter

    lateinit var searchBar: EditText
    lateinit var btnAll: TextView
    lateinit var btnOpen: TextView
    lateinit var btnClosed: TextView
    lateinit var btnFilter: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var userLat = 0.0
    private var userLng = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurantRecycler = view.findViewById(R.id.restaurantRecycler)
        searchBar = view.findViewById(R.id.searchBar)
        btnAll = view.findViewById(R.id.btnAll)
        btnOpen = view.findViewById(R.id.btnOpen)
        btnClosed = view.findViewById(R.id.btnClosed)
        btnFilter = view.findViewById(R.id.btnFilter)

        restaurantList = ArrayList()
        fullList = ArrayList()

        /*adapter = RestaurantAdapter(
            restaurantList,
            onFavoriteClick = { _, _ -> },
            onItemClick = { restaurant ->
                val intent = Intent(requireContext(), RestaurantDetailActivity::class.java)
                intent.putExtra("name", restaurant.name)
                intent.putExtra("image", restaurant.image)
                intent.putExtra("rating", restaurant.rating)
                intent.putExtra("distance", restaurant.distance)
                intent.putExtra("isOpen", restaurant.isOpen)
                startActivity(intent)
            }
        )*/
        adapter = RestaurantAdapter(
            restaurantList,
            onFavoriteClick = { restaurant, _, callback ->

                val db = FirebaseFirestore.getInstance()
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                val ref = db.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .document(restaurant.id)

                if (restaurant.isFavorite) {

                    val data = hashMapOf(
                        "name" to restaurant.name,
                        "rating" to restaurant.rating,
                        "distance" to restaurant.distance,
                        "image" to restaurant.image
                    )

                    ref.set(data)
                        .addOnSuccessListener { callback(true) }
                        .addOnFailureListener { callback(false) }

                } else {

                    ref.delete()
                        .addOnSuccessListener { callback(true) }
                        .addOnFailureListener { callback(false) }
                }
            },

            onItemClick = { restaurant ->
                // detail screen
            }
        )


        restaurantRecycler.layoutManager = LinearLayoutManager(requireContext())
        restaurantRecycler.adapter = adapter

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        requestLocation()

        //  SEARCH
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        //  FILTER BUTTONS
        btnAll.setOnClickListener { filterData("ALL") }
        btnOpen.setOnClickListener { filterData("OPEN") }
        btnClosed.setOnClickListener { filterData("CLOSED") }

        //  FILTER DROPDOWN
        btnFilter.setOnClickListener { showFilterPopup() }
    }

    //  LOCATION
    private fun requestLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        }
    }

    private fun toggleFavorite(restaurant: Restaurant) {

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("favorites")
            .document(restaurant.id)

        if (restaurant.isFavorite) {
            // ❌ REMOVE
            ref.delete()
        } else {
            // ✅ ADD
            val data = hashMapOf(
                "name" to restaurant.name,
                "image" to restaurant.image,
                "rating" to restaurant.rating,
                "distance" to restaurant.distance
            )

            ref.set(data)
        }
    }


    private fun listenFavoritesRealtime() {

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("favorites")
            .addSnapshotListener { snapshot, _ ->

                val favIds = snapshot?.map { it.id } ?: emptyList()

                for (item in fullList) {
                    item.isFavorite = favIds.contains(item.id)
                }

                adapter.notifyDataSetChanged()
            }
    }


    @SuppressLint("MissingPermission")
    private fun getUserLocation() {

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {

                val location = result.lastLocation ?: return

                userLat = location.latitude
                userLng = location.longitude

                loadNearbyRestaurants(userLat, userLng)

                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )
    }

    //  GEOAPIFY
    private fun loadNearbyRestaurants(lat: Double, lng: Double) {

        val apiKey = "7e28a0187b35491b83f51aeb66422812"

        val url = "https://api.geoapify.com/v2/places?" +
                "categories=catering.restaurant,catering.cafe" +
                "&filter=circle:$lng,$lat,5000" +
                "&limit=20" +
                "&apiKey=$apiKey"

        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val result = reader.readText()

                val features = JSONObject(result).getJSONArray("features")

                requireActivity().runOnUiThread {

                    fullList.clear()

                    for (i in 0 until features.length()) {

                        val obj = features.getJSONObject(i)
                        val properties = obj.getJSONObject("properties")

                        val name = properties.optString("name", "Restaurant")

                        val geometry = obj.getJSONObject("geometry")
                        val coords = geometry.getJSONArray("coordinates")

                        val rLng = coords.getDouble(0)
                        val rLat = coords.getDouble(1)

                        val restaurant = Restaurant(
                            id = "geo_$i",
                            image = getRandomImage(),
                            name = name,
                            rating = generateRating(),
                            distance = calculateDistance(lat, lng, rLat, rLng),
                            isVegan = false,
                            isOpen = getOpenStatus(properties)
                        )

                        fullList.add(restaurant)
                    }

                    updateUI()
                }

            } catch (e: Exception) {
                Log.e("GEO_ERROR", e.toString())
            }
        }.start()
    }

    //  UPDATE UI
    private fun updateUI() {
        restaurantList.clear()
        restaurantList.addAll(fullList)
        adapter.notifyDataSetChanged()
    }

    //  SEARCH
    private fun filterSearch(query: String) {
        restaurantList.clear()

        if (query.isEmpty()) {
            restaurantList.addAll(fullList)
        } else {
            for (item in fullList) {
                if (item.name.contains(query, true)) {
                    restaurantList.add(item)
                }
            }
        }

        adapter.notifyDataSetChanged()
    }

    //  FILTER BUTTONS
    private fun filterData(type: String) {
        restaurantList.clear()

        when (type) {
            "ALL" -> restaurantList.addAll(fullList)
            "OPEN" -> fullList.filter { it.isOpen }.forEach { restaurantList.add(it) }
            "CLOSED" -> fullList.filter { !it.isOpen }.forEach { restaurantList.add(it) }
        }

        adapter.notifyDataSetChanged()
    }

    //  POPUP FILTER
    private fun showFilterPopup() {
        val popup = PopupMenu(requireContext(), btnFilter)

        popup.menu.add("Rating 4.5+")
        popup.menu.add("Under 2 km")

        popup.setOnMenuItemClickListener {
            when (it.title) {
                "Rating 4.5+" -> filterByRating(4.5, 5.0)
                "Under 2 km" -> filterByDistance(0.0, 2.0)
            }
            true
        }

        popup.show()
    }

    private fun filterByRating(min: Double, max: Double) {
        restaurantList.clear()

        for (item in fullList) {
            val rating = item.rating.replace("⭐", "").toDoubleOrNull() ?: 0.0
            if (rating in min..max) restaurantList.add(item)
        }

        adapter.notifyDataSetChanged()
    }

    private fun filterByDistance(min: Double, max: Double) {
        restaurantList.clear()

        for (item in fullList) {
            val dist = item.distance.replace("km", "").trim().toDoubleOrNull() ?: 0.0
            if (dist in min..max) restaurantList.add(item)
        }

        adapter.notifyDataSetChanged()
    }

    //  RANDOM IMAGE
    private fun getRandomImage(): Int {
        val list = listOf(
            R.drawable.rest1,
            R.drawable.rest2,
            R.drawable.rest3,
            R.drawable.rest4
        )
        return list.random()
    }

    //  RATING
    private fun generateRating(): String {
        val r = 3.5 + Math.random() * 1.5
        return "⭐ %.1f".format(r)
    }

    //  DISTANCE
    private fun calculateDistance(
        uLat: Double,
        uLng: Double,
        rLat: Double,
        rLng: Double
    ): String {

        val result = FloatArray(1)

        android.location.Location.distanceBetween(
            uLat, uLng, rLat, rLng, result
        )

        return "%.2f km".format(result[0] / 1000)
    }

    // 🟢 OPEN/CLOSED
    private fun getOpenStatus(properties: JSONObject): Boolean {
        val hours = properties.optString("opening_hours", "")
        return hours.contains("open", true) || hours.contains("24/7")
    }

    //  PERMISSION RESULT
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocation()
        }
    }
}















