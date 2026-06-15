package com.king.veganapp.fragments


import android.Manifest
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.king.veganapp.R
import com.king.veganapp.ui.RestaurantDetailActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

// Back button handling
import androidx.activity.OnBackPressedCallback

// Intent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.king.veganapp.RestaurantRepository

// Tujha model (important)
import com.king.veganapp.model.Restaurant   // ⚠️ package path check kar

// Favorite manager (jar use kartos tar)
 // ⚠️ path change ho shakta
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID


/*class MapFragment : Fragment(R.layout.fragment_map) {

    private lateinit var mapView: MapView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.mapView)

        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        mapView.setMultiTouchControls(true)

        val mapController = mapView.controller
        mapController.setZoom(15.0)

        // 📍 Default location (example Navi Mumbai)
        val startPoint = GeoPoint(19.0330, 73.0297)
        mapController.setCenter(startPoint)

        // 👉 markers add कर
        addMarkers()
    }
    private fun addMarkers() {

        val db = FirebaseFirestore.getInstance()

        db.collection("restaurants")
            .get()
            .addOnSuccessListener { result ->

                for (doc in result) {

                    val name = doc.getString("name") ?: ""
                    val lat = doc.getDouble("lat") ?: 0.0
                    val lng = doc.getDouble("lng") ?: 0.0

                    val point = GeoPoint(lat, lng)

                    val marker = Marker(mapView)
                    marker.position = point
                    marker.title = name

                    // 👉 click listener
                    marker.setOnMarkerClickListener { m, _ ->

                        val context = requireContext()
                        val intent = Intent(context, RestaurantDetailActivity::class.java)

                        intent.putExtra("name", name)
                        context.startActivity(intent)

                        true
                    }

                    mapView.overlays.add(marker)
                }

                mapView.invalidate()
            }
    }

}*/

/*class MapFragment : Fragment(R.layout.fragment_map) {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔥 MOST IMPORTANT (OSM FIX)
        Configuration.getInstance().userAgentValue = requireContext().packageName

        mapView = view.findViewById(R.id.map)

        // 🔥 TILE SOURCE (MAP SHOW HOIL)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        requestLocation()
    }

    private fun requestLocation() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )

        } else {
            getUserLocation()
        }
    }

    // 🔥 REAL-TIME LOCATION
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getUserLocation() {

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {

                val location = result.lastLocation

                if (location != null) {

                    val lat = location.latitude
                    val lng = location.longitude

                    val userPoint = GeoPoint(lat, lng)

                    val controller = mapView.controller
                    controller.setZoom(15.0)
                    controller.setCenter(userPoint)

                    // 🔥 CLEAR OLD MARKERS (IMPORTANT)
                    mapView.overlays.clear()

                    // 📍 USER MARKER
                    val userMarker = Marker(mapView)
                    userMarker.position = userPoint
                    userMarker.title = "You are here"
                    mapView.overlays.add(userMarker)

                    userMarker.icon = getResizedIcon(R.drawable.ic_user_marker, 70, 70)

                    // 🔥 LOAD RESTAURANTS
                    loadNearbyRestaurants(lat, lng)

                    fusedLocationClient.removeLocationUpdates(this)

                } else {
                    Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    // 🔥 OVERPASS API
    private fun loadNearbyRestaurants(lat: Double, lng: Double) {

        val apiKey = "7e28a0187b35491b83f51aeb66422812"

        val url = "https://api.geoapify.com/v2/places?" +
                "categories=catering.restaurant,catering.cafe" +
                "&filter=circle:$lng,$lat,5000" +
                "&limit=20" +
                "&apiKey=$apiKey"

        Log.d("GEO_URL", url)

        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val result = reader.readText()

                Log.d("GEO_RESPONSE", result)

                val json = JSONObject(result)
                val features = json.getJSONArray("features")

                requireActivity().runOnUiThread {

                    if (features.length() == 0) {
                        Toast.makeText(requireContext(), "No restaurants found ❌", Toast.LENGTH_SHORT).show()
                        return@runOnUiThread
                    }

                    for (i in 0 until features.length()) {

                        val obj = features.getJSONObject(i)
                        val properties = obj.getJSONObject("properties")

                        val name = properties.optString("name", "Restaurant")

                        val geometry = obj.getJSONObject("geometry")
                        val coordinates = geometry.getJSONArray("coordinates")

                        val rLng = coordinates.getDouble(0)
                        val rLat = coordinates.getDouble(1)

                        addMarker(name, rLat, rLng)
                    }

                    // 🔥 REFRESH MAP AFTER LOOP
                    mapView.invalidate()

                    Toast.makeText(
                        requireContext(),
                        "Loaded ${features.length()} restaurants 🔥",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("GEO_ERROR", e.toString())

                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "API Error ❌", Toast.LENGTH_SHORT).show()
                }
            }

        }.start()
    }

    private fun getResizedIcon(drawableId: Int, width: Int, height: Int): BitmapDrawable {

        val drawable = ContextCompat.getDrawable(requireContext(), drawableId)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val scaled = Bitmap.createScaledBitmap(bitmap, width, height, false)

        return BitmapDrawable(resources, scaled)
    }



    private fun addMarker(name: String, lat: Double, lng: Double) {

        val marker = Marker(mapView)
        marker.position = GeoPoint(lat, lng)
        marker.title = name

        marker.icon = getResizedIcon(R.drawable.ic_restaurant_marker, 60, 60)


        marker.setOnMarkerClickListener { m, _ ->

            val intent = Intent(requireContext(), RestaurantDetailActivity::class.java)
            intent.putExtra("name", m.title)

            startActivity(intent)
            true
        }

        mapView.overlays.add(marker)
    }


    // 🔥 PERMISSION HANDLE
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocation()
        }
    }
}*/

