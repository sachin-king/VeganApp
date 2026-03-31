package com.king.veganapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.king.veganapp.FavoriteManager
import com.king.veganapp.R
import com.king.veganapp.adaptor.RestaurantAdapter
import com.king.veganapp.ui.RestaurantDetailActivity

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.favRecycler)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = RestaurantAdapter(
            FavoriteManager.favoriteList,

            onFavoriteClick = { restaurant, position ->
                // ❤️ favorite remove कर
                FavoriteManager.favoriteList.removeAt(position)
                recyclerView.adapter?.notifyItemRemoved(position)
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

    }
}
