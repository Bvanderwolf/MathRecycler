package com.example.mathrecycler

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

const val ADD_PLAYABLE_GAME_REQUEST_CODE = 100
const val EXTRA_DIFFICULTY = "EXTRA_DIFFICULTY"
const val EXTRA_OPERATOR = "EXTRA_OPERATOR"
const val BUTTON_CLICK_SOUND_VOLUME = 0.25f

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(HomeActivityViewModel::class.java)

        initNavigation()
    }

    override fun onStart() {
        super.onStart()
        //when starting this activity we want to show the playButton animation
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.playbutton_animation)
        btnPlay.startAnimation(animation)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun initNavigation(){
        // The NavController
        val navController = findNavController(R.id.navHostFragmentMain)

        // Connect the navHostFragment with the ActionBar.
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handles main_menu menu items
        // navigates up when home button is pressed
        // starts history activity when history action is pressed
        // navigates to profile fragment when profile action is pressed
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.playButtonSound(applicationContext)
                findNavController(R.id.navHostFragmentMain).navigateUp()
                true
            }
            R.id.action_history -> {
                viewModel.playButtonSound(applicationContext)
                var historyIntent = Intent(this, HistoryActivity::class.java)
                startActivity(historyIntent)
                true
            }
            R.id.action_profile -> {
                viewModel.playButtonSound(applicationContext)
                findNavController(R.id.navHostFragmentMain).navigate(R.id.action_homeFragment_to_profileFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
