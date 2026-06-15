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




/*class RestaurantAdapter(
    private val list: List<Restaurant>,
    private val onFavoriteClick: (Restaurant, Int) -> Unit,
    private val onItemClick: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

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

        // 🔥 IMAGE (safe fallback)
        if (restaurant.image != 0) {
            holder.restImage.setImageResource(restaurant.image)
        } else {
            holder.restImage.setImageResource(R.drawable.rest1) // fallback
        }

        // 🔥 TEXT DATA
        holder.restName.text = restaurant.name
        holder.restRating.text = restaurant.rating
        holder.restDistance.text = restaurant.distance

        // 🌿 VEGAN BADGE
        holder.veganBadge.visibility =
            if (restaurant.isVegan) View.VISIBLE else View.GONE

        // 🟢 OPEN / CLOSED (improved UI)
        if (restaurant.isOpen) {
            holder.statusText.text = "🟢 Open"
            holder.statusText.setBackgroundResource(R.drawable.bg_open)
        } else {
            holder.statusText.text = "🔴 Closed"
            holder.statusText.setBackgroundResource(R.drawable.bg_closed)
        }

        // ❤️ FAVORITE ICON
        holder.favBtn.setImageResource(
            if (restaurant.isFavorite)
                R.drawable.ic_heart_filled
            else
                R.drawable.ic_heart1
        )

        // ❤️ FAVORITE CLICK (OPTIMIZED 🔥)
        holder.favBtn.setOnClickListener {

            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            // 🔥 UI instant change (fast UX)
            restaurant.isFavorite = !restaurant.isFavorite

            notifyItemChanged(pos)

            // 🔥 FIREBASE handled outside
            onFavoriteClick(restaurant, pos)
        }

        // 🔥 ITEM CLICK → DETAIL (clean way)
        holder.itemView.setOnClickListener {
            onItemClick(restaurant)
        }
    }

    override fun getItemCount(): Int = list.size
}*/

class RestaurantAdapter(
    private val list: List<Restaurant>,
    private val onFavoriteClick: (Restaurant, Int, (Boolean) -> Unit) -> Unit,
    private val onItemClick: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

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

        // IMAGE
        holder.restImage.setImageResource(
            if (restaurant.image != 0) restaurant.image else R.drawable.rest1
        )

        // TEXT
        holder.restName.text = restaurant.name
        holder.restRating.text = restaurant.rating
        holder.restDistance.text = restaurant.distance

        // VEGAN
        holder.veganBadge.visibility =
            if (restaurant.isVegan) View.VISIBLE else View.GONE

        // STATUS
        if (restaurant.isOpen) {
            holder.statusText.text = "🟢 Open"
            holder.statusText.setBackgroundResource(R.drawable.bg_open)
        } else {
            holder.statusText.text = "🔴 Closed"
            holder.statusText.setBackgroundResource(R.drawable.bg_closed)
        }

        // FAVORITE ICON
        holder.favBtn.setImageResource(
            if (restaurant.isFavorite)
                R.drawable.ic_heart_filled
            else
                R.drawable.ic_heart1
        )

        // ❤️ CLICK
        holder.favBtn.setOnClickListener {

            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            val oldState = restaurant.isFavorite

            // 🔥 instant UI
            restaurant.isFavorite = !oldState
            notifyItemChanged(pos)

            // 🔥 FIRESTORE CALL + CALLBACK
            onFavoriteClick(restaurant, pos) { success ->

                if (!success) {
                    // ❌ revert UI if failed
                    restaurant.isFavorite = oldState
                    notifyItemChanged(pos)
                }
            }
        }

        // ITEM CLICK
        holder.itemView.setOnClickListener {
            onItemClick(restaurant)
        }
    }

    override fun getItemCount(): Int = list.size
}



