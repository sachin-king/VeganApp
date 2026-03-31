package com.king.veganapp.fragments

import android.content.Intent
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

class MapFragment : Fragment(R.layout.fragment_map) {

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

}
