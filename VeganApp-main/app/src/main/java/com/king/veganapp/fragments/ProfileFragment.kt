package com.king.veganapp.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.king.veganapp.R
import com.king.veganapp.ui.LoginActivity
import java.util.Locale

//class ProfileFragment : Fragment(R.layout.fragment_profile) {
//
//    private lateinit var profileImage: ImageView
//    private lateinit var profileName: TextView
//    private lateinit var profileEmail: TextView
//    private lateinit var profileLocation: TextView
//
//    private lateinit var editBtn: Button
//    private lateinit var logoutBtn: Button
//
//    private lateinit var auth: FirebaseAuth
//    private val db = FirebaseFirestore.getInstance()
//    private val storage = FirebaseStorage.getInstance()
//
//    private val userId get() = auth.currentUser?.uid ?: ""
//
//    private var imageUri: Uri? = null
//
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//
//
//    //  Image picker
//    private val pickImage =
//        registerForActivityResult(ActivityResultContracts.GetContent()) {
//            imageUri = it
//            profileImage.setImageURI(it)
//        }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        //  INIT
//        auth = FirebaseAuth.getInstance()
//
//        profileImage = view.findViewById(R.id.profileImage)
//        profileName = view.findViewById(R.id.profileName)
//        profileEmail = view.findViewById(R.id.profileEmail)
//        profileLocation = view.findViewById(R.id.profileLocation)
//
//        editBtn = view.findViewById(R.id.editBtn)
//        logoutBtn = view.findViewById(R.id.logoutBtn)
//
//        // LOAD PROFILE
//        if (userId.isNotEmpty()) {
//            loadProfile()
//        }
//
//        // 🖼 IMAGE PICK
//        profileImage.setOnClickListener {
//            pickImage.launch("image/*")
//        }
//
//        // ✏ EDIT PROFILE
//        editBtn.setOnClickListener {
//            showEditDialog()
//        }
//
//        //  LOGOUT
//        logoutBtn.setOnClickListener {
//
//            auth.signOut()
//
//            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
//
//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            startActivity(intent)
//            requireActivity().finish()
//        }
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
//        getLocation()
//
//    }
//
//
//    private fun getLocation() {
//
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                101
//            )
//            return
//        }
//
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//
//            if (location != null) {
//
//                val geocoder = Geocoder(requireContext(), Locale.getDefault())
//                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
//
//                if (!addresses.isNullOrEmpty()) {
//
//                    val city = addresses[0].locality
//                    val fullAddress = addresses[0].getAddressLine(0)
//
//                    //  UI update
//                    profileLocation.text = city ?: fullAddress
//
//                    //  Firestore save
//                    saveLocationToFirestore(city ?: fullAddress)
//                }
//            }
//        }
//    }
//    private fun saveLocationToFirestore(location: String?) {
//
//        if (location == null) return
//
//        val map = hashMapOf<String, Any>(
//            "location" to location
//        )
//
//        db.collection("users")
//            .document(userId)
//            .set(map, SetOptions.merge())
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == 101 && grantResults.isNotEmpty() &&
//            grantResults[0] == PackageManager.PERMISSION_GRANTED
//        ) {
//            getLocation()
//        }
//    }
//
//
//
//    //  LOAD DATA FROM FIRESTORE
//    private fun loadProfile() {
//
//        db.collection("users").document(userId)
//            .get()
//            .addOnSuccessListener { doc ->
//
//                if (doc.exists()) {
//
//                    profileName.text = doc.getString("name") ?: ""
//                    profileEmail.text = doc.getString("email") ?: ""
//                    //profileLocation.text = doc.getString("location") ?: ""
//                    /*val location = doc.getString("location")
//                    profileLocation.text = if (location.isNullOrEmpty()) "No location" else location*/
//
//
//                    val imageUrl = doc.getString("profileImage")
//
//                    if (!imageUrl.isNullOrEmpty()) {
//                        Glide.with(this)
//                            .load(imageUrl)
//                            .into(profileImage)
//                    }
//                }
//            }
//            .addOnFailureListener {
//                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    // ✏ EDIT DIALOG
//    private fun showEditDialog() {
//
//        val dialogView = LayoutInflater.from(requireContext())
//            .inflate(R.layout.dialog_edit_profile, null)
//
//        val nameInput = dialogView.findViewById<EditText>(R.id.editName)
//        val emailInput = dialogView.findViewById<EditText>(R.id.editEmail)
//        val locationInput = dialogView.findViewById<EditText>(R.id.editLocation)
//
//        //  SET EXISTING DATA
//        nameInput.setText(profileName.text)
//        emailInput.setText(profileEmail.text)
//        locationInput.setText(profileLocation.text)
//
//        AlertDialog.Builder(requireContext())
//            .setTitle("Edit Profile")
//            .setView(dialogView)
//            .setPositiveButton("Save") { _, _ ->
//
//                val name = nameInput.text.toString()
//                val email = emailInput.text.toString()
//                val location = locationInput.text.toString()
//
//                if (imageUri != null) {
//                    uploadImageAndSave(name, email, location)
//                } else {
//                    saveToFirestore(name, email, location, null)
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//
//    // 🖼UPLOAD IMAGE TO FIREBASE STORAGE
//    private fun uploadImageAndSave(name: String, email: String, location: String) {
//
//        val ref = storage.reference.child("profileImages/$userId.jpg")
//
//        imageUri?.let {
//            ref.putFile(it)
//                .addOnSuccessListener {
//
//                    ref.downloadUrl.addOnSuccessListener { uri ->
//
//                        saveToFirestore(name, email, location, uri.toString())
//                    }
//                }
//                .addOnFailureListener {
//                    Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
//
//    //  SAVE DATA TO FIRESTORE
//    private fun saveToFirestore(
//        name: String,
//        email: String,
//        location: String,
//        imageUrl: String?
//    ) {
//
//        val userMap = hashMapOf<String, Any>(
//            "name" to name,
//            "email" to email,
//            "location" to location
//        )
//
//        //  Only update image if exists
//        if (imageUrl != null) {
//            userMap["profileImage"] = imageUrl
//        }
//
//        db.collection("users")
//            .document(userId)
//            .set(userMap, SetOptions.merge())
//            .addOnSuccessListener {
//                Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_SHORT).show()
//                loadProfile()
//            }
//            .addOnFailureListener {
//                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
//            }
//    }
//}

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileLocation: TextView

    private lateinit var editBtn: Button
    private lateinit var logoutBtn: Button

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val userId get() = auth.currentUser?.uid ?: ""

    private var imageUri: Uri? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Image picker
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            imageUri = it
            profileImage.setImageURI(it)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        profileImage = view.findViewById(R.id.profileImage)
        profileName = view.findViewById(R.id.profileName)
        profileEmail = view.findViewById(R.id.profileEmail)
        profileLocation = view.findViewById(R.id.profileLocation)

        editBtn = view.findViewById(R.id.editBtn)
        logoutBtn = view.findViewById(R.id.logoutBtn)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        // LOAD PROFILE FIRST
        if (userId.isNotEmpty()) {
            loadProfile()
        }

        // IMAGE PICK
        profileImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        // EDIT
        editBtn.setOnClickListener {
            showEditDialog()
        }

        // LOGOUT
        logoutBtn.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()

            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    //  LOAD PROFILE
    private fun loadProfile() {

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->

                if (doc.exists()) {

                    profileName.text = doc.getString("name") ?: ""
                    profileEmail.text = doc.getString("email") ?: ""

                    val location = doc.getString("location")

                    if (location.isNullOrEmpty()) {
                        profileLocation.text = "Fetching location..."
                        getLocation()   //  only call when empty
                    } else {
                        profileLocation.text = location
                    }

                    val imageUrl = doc.getString("profileImage")

                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .circleCrop()
                            .into(profileImage)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
    }

    //  GET LOCATION
    private fun getLocation() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->

            if (location != null) {

                val geocoder = Geocoder(requireContext(), Locale.getDefault())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    ) { addresses ->

                        if (addresses.isNotEmpty()) {
                            val city = addresses[0].locality ?: addresses[0].getAddressLine(0)

                            profileLocation.text = city

                            saveLocationToFirestore(city)
                        }
                    }

                } else {

                    val addresses = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )

                    if (!addresses.isNullOrEmpty()) {

                        val city = addresses[0].locality ?: addresses[0].getAddressLine(0)

                        profileLocation.text = city

                        saveLocationToFirestore(city)
                    }
                }

            } else {
                profileLocation.text = "Enable GPS"
            }
        }
    }

    //  SAVE LOCATION
    private fun saveLocationToFirestore(location: String) {

        val map = hashMapOf<String, Any>(
            "location" to location
        )

        db.collection("users")
            .document(userId)
            .set(map, SetOptions.merge())
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
            getLocation()
        }
    }

    //  EDIT DIALOG
    private fun showEditDialog() {

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_profile, null)

        val nameInput = dialogView.findViewById<EditText>(R.id.editName)
        val emailInput = dialogView.findViewById<EditText>(R.id.editEmail)
        val locationInput = dialogView.findViewById<EditText>(R.id.editLocation)

        nameInput.setText(profileName.text)
        emailInput.setText(profileEmail.text)
        locationInput.setText(profileLocation.text)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->

                val name = nameInput.text.toString()
                val email = emailInput.text.toString()
                val location = locationInput.text.toString()

                if (imageUri != null) {
                    uploadImageAndSave(name, email, location)
                } else {
                    saveToFirestore(name, email, location, null)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    //  UPLOAD IMAGE
    private fun uploadImageAndSave(name: String, email: String, location: String) {

        val ref = storage.reference.child("profileImages/$userId.jpg")

        imageUri?.let {
            ref.putFile(it)
                .addOnSuccessListener {

                    ref.downloadUrl.addOnSuccessListener { uri ->
                        saveToFirestore(name, email, location, uri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    //  SAVE PROFILE
    private fun saveToFirestore(
        name: String,
        email: String,
        location: String,
        imageUrl: String?
    ) {

        val userMap = hashMapOf<String, Any>(
            "name" to name,
            "email" to email,
            "location" to location
        )

        if (imageUrl != null) {
            userMap["profileImage"] = imageUrl
        }

        db.collection("users")
            .document(userId)
            .set(userMap, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_SHORT).show()
                loadProfile()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
            }
    }
}


