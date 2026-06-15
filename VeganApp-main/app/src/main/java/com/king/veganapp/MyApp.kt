package com.king.veganapp

import android.app.Application
import org.osmdroid.config.Configuration

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        //  OSMDroid config (MOST IMPORTANT)
        Configuration.getInstance().load(
            this,
            getSharedPreferences("osm_pref", MODE_PRIVATE)
        )

        Configuration.getInstance().userAgentValue = packageName
    }
}
