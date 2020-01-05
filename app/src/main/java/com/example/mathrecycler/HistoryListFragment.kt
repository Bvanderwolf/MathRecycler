package com.example.mathrecycler


import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_history_list.*

/**
 * A simple [Fragment] subclass.
 */
class HistoryListFragment : Fragment() {

    private val backlog = arrayListOf<PlayedGame>()
    private val backlogAdapter = BacklogAdapter(backlog) { playedGame ->  onHistoryItemClicked(playedGame)}

    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initViewModel()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvBacklog.layoutManager =
                LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)

        rvBacklog.adapter = backlogAdapter
    }

    private fun initViewModel(){
        viewModel = ViewModelProviders.of(activity as AppCompatActivity).get(HomeActivityViewModel::class.java)

        //observe backlog and re initialize on change
        viewModel.backlog.observe(this, Observer { backlog ->
            this@HistoryListFragment.backlog.clear()
            this@HistoryListFragment.backlog.addAll(backlog)
            backlogAdapter.notifyDataSetChanged()
        })
    }

    //when a history item is clicked, we navigate to a played game fragment with using its values
    private fun onHistoryItemClicked(playedGame: PlayedGame){
        viewModel.playButtonSound(context)
        val action = HistoryListFragmentDirections.actionHistoryListFragmentToPlayedGameFragment(playedGame)
        findNavController().navigate(action)
    }
}
