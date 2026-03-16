package com.king.veganapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.king.veganapp.R
import com.king.veganapp.adaptor.RecipeAdaptor
import com.king.veganapp.adaptor.RestaurantAdapter
import com.king.veganapp.adaptor.SimpleTextAdapter
import com.king.veganapp.model.Recipe
import com.king.veganapp.model.Restaurant

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var restaurantRecycler: RecyclerView
    lateinit var recipeRecycler: RecyclerView
    lateinit var categoryRecycler: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        restaurantRecycler = view.findViewById(R.id.restaurantRecycler)
        recipeRecycler = view.findViewById(R.id.recipeRecycler)
        categoryRecycler = view.findViewById(R.id.categoryRecycler)

        // Restaurant Data
        val restaurantList = ArrayList<Restaurant>()
        val recipeList = ArrayList<Recipe>()

        restaurantList.add(
            Restaurant(
                R.drawable.rest1,
                "Green Cafe",
                "⭐4.5",
                "2 km"
            )
        )

        restaurantList.add(
            Restaurant(
                R.drawable.rest2,
                "Plant Power",
                "⭐4.2",
                "1.5 km"
            )
        )

        restaurantList.add(
            Restaurant(
                R.drawable.rest3,
                "Healthy Bites",
                "⭐4.7",
                "3 km"
            )
        )

        restaurantList.add(
            Restaurant(
                R.drawable.rest4,
                "Vegan House",
                "⭐4.3",
                "2.2 km"
            )
        )

//        val recipes = listOf(
//            "Vegan Burger",
//            "Tofu Curry",
//            "Avocado Salad"
//        )
        recipeList.add(
            Recipe(
                R.drawable.recipe1,
                "Vegan Burger"
            )
        )
        recipeList.add(
            Recipe(
                R.drawable.recipe2,
                "Tofu Curry"
            )
        )
        recipeList.add(
            Recipe(
                R.drawable.recipe3,
                "Avocado Salad"
            )
        )
        recipeList.add(
            Recipe(
                R.drawable.recipe4,
                "Vegan Burger"
            )
        )


        val categories = listOf(
            "Fast Food",
            "Healthy",
            "Desserts",
            "Drinks"
        )

        // Layout Managers

        restaurantRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recipeRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        categoryRecycler.layoutManager =
            GridLayoutManager(requireContext(), 2)

        // Adapters

        restaurantRecycler.adapter = RestaurantAdapter(restaurantList)
        recipeRecycler.adapter = RecipeAdaptor(recipeList)
        categoryRecycler.adapter = SimpleTextAdapter(categories)

    }
}

