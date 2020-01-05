package com.example.mathrecycler


import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 */

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeActivityViewModel

    private var popupService: PopupService? = null
    private var lastBestGame: PlayedGame? = null

    private var shownTrivia = false

    private val mainScope = CoroutineScope(Dispatchers.Main)

    private val defaultProfile = PlayerProfile(
        "Player1",
        "Easy",
        "Addition",
        true,
        0, 0, 0, 0, 0,
        0, 0, 0,
        0, 0, 0, 0, 0,
        true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(activity as AppCompatActivity).get(HomeActivityViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        popupService = PopupService(view, activity!!)

        initViews()

        viewModel.profiles.observe(this, Observer {
            //if count is 0 add default profile and set current player to it
            if(it.count() == 0){
                viewModel.insertPlayerProfile(defaultProfile)
                viewModel.currentPlayer = defaultProfile
                Log.v("default profile", "inserting default")
            }
            else{
                //if there are profiles available, find the active one and try to show trivia if it hasn't been shown yet
                viewModel.currentPlayer = it.find { playerProfile -> playerProfile.active }
                if(!shownTrivia){
                    tryShowRandomTriviaNumber()
                }
            }
        })

        //when the best played game card view is clicked, navigate to the played game fragment with best game values
        cvBestPlayed.setOnClickListener {
            viewModel.initBestPlayedGame {
                if(it != null){
                    mainScope.launch {
                        viewModel.playButtonSound(context)
                        val action = HomeFragmentDirections.actionHomeFragmentToPlayedGameFragment2(it)
                        findNavController().navigate(action)
                    }
                }
            }
        }

        //when the last played game card view is clicked, navigate to the played game fragment with last game values
        cvLastPlayed.setOnClickListener{
            viewModel.initLastPlayedGame {
                if(it != null){
                    mainScope.launch {
                        viewModel.playButtonSound(context)
                        val action = HomeFragmentDirections.actionHomeFragmentToPlayedGameFragment2(it)
                        findNavController().navigate(action)
                    }
                }
            }
        }

        //when the play button is clicked, we add the current player his operator and difficulty as extra's
        //to playIntent of type PlayActivity and start the play activity with it
        btnPlay.setOnClickListener {
            viewModel.playButtonSound(context)
            var playIntent = Intent(view.context, PlayActivity::class.java)
            playIntent.putExtra(EXTRA_DIFFICULTY ,viewModel.currentPlayer!!.difficulty)
            playIntent.putExtra(EXTRA_OPERATOR, viewModel.currentPlayer!!.operator)
            startActivityForResult(playIntent, ADD_PLAYABLE_GAME_REQUEST_CODE)
        }
    }

    private fun tryShowRandomTriviaNumber(){
        if(viewModel.currentPlayer != null && viewModel.currentPlayer!!.numberTrivia){

            // Observe the triviaError message.
            viewModel.triviaError.observe(this, Observer {
                Toast.makeText(activity!!.applicationContext, it, Toast.LENGTH_LONG).show()
            })

            viewModel.getRandomNumberTrivia {
                if(it != null && it.found){
                    popupService?.showPopupWindow(it)
                }
            }

            shownTrivia = true
        }
    }

    private fun initViews(){

        //show best played game information
        viewModel.initBestPlayedGame {
            mainScope.launch {
                if(it != null){
                    activity!!.tvBestPlayedTitle.text = getString(R.string.best_played_game_title)
                    activity!!.tvBestDifficulty.text = getString(R.string.played_game_difficulty, it.difficulty)
                    activity!!.tvBestOperator.text = getString(R.string.played_game_operator, it.usedOperator)
                    activity!!.tvBestCorrectAnswers.text = getString(R.string.played_game_correct_answers, it.correctAnswers.toString())
                    activity!!.tvBestWrongAnswers.text = getString(R.string.played_game_wrong_answers, it.wrongAnswers.toString())
                    activity!!.tvBestGameTime.text = getString(R.string.game_popup_gameTime, it.gameTime)
                    activity!!.tvBestPlayedGameDate.text = getString(R.string.played_game_date, it.date)
                    if(lastBestGame != null){
                        if(it.correctAnswers > lastBestGame!!.correctAnswers){
                            val lottieAnimator = activity!!.LottieAVCelebrate
                            val celebrateText = activity!!.tvCelebrate
                            lottieAnimator.visibility = View.VISIBLE
                            lottieAnimator.setAnimation(R.raw.lottie_celebrate)
                            lottieAnimator.playAnimation()
                            lottieAnimator.addAnimatorUpdateListener {
                                if(lottieAnimator.frame == lottieAnimator.maxFrame.toInt()){
                                    lottieAnimator.visibility = View.GONE
                                    celebrateText.text = ""
                                }
                            }
                            celebrateText.text = getString(R.string.best_game_celebrate)
                            MediaPlayer.create(context, R.raw.celebrate).start()
                        }
                    }
                    lastBestGame = it
                }
                else{
                    activity!!.tvBestPlayedTitle.text = getString(R.string.played_game_error);
                }
            }
        }

        //show last played game information
        viewModel.initLastPlayedGame {
            mainScope.launch {
                if(it != null){
                    activity!!.tvLastPlayedTitle.text = getString(R.string.last_played_game_title)
                    activity!!.tvLastDifficulty.text = getString(R.string.played_game_difficulty, it.difficulty)
                    activity!!.tvLastOperator.text = getString(R.string.played_game_operator, it.usedOperator)
                    activity!!.tvLastCorrectAnswers.text = getString(R.string.played_game_correct_answers, it.correctAnswers.toString())
                    activity!!.tvLastWrongAnswers.text = getString(R.string.played_game_wrong_answers, it.wrongAnswers.toString())
                    activity!!.tvLastGameTime.text = getString(R.string.game_popup_gameTime, it.gameTime)
                    activity!!.tvLastPlayedGameDate.text = getString(R.string.played_game_date, it.date)
                }
                else{
                    activity!!.tvLastPlayedTitle.text = getString(R.string.played_game_error);
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                ADD_PLAYABLE_GAME_REQUEST_CODE -> {
                    //if played game gained from result data is not null we add it to the database and update the views
                    val playedGame = data!!.getParcelableExtra<PlayedGame>(EXTRA_PLAYED_GAME)
                    if (playedGame != null){
                        viewModel.insertPlayedGame(playedGame) {
                            initViews()
                        }
                        viewModel.updateCurrentPlayerProfile(playedGame)
                    }
                }
            }
        }
    }
}
