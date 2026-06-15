package com.king.veganapp.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.king.veganapp.R
import com.king.veganapp.model.Recipe


class RecipeAdaptor(private val list: ArrayList<Recipe>) :
    RecyclerView.Adapter<RecipeAdaptor.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val restName: TextView = itemView.findViewById(R.id.recipeName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val recipe = list[position]

        holder.recipeImage.setImageResource(recipe.image)
        holder.restName.text = recipe.name

    }

    override fun getItemCount(): Int {
        return list.size
    }
}