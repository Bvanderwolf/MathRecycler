package com.example.mathrecycler


import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_difficulty_select.*
import kotlinx.android.synthetic.main.fragment_difficulty_select.view.*

/**
 * A simple [Fragment] subclass.
 */
class DifficultySelectFragment : Fragment() {

    private lateinit var viewModel: GameActivityViewModel

    private lateinit var operatorAdapter: ArrayAdapter<CharSequence>
    private lateinit var difficultyAdapter: ArrayAdapter<CharSequence>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //hookup view model by using the ViewModelProviders class
        viewModel = ViewModelProviders.of(activity as AppCompatActivity).get(GameActivityViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_difficulty_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*Initialize the operator and difficulty spinners by using the ArrayAdapter class it's createFromResource
        method. Adding the resources from strings.xml and hookup up the adapter to the spinner ViewItem*/
        val spinnerOperator : Spinner = view.spOperator
        operatorAdapter = ArrayAdapter.createFromResource(
            view.context,
            R.array.spinner_operator_array,
            R.layout.custom_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOperator.adapter = adapter
        }

        val spinnerDifficulty : Spinner = view.spDifficulty
        difficultyAdapter = ArrayAdapter.createFromResource(
            view.context,
            R.array.spinner_difficulty_array,
            R.layout.custom_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDifficulty.adapter = adapter
        }

        /*On button start click we set to game settings chosen in this fragment in our view model
        so it can be used by our game fragment when playing the game, then we navigate to it*/
        btnStartRecycling.setOnClickListener {
            viewModel.playButtonSound(context)
            viewModel.setGameSettings(spOperator.selectedItem.toString(), spDifficulty.selectedItem.toString())
           findNavController().navigate(R.id.action_difficultySelectFragment_to_gameFragment)
        }

        /*When the usePlayerSettings button is pressed, we get our extra operator and extra difficult
        that we received from the main activity and if they are not the same as the current values,
        override them*/
        btnUsePlayerSettings.setOnClickListener {
            val playerOperator = activity!!.intent.getStringExtra(EXTRA_OPERATOR)
            val operatorPosition = operatorAdapter.getPosition(playerOperator)
            if(operatorPosition != spOperator.selectedItemPosition){
                spOperator.setSelection(operatorPosition, true)
            }

            val playerDifficulty = activity!!.intent.getStringExtra(EXTRA_DIFFICULTY)
            val difficultyPosition = operatorAdapter.getPosition(playerDifficulty)
            if(difficultyPosition != spDifficulty.selectedItemPosition){
                spDifficulty.setSelection(difficultyPosition, true)
            }
        }
    }
}
