package com.example.mathrecycler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

class PopupService(private val view: View, private val activity: FragmentActivity) {

    private val popupAnimation = AnimationUtils.loadAnimation(view.context, R.anim.dropdown_popup_animation)

    fun showPopupWindow(playedGame: PlayedGame){
        //create view with layoutInflater
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.game_popup_layout, null)

        //get display information
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        //set width and height of popup based on layout and display info
        val width = (displayMetrics.widthPixels - view.context.resources.getDimension(R.dimen.game_popup_margin)).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        //show popup window at center of screen
        val popupWindow = PopupWindow(popupView, width, height, false)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        //Set popup window views
        val tvGameTime = popupView.findViewById<TextView>(R.id.tvPopupGameTime)
        tvGameTime.text = view.context.getString(R.string.game_popup_gameTime, playedGame.gameTime)

        val tvCorrectAnswers = popupView.findViewById<TextView>(R.id.tvPopupAnswersCorrect)
        tvCorrectAnswers.text = view.context.getString(R.string.played_game_correct_answers, playedGame.correctAnswers.toString())

        val tvWrongAnswers = popupView.findViewById<TextView>(R.id.tvPopupAnswersWrong)
        tvWrongAnswers.text = view.context.getString(R.string.played_game_wrong_answers, playedGame.wrongAnswers.toString())

        //When the return button is pressed we end the activity with given played game as result
        val returnButton = popupView.findViewById<Button>(R.id.btnReturn)
        returnButton.setOnClickListener {
            popupWindow.dismiss()
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_PLAYED_GAME, playedGame)
            activity.setResult(Activity.RESULT_OK, resultIntent)
            activity.finish()
        }

        popupView.startAnimation(popupAnimation)
    }

    fun showPopupWindow(triviaObject: TriviaObject){

        //create view with layoutInflater
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.trivia_popup_layout, null)

        //get display information
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        //set width and height of popup based on layout and display info
        val width = (displayMetrics.widthPixels - view.context.resources.getDimension(R.dimen.game_popup_margin)).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        //show popup window at center of screen
        val popupWindow = PopupWindow(popupView, width, height, true)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        val tvSubtitle = popupView.findViewById<TextView>(R.id.tvTriviaSubTitle)
        tvSubtitle.text = activity.getString(R.string.trivia_subtitle, triviaObject.number)

        val tvContent = popupView.findViewById<TextView>(R.id.tvTriviaContent)
        tvContent.text = triviaObject.text

        popupView.startAnimation(popupAnimation)
    }
}