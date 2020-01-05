package com.example.mathrecycler


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_difficulty_select.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var viewModel: HomeActivityViewModel

    private var profileShowing: PlayerProfile? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initViewModel()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    private fun initSpinners(){
        /*Initialize the operator and difficulty spinners by using the ArrayAdapter class it's createFromResource
        method. Adding the resources from strings.xml and hookup up the adapter to the spinner ViewItem*/
        val spinnerOperator : Spinner = activity!!.spProfileOperator
        val operatorAdapter = ArrayAdapter.createFromResource(
            activity!!.applicationContext,
            R.array.spinner_operator_array,
            R.layout.spinner_item_profile
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOperator.adapter = adapter
        }

        val spinnerDifficulty : Spinner = activity!!.spProfileDifficulty
        val difficultyAdapter = ArrayAdapter.createFromResource(
            activity!!.applicationContext,
            R.array.spinner_difficulty_array,
            R.layout.spinner_item_profile
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDifficulty.adapter = adapter
        }

        /*if there is a profile showing, we set this profile's operator and difficulty
        preference values on the spinners*/
        if(profileShowing != null){
            val profileOperator = profileShowing!!.operator
            val operatorPosition = operatorAdapter.getPosition(profileOperator)
            if(operatorPosition != activity!!.spProfileOperator.selectedItemPosition){
                activity!!.spProfileOperator.setSelection(operatorPosition, true)
            }

            val profileDifficulty = profileShowing!!.difficulty
            val difficultyPosition = difficultyAdapter.getPosition(profileDifficulty)
            if(difficultyPosition != activity!!.spProfileDifficulty.selectedItemPosition){
                activity!!.spProfileDifficulty.setSelection(difficultyPosition, true)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        /*when pausing the fragment liveCycle, still set the profile it's operator and
        difficulty values and update the player profile*/
        val selectedOperator = spProfileOperator.selectedItem.toString()
        val selectedDifficulty = spProfileDifficulty.selectedItem.toString()

        if(profileShowing!!.operator != selectedOperator){
            profileShowing!!.operator = selectedOperator
        }

        if(profileShowing!!.difficulty != selectedOperator){
            profileShowing!!.difficulty = selectedDifficulty
        }
        viewModel.updatePlayerProfile(profileShowing)
    }

    private fun initViewModel(){
        //initialize viewModel
        viewModel = ViewModelProviders.of(activity as AppCompatActivity).get(HomeActivityViewModel::class.java)

        /*when observing the profiles list, we first check whether there are profiles in the list.
        If there are profiles, we check if a profile is already showing, if it isn't we find the
        active one in the list and show it. If there already is a profile showing, we check whether
        this profile is the active one from the list and update the showing profile if it wasn't*/
        viewModel.profiles.observe(this, Observer {
            Log.v("profiles check", "has ${it.count()} profiles")
            if(it.count() != 0){
                if(profileShowing == null){
                    profileShowing = it.find { profile -> profile.active }
                    this@ProfileFragment.initViews(profileShowing)
                }
                else{
                    val activeProfile = it.find { profile -> profile.active }
                    if(activeProfile != profileShowing){
                        viewModel.currentPlayer =  activeProfile
                        profileShowing = activeProfile
                        this@ProfileFragment.initViews(profileShowing)
                    }
                }
            }
        })
    }

    private fun initViews(playerProfile: PlayerProfile?){

        CoroutineScope(Dispatchers.Main).launch {
            if(playerProfile != null){
                activity!!.tvProfileName.text = getString(R.string.profile_name, playerProfile.playerName)

                initSpinners()

                //set numberTrivia textbox value and onClickListener to update the profile when pressed
                activity!!.cbTrivia.isChecked = playerProfile.numberTrivia
                activity!!.cbTrivia.setOnClickListener {
                    playerProfile.numberTrivia = !playerProfile.numberTrivia
                    viewModel.updatePlayerProfile(playerProfile)
                }

                activity!!.tvStatsAnswered.text = getString(R.string.profile_stats_answered, playerProfile.totalAnswered.toString())
                activity!!.tvStatsGameTime.text = getString(R.string.profile_stats_game_time, playerProfile.totalGameTime.toString())
                activity!!.tvStatsCorrects.text = getString(R.string.profile_stats_corrects, playerProfile.totalCorrect.toString())
                activity!!.tvStatsWrongs.text = getString(R.string.profile_stats_wrongs, playerProfile.totalWrong.toString())
                activity!!.tvStatsHighestStreak.text = getString(R.string.profile_stats_highest_streak, playerProfile.highestStreak.toString())

                activity!!.tvStatsEasy.text = getString(R.string.profile_stats_total_easy, playerProfile.totalEasy.toString())
                activity!!.tvStatsModerate.text = getString(R.string.profile_stats_total_moderate, playerProfile.totalModerate.toString())
                activity!!.tvStatsHard.text = getString(R.string.profile_stats_total_hard, playerProfile.totalHard.toString())

                activity!!.tvStatsAddition.text = getString(R.string.profile_stats_addition, playerProfile.totalAddition.toString())
                activity!!.tvStatsSubtraction.text = getString(R.string.profile_stats_subtraction, playerProfile.totalSubtraction.toString())
                activity!!.tvStatsMultiplication.text = getString(R.string.profile_stats_multiplication, playerProfile.totalMultiplication.toString())
                activity!!.tvStatsDivision.text = getString(R.string.profile_stats_division, playerProfile.totalDivision.toString())
                activity!!.tvStatsModulo.text = getString(R.string.profile_stats_modulo, playerProfile.totalModulo.toString())

                val animation = AnimationUtils.loadAnimation(context ,R.anim.dropdown_popup_animation)
                cvStatsGeneral.startAnimation(animation)
                cvStatsDifficulty.startAnimation(animation)
                cvStatsOperator.startAnimation(animation)
            }
            else{
                Log.v("profile error", "given playerProfile is null @ProfileFragment.initViews")
            }
        }
    }
}
