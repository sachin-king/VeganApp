package com.king.veganapp.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.king.veganapp.R
import com.king.veganapp.model.Restaurant

class RestaurantAdapter(private val list: List<Restaurant>) :
    RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val restImage: ImageView = itemView.findViewById(R.id.restImage)
        val restName: TextView = itemView.findViewById(R.id.restName)
        val restRating: TextView = itemView.findViewById(R.id.restRating)
        val restDistance: TextView = itemView.findViewById(R.id.restDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val restaurant = list[position]

        holder.restImage.setImageResource(restaurant.image)
        holder.restName.text = restaurant.name
        holder.restRating.text = restaurant.rating
        holder.restDistance.text = restaurant.distance
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

