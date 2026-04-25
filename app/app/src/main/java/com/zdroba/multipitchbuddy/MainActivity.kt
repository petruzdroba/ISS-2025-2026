package com.zdroba.multipitchbuddy

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zdroba.multipitchbuddy.ui.GradientGenerator
import com.zdroba.multipitchbuddy.ui.HomeFragment
import com.zdroba.multipitchbuddy.ui.LogFragment
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        window.decorView.background = appBackground?.toDrawable(getResources())

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_log -> LogFragment()
                else -> HomeFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }
    }

    companion object {
        val appBackground: Bitmap? = GradientGenerator.generate(800)
    }
}