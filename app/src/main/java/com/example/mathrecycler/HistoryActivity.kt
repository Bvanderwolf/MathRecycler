package com.example.mathrecycler

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController

import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.content_history.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(this).get(HomeActivityViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handles history_menu menu items
        // when home action is pressed it finishes or navigates back/up based on fragment showing
        // clears backlog when clear backlog action is pressed
        return when(item.itemId){
            android.R.id.home -> {
                viewModel.playButtonSound(applicationContext)
                val navHost = navHostFragmentHistory.childFragmentManager.fragments
                when(navHost[0]){
                    is PlayedGameFragment -> findNavController(R.id.navHostFragmentHistory).navigateUp()
                    is HistoryListFragment -> finish()
                }
                true
            }
            R.id.action_clear_backlog -> {
                viewModel.playButtonSound(applicationContext)
                viewModel.clearBacklog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