class MapFragment : Fragment(R.layout.fragment_map) {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var userLat: Double = 0.0
    private var userLng: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().userAgentValue = requireContext().packageName

        mapView = view.findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
    }

    // 🔥 LIFECYCLE FIX
    override fun onResume() {
        super.onResume()
        mapView.onResume()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocation()
        } else {
            requestLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    private fun requestLocation() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }

    // 🔥 LOCATION
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getUserLocation() {

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {

                val location = result.lastLocation

                if (location != null) {

                    userLat = location.latitude
                    userLng = location.longitude

                    val userPoint = GeoPoint(userLat, userLng)

                    val controller = mapView.controller
                    controller.setZoom(15.0)
                    controller.setCenter(userPoint)

                    // 🔥 CLEAR OLD
                    mapView.overlays.clear()

                    // 📍 USER MARKER
                    val userMarker = Marker(mapView)
                    userMarker.position = userPoint
                    userMarker.title = "You are here"
                    userMarker.icon = getResizedIcon(R.drawable.ic_user_marker, 70, 70)

                    mapView.overlays.add(userMarker)

                    // 🔥 LOAD RESTAURANTS
                    loadNearbyRestaurants(userLat, userLng)

                    fusedLocationClient.removeLocationUpdates(this)

                } else {
                    Toast.makeText(requireContext(), "Fetching location...", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // 🔥 GEOAPIFY API
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
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val result = reader.readText()

                val json = JSONObject(result)
                val features = json.getJSONArray("features")

                requireActivity().runOnUiThread {

                    // 🔥 CLEAR SHARED LIST
                    RestaurantRepository.nearbyRestaurants.clear()

                    if (features.length() == 0) {
                        Toast.makeText(requireContext(), "No restaurants found ❌", Toast.LENGTH_SHORT).show()
                        return@runOnUiThread
                    }

                    for (i in 0 until features.length()) {

                        val obj = features.getJSONObject(i)
                        val properties = obj.getJSONObject("properties")

                        val name = properties.optString("name", "Restaurant")

                        val geometry = obj.getJSONObject("geometry")
                        val coordinates = geometry.getJSONArray("coordinates")

                        val rLng = coordinates.getDouble(0)
                        val rLat = coordinates.getDouble(1)

                        val rating = generateRating()
                        val distance = calculateDistance(userLat, userLng, rLat, rLng)
                        val status = getOpenStatus(properties)

                        // 🔥 RANDOM IMAGE
                        val image = getRandomImage()

                        // 🔥 CREATE OBJECT
                        val restaurant = Restaurant(
                            id = UUID.randomUUID().toString(),
                            image = image,
                            name = name,
                            rating = rating,
                            distance = distance,
                            isVegan = false,
                            isOpen = status.contains("Open")
                        )

                        // 🔥 SAVE FOR HOME
                        RestaurantRepository.nearbyRestaurants.add(restaurant)

                        // 🔥 MAP MARKER
                        addMarker(name, rLat, rLng, rating, distance, status)
                    }

                    mapView.invalidate()
                }

            } catch (e: Exception) {
                Log.e("GEO_ERROR", e.toString())

                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "API Error ❌", Toast.LENGTH_SHORT).show()
                }
            }

        }.start()
    }

    // 📍 ADD MARKER
    private fun addMarker(
        name: String,
        lat: Double,
        lng: Double,
        rating: String,
        distance: String,
        status: String
    ) {

        val marker = Marker(mapView)
        marker.position = GeoPoint(lat, lng)
        marker.title = "$name\n$rating • $distance\n$status"
        marker.icon = getResizedIcon(R.drawable.ic_restaurant_marker, 60, 60)

        marker.setOnMarkerClickListener { _, _ ->

            val intent = Intent(requireContext(), RestaurantDetailActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("rating", rating)
            intent.putExtra("distance", distance)
            intent.putExtra("status", status)
            intent.putExtra("image", getRandomImage())

            startActivity(intent)
            true
        }

        mapView.overlays.add(marker)
    }

    // ⭐ RANDOM RATING
    private fun generateRating(): String {
        val rating = 3.5 + Math.random() * (5.0 - 3.5)
        return "⭐ %.1f".format(rating)
    }

    // 📍 DISTANCE
    private fun calculateDistance(
        userLat: Double,
        userLng: Double,
        restLat: Double,
        restLng: Double
    ): String {

        val results = FloatArray(1)

        android.location.Location.distanceBetween(
            userLat, userLng,
            restLat, restLng,
            results
        )

        val km = results[0] / 1000
        return "%.2f km".format(km)
    }

    // 🟢 STATUS
    private fun getOpenStatus(properties: JSONObject): String {

        val hours = properties.optString("opening_hours", "")

        return if (hours.contains("24/7") || hours.contains("open", true)) {
            "🟢 Open"
        } else {
            "🔴 Closed"
        }
    }

    // 🎨 ICON
    private fun getResizedIcon(drawableId: Int, width: Int, height: Int): BitmapDrawable {

        val drawable = ContextCompat.getDrawable(requireContext(), drawableId)
            ?: return BitmapDrawable()

        val bitmap = (drawable as BitmapDrawable).bitmap
        val scaled = Bitmap.createScaledBitmap(bitmap, width, height, false)

        return BitmapDrawable(resources, scaled)
    }

    // 🖼 RANDOM IMAGE
    private fun getRandomImage(): Int {
        val images = listOf(
            R.drawable.rest1,
            R.drawable.rest2,
            R.drawable.rest3,
            R.drawable.rest4
        )
        return images.random()
    }

    // 🔐 PERMISSION
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}








