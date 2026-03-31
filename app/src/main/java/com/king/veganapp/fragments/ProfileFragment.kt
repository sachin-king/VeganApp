package com.king.veganapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.king.veganapp.R
import com.king.veganapp.ui.LoginActivity

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

    // 🔥 Image picker
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            imageUri = it
            profileImage.setImageURI(it)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔥 INIT
        auth = FirebaseAuth.getInstance()

        profileImage = view.findViewById(R.id.profileImage)
        profileName = view.findViewById(R.id.profileName)
        profileEmail = view.findViewById(R.id.profileEmail)
        profileLocation = view.findViewById(R.id.profileLocation)

        editBtn = view.findViewById(R.id.editBtn)
        logoutBtn = view.findViewById(R.id.logoutBtn)

        // 🔥 LOAD PROFILE
        if (userId.isNotEmpty()) {
            loadProfile()
        }

        // 🖼️ IMAGE PICK
        profileImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        // ✏️ EDIT PROFILE
        editBtn.setOnClickListener {
            showEditDialog()
        }

        // 🚪 LOGOUT
        logoutBtn.setOnClickListener {

            auth.signOut()

            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    // 🔥 LOAD DATA FROM FIRESTORE
    private fun loadProfile() {

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->

                if (doc.exists()) {

                    profileName.text = doc.getString("name") ?: "User"
                    profileEmail.text = doc.getString("email") ?: ""
                    profileLocation.text = doc.getString("location") ?: ""

                    val imageUrl = doc.getString("profileImage")

                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .into(profileImage)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
    }

    // ✏️ EDIT DIALOG
    private fun showEditDialog() {

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_profile, null)

        val nameInput = dialogView.findViewById<EditText>(R.id.editName)
        val emailInput = dialogView.findViewById<EditText>(R.id.editEmail)
        val locationInput = dialogView.findViewById<EditText>(R.id.editLocation)

        // 🔥 SET EXISTING DATA
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

    // 🖼️ UPLOAD IMAGE TO FIREBASE STORAGE
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

    // 🔥 SAVE DATA TO FIRESTORE
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

        // 🔥 Only update image if exists
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

