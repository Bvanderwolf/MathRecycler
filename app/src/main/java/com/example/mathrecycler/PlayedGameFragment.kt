package com.example.mathrecycler


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_played_game.view.*

/**
 * A simple [Fragment] subclass.
 */
class PlayedGameFragment : Fragment() {

    private val args: PlayedGameFragmentArgs by navArgs()

    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(activity as AppCompatActivity).get(HomeActivityViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_played_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.tvPGName.text = getString(R.string.profile_name, args.playedGame!!.playerName)
        view.tvPGDifficulty.text = getString(R.string.played_game_difficulty, args.playedGame!!.difficulty)
        view.tvPGOperator.text = getString(R.string.played_game_operator, args.playedGame!!.usedOperator)
        view.tvPGGameTime.text = getString(R.string.game_popup_gameTime, args.playedGame!!.gameTime)
        view.tvPGCorrectAnswers.text = getString(R.string.played_game_correct_answers, args.playedGame!!.correctAnswers.toString())
        view.tvPGWrongAnswers.text = getString(R.string.played_game_wrong_answers, args.playedGame!!.wrongAnswers.toString())
        view.tvPGDate.text = getString(R.string.played_game_date, args.playedGame!!.date)

        /*check when pressing the delete button, whether we are in the main activity or the history
        * activity. We base our action for navigation on this*/
        view.btnDelete.setOnClickListener {
            viewModel.deletePlayedGame(args.playedGame!!)
            viewModel.playButtonSound(context)
            val controller = findNavController()
            when(activity){
                is MainActivity -> controller.navigate(R.id.action_playedGameFragment2_to_homeFragment)
                is HistoryActivity -> controller.navigate(R.id.action_playedGameFragment_to_historyListFragment)
            }
        }
    }
}
