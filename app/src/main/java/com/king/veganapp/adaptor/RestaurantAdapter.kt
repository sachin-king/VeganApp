package com.king.veganapp.adaptor

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.king.veganapp.R
import com.king.veganapp.model.Restaurant
import com.king.veganapp.ui.RestaurantDetailActivity
import androidx.core.content.ContextCompat
import android.graphics.Color
import android.widget.Toast


//class RestaurantAdapter(
//    private val list: List<Restaurant>
//) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {
//
//    private val db = FirebaseFirestore.getInstance()
//    private val userId = "user1" // 🔥 later Firebase Auth
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        val restImage: ImageView = itemView.findViewById(R.id.restImage)
//        val restName: TextView = itemView.findViewById(R.id.restName)
//        val restRating: TextView = itemView.findViewById(R.id.restRating)
//        val restDistance: TextView = itemView.findViewById(R.id.restDistance)
//
//        // 🔥 NEW VIEWS
//        val veganBadge: TextView = itemView.findViewById(R.id.veganBadge)
//        val favBtn: ImageView = itemView.findViewById(R.id.favBtn)
//        val statusText: TextView = itemView.findViewById(R.id.statusText)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.restaurant_item, parent, false)
//
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//        val restaurant = list[position]
//
//        // 🔥 BASIC DATA
//        holder.restImage.setImageResource(restaurant.image)
//        holder.restName.text = restaurant.name
//        holder.restRating.text = restaurant.rating
//        holder.restDistance.text = restaurant.distance
//
//        // 🌿 VEGAN BADGE
//        if (restaurant.isVegan) {
//            holder.veganBadge.visibility = View.VISIBLE
//        } else {
//            holder.veganBadge.visibility = View.GONE
//        }
//
//        // 🟢 OPEN / CLOSED
//        if (restaurant.isOpen) {
//            holder.statusText.text = "Open"
//            holder.statusText.setBackgroundColor(Color.parseColor("#4CAF50"))
//        } else {
//            holder.statusText.text = "Closed"
//            holder.statusText.setBackgroundColor(Color.RED)
//        }
//
//        // ❤️ FAVORITE ICON
//        if (restaurant.isFavorite) {
//            holder.favBtn.setImageResource(R.drawable.ic_heart_filled)
//        } else {
//            holder.favBtn.setImageResource(R.drawable.ic_heart1)
//        }
//
//        // ❤️ FAVORITE CLICK (FIREBASE SAVE)
//        val favRef = db.collection("users")
//            .document(userId)
//            .collection("favorites")
//            .document(restaurant.id)
//
//        holder.favBtn.setOnClickListener {
//
//            if (restaurant.isFavorite) {
//                favRef.delete()
//                restaurant.isFavorite = false
//            } else {
//                val data = hashMapOf(
//                    "name" to restaurant.name,
//                    "image" to restaurant.image,
//                    "rating" to restaurant.rating,
//                    "distance" to restaurant.distance
//                )
//
//                favRef.set(data)
//                restaurant.isFavorite = true
//            }
//
//            notifyItemChanged(position)
//        }
//
//        // 🔥 ITEM CLICK → DETAIL SCREEN
//        holder.itemView.setOnClickListener {
//
//            val context = holder.itemView.context
//            val intent = Intent(context, RestaurantDetailActivity::class.java)
//
//            intent.putExtra("name", restaurant.name)
//            intent.putExtra("image", restaurant.image)
//            intent.putExtra("rating", restaurant.rating)
//            intent.putExtra("distance", restaurant.distance)
//            intent.putExtra("isVegan", restaurant.isVegan)
//            intent.putExtra("isOpen", restaurant.isOpen)
//
//            context.startActivity(intent)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return list.size
//    }
//}

class RestaurantAdapter(
    private val list: List<Restaurant>, private val onFavoriteClick: (Restaurant, Int) -> Unit,
    private val onItemClick: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = "user1"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val restImage: ImageView = itemView.findViewById(R.id.restImage)
        val restName: TextView = itemView.findViewById(R.id.restName)
        val restRating: TextView = itemView.findViewById(R.id.restRating)
        val restDistance: TextView = itemView.findViewById(R.id.restDistance)

        val veganBadge: TextView = itemView.findViewById(R.id.veganBadge)
        val favBtn: ImageView = itemView.findViewById(R.id.favBtn)
        val statusText: TextView = itemView.findViewById(R.id.statusText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val restaurant = list[position]

        // 🔥 BASIC DATA
        holder.restImage.setImageResource(restaurant.image)
        holder.restName.text = restaurant.name
        holder.restRating.text = restaurant.rating
        holder.restDistance.text = restaurant.distance

        // 🌿 VEGAN BADGE
        holder.veganBadge.visibility =
            if (restaurant.isVegan) View.VISIBLE else View.GONE

        // 🟢 OPEN / CLOSED (Better way)
        if (restaurant.isOpen) {
            holder.statusText.text = "Open"
            holder.statusText.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.green)
            )
        } else {
            holder.statusText.text = "Closed"
            holder.statusText.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red)
            )
        }

        // ❤️ FAVORITE ICON
        holder.favBtn.setImageResource(
            if (restaurant.isFavorite)
                R.drawable.ic_heart_filled
            else
                R.drawable.ic_heart1
        )

        // ❤️ FIREBASE REF
        val favRef = db.collection("users")
            .document(userId)
            .collection("favorites")
            .document(restaurant.id)

        // ❤️ FAVORITE CLICK (SAFE VERSION 🔥)
        holder.favBtn.setOnClickListener {

            /*holder.favBtn.isEnabled = false // prevent double click

            if (restaurant.isFavorite) {

                favRef.delete()
                    .addOnSuccessListener {
                        restaurant.isFavorite = false
                        notifyItemChanged(position)
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            holder.itemView.context,
                            "Error removing favorite",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnCompleteListener {
                        holder.favBtn.isEnabled = true
                    }

            } else {

                val data = hashMapOf(
                    "name" to restaurant.name,
                    "image" to restaurant.image,
                    "rating" to restaurant.rating,
                    "distance" to restaurant.distance
                )

                favRef.set(data)
                    .addOnSuccessListener {
                        restaurant.isFavorite = true
                        notifyItemChanged(position)


                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            holder.itemView.context,
                            "Error adding favorite",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnCompleteListener {
                        holder.favBtn.isEnabled = true
                    }
            }*/

            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            holder.favBtn.isEnabled = false

            if (restaurant.isFavorite) {
                restaurant.isFavorite = false
                holder.favBtn.setImageResource(R.drawable.ic_heart1)
            } else {
                restaurant.isFavorite = true
                holder.favBtn.setImageResource(R.drawable.ic_heart_filled)
            }

            notifyItemChanged(pos)

            holder.favBtn.isEnabled = true

            // 🔥 move firebase call outside (important)
            onFavoriteClick(restaurant, pos)
        }

        // 🔥 ITEM CLICK → DETAIL
        holder.itemView.setOnClickListener {

            val context = holder.itemView.context
            val intent = Intent(context, RestaurantDetailActivity::class.java)

            intent.putExtra("id", restaurant.id)
            intent.putExtra("name", restaurant.name)
            intent.putExtra("image", restaurant.image)
            intent.putExtra("rating", restaurant.rating)
            intent.putExtra("distance", restaurant.distance)
            intent.putExtra("isVegan", restaurant.isVegan)
            intent.putExtra("isOpen", restaurant.isOpen)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}



