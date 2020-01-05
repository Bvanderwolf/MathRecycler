package com.example.mathrecycler

import android.content.Context
import android.media.MediaPlayer
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Chronometer
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.fragment_game.*
import kotlin.random.Random

class GameActivityViewModel : ViewModel() {

    private val repository = MathRepository()

    private var chronoMeter: Chronometer? = null
    private var timerPausedOffset: Long = 0

    private var answeredQuestions = 0
    private var correctStreak = 0

    private var wrongStreak = 0
    private val maxWrongStreak = 4

    private var timerRunning = false

    val maxItemsInRecycler = 10
    val itemsToFadeAt = 7

    var highestCorrectStreak : Int = 0
        private set

    var gameOperator : String = ""
        private set

    var gameDifficulty : String = ""
        private set

    var answersCorrect = 0
        private set

    var answersWrong = 0
        private set

    //returns the incremented value of answered questions
    fun updateAnsweredQuestions() : Number = ++answeredQuestions

    //returns the incremented value of current correctStreak
    fun updateCorrectStreak(): Number = ++correctStreak

    //increments the value of current wrongStreak
    fun updateWrongStreak(){ wrongStreak++ }

    fun updateAnswersCorrect(){
        answersCorrect++
        if(answersCorrect > highestCorrectStreak){
            highestCorrectStreak = answersCorrect
        }
    }

    fun updateAnswersWrong(){ answersWrong++ }

    //resets the current correctStreak to 0 and returns it
    fun resetCorrectStreak() : Number{
        correctStreak = 0
        return correctStreak
    }

    //resets the current wrongStreak to 0
    fun resetWrongStreak(){
        wrongStreak = 0
    }

    fun maxWrongStreakReached(): Boolean = wrongStreak == maxWrongStreak

    //hooks up chronometer to view model to be used for timer functions
    fun setupTimer(cm : Chronometer){
        chronoMeter = cm
    }

    /*starts the timer if the timer isn't running yet. Sets the base/start value to elapsed time
    since boot so it starts at zero, minus the timerPausedOffset so if paused still has accurate time*/
    fun startTimer(){
        if(!timerRunning){
            chronoMeter?.base = SystemClock.elapsedRealtime() - timerPausedOffset
            chronoMeter?.start()
            timerRunning = true
        }
    }

    //stops the timer and sets the timerPausedOffset to elapsed time minus the timer its base
    fun stopTimer(){
        if(timerRunning){
            chronoMeter?.stop()
            timerPausedOffset = SystemClock.elapsedRealtime() - chronoMeter!!.base
            timerRunning = false
        }
    }

    //returns game timer time as string by subtracting start/base time of chronometer from the system's elapsedRealTime
    fun getTimerTime(): String = ((SystemClock.elapsedRealtime() - chronoMeter!!.base) / 1000f).toInt().toString()

    //sets game operator and game difficulty stored in view model
    fun setGameSettings(operator: String, difficulty: String){
        gameOperator = operator
        gameDifficulty = difficulty
    }

    /*creates a new math item by getting values from repository and returns it */
    fun createMathItem() : MathItem {
        val leftSideArg = repository.createRandomNumber(gameDifficulty, gameOperator)
        val rightSideArg = if(gameOperator == "Division"){
            repository.createDivisionNumber(leftSideArg.toFloat())
        }
        else{
            repository.createRandomNumber(gameDifficulty, gameOperator)
        }

        val operator = repository.getOperatorSymbol(gameOperator)

        return MathItem(
            operator,
            leftSideArg,
            rightSideArg,
            repository.createProposedAnswer(leftSideArg.toFloat(), rightSideArg.toFloat(), operator))
    }

    //evaluates given item by looking at left hand argument, right hand argument, proposed answer and swipe direction
    fun evaluateQuestion(swipeDirection: Int, mathItem: MathItem) : Boolean{
        val floatLeftHand = mathItem.leftHandArg.toFloat()
        val floatRightHand = mathItem.rightHandArg.toFloat()
        val floatAnswer = mathItem.proposedAnswer.toFloat()

        val evaluator = {operator: String ->
            when(operator){
                "+" -> floatLeftHand + floatRightHand == floatAnswer
                "-" -> floatLeftHand - floatRightHand == floatAnswer
                "/" -> floatLeftHand / floatRightHand == floatAnswer
                "*" -> floatLeftHand * floatRightHand == floatAnswer
                "%" -> floatLeftHand % floatRightHand == floatAnswer
                else -> false
            }
        }
        return when(swipeDirection){
            ItemTouchHelper.LEFT -> !evaluator(mathItem.operator)
            ItemTouchHelper.RIGHT -> evaluator(mathItem.operator)
            else -> false
        }
    }

    //returns a VFXidWithViewScale object based on random number between 0 and max ammount of positive vfx assets
    fun getRandomPositiveLottieVFX(): VFXIdWithViewScale?{
        return when(Random.nextInt(3)){
            0 -> VFXIdWithViewScale(R.raw.lottie_heart, 0.5f)
            1 -> VFXIdWithViewScale(R.raw.lottie_star, 0.5f)
            2 -> VFXIdWithViewScale(R.raw.lottie_thumbs_up, 0.5f)
            else -> null
        }
    }

    //returns a VFXidWithViewScale object based on random number between 0 and max amount of negative vfx assets
    fun getRandomNegativeLottieVFX(): VFXIdWithViewScale?{
        return when(Random.nextInt(2)){
            0 -> VFXIdWithViewScale(R.raw.lottie_error1)
            1 -> VFXIdWithViewScale(R.raw.lottie_error2)
            else -> null
        }
    }

    //plays button sound in given context
    fun playButtonSound(context: Context?){
        val mediaPlayer = MediaPlayer.create(context,R.raw.button_click_tone)
        mediaPlayer.setVolume(BUTTON_CLICK_SOUND_VOLUME, BUTTON_CLICK_SOUND_VOLUME)
        mediaPlayer.start()
    }

    //class used for storing id relative to the scale of the lottie animation view
    inner class VFXIdWithViewScale(val id: Int, val scale: Float = 1f)
}