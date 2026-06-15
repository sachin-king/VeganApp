package com.king.veganapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.king.veganapp.R
import com.king.veganapp.fragments.FavoriteFragment
import com.king.veganapp.fragments.HomeFragment
import com.king.veganapp.fragments.MapFragment
import com.king.veganapp.fragments.ProfileFragment


class MainActivity : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNavigation)

        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener {

            when(it.itemId){

                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.map -> {
                    loadFragment(MapFragment())
                    true
                }

                R.id.favorites -> {
                    loadFragment(FavoriteFragment())
                    true
                }

                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }

        }

    }

    private fun loadFragment(fragment: Fragment){

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout,fragment)
            .commit()

    }
}

