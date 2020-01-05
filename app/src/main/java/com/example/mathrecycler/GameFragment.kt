package com.example.mathrecycler


import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_game.*
import java.lang.Runnable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 */
class GameFragment : Fragment() {

    private var mathItems = arrayListOf<MathItem>()

    private lateinit var mathItemAdapter: MathItemAdapter
    private lateinit var viewModel: GameActivityViewModel

    private var mItemInsertInterval : Long = 5000
    private val intervalSubtraction : Long = 25
    private val intervalThreshhold: Long = 2000

    private val itemInsertHandler = Handler(Looper.getMainLooper())

    private var popupService: PopupService? = null

    /*runnable is an object of type Runnable that in its run method adds
    a math item to the recycler and than reruns itself*/
    private val runnable: Runnable  = object : Runnable {
        override fun run(){
            addMathItem(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(activity as AppCompatActivity).get(GameActivityViewModel::class.java)
        mathItemAdapter = MathItemAdapter(mathItems, context!!)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvMathRecycler.layoutManager =
            LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        rvMathRecycler.adapter = mathItemAdapter
        createItemTouchHelper().attachToRecyclerView(rvMathRecycler)

        tvStreak.text = getString(R.string.game_streak, "0")
        tvAnswered.text = getString(R.string.game_answered, "0")

        //setup timer reference and start timer
        viewModel.setupTimer(view.findViewById(R.id.cmGameTime))
        viewModel.startTimer()

        //instantiate popupService
        popupService = PopupService(view, activity!!)

        /*start inserting math items into the recycler view by running a Runnable that, on run,
        does another delayed post giving itself as an argument, chaining this function with a delay*/
        itemInsertHandler.post( runnable )
    }

    //make sure we stop the timer and remove callbacks when leaving the activity
    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopTimer()
        itemInsertHandler.removeCallbacks(runnable)
    }

    //when pausing the fragment the timer needs to be stopped and callbacks removed so it doesn't continue when leaving
    override fun onPause() {
        super.onPause()
        viewModel.stopTimer()
        itemInsertHandler.removeCallbacks(runnable)
    }

    /*When adding a math item to the recycler we first check whether the item count doesn't equal or equal the max.
    If not, we can create a math item, otherwise we end the game */
    private fun addMathItem(runnable: Runnable){
        if(mathItemAdapter.itemCount < viewModel.maxItemsInRecycler){

            mathItems.add(viewModel.createMathItem())
            //If item count is greater or equal to items to fade at we make the math Adapter fade the list.
            if(mathItemAdapter.itemCount >= viewModel.itemsToFadeAt){
                mathItemAdapter.shakingList = true
            }
            //we update the position of object to be animated by math item adapter
            mathItemAdapter.updateAnimationPosition()
            mathItemAdapter.notifyDataSetChanged()

            //if the insertInterval is greater than the threshHold it can be decreased
            if(mItemInsertInterval > intervalThreshhold)
                mItemInsertInterval -= intervalSubtraction

            //post the runnable with this function in its run method with delay
            itemInsertHandler.postDelayed(runnable, mItemInsertInterval)
        }
        else{
            endGame()
        }
    }
    /**
     * Create a touch helper to recognize when a user swipes an item from a recycler view.
     * An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
     * and uses callbacks to signal when a user is performing these actions.
     */
    private fun createItemTouchHelper(): ItemTouchHelper{

        val callback = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //evaluate the question at hand
                val isCorrect = viewModel.evaluateQuestion(direction, mathItems[viewHolder.layoutPosition])

                //update view model values accordingly and update screen text
                tvAnswered.text = getString(R.string.game_answered, viewModel.updateAnsweredQuestions().toString())
                if(isCorrect) {
                    viewModel.updateAnswersCorrect()
                    tvStreak.text = getString(R.string.game_streak, viewModel.updateCorrectStreak().toString())
                    viewModel.resetWrongStreak()
                }
                else{
                    viewModel.updateAnswersWrong()
                    tvStreak.text = getString(R.string.game_streak, viewModel.resetCorrectStreak())
                    viewModel.updateWrongStreak()

                }

                //play vfx feedback based on if answer is correct or not
                playFeedbackVFXOneShot(isCorrect)

                //remove the swiped math item and notify the adapter
                mathItems.removeAt(viewHolder.layoutPosition)

                //if the new itemCount is smaller than number of items to fade at we stop fading the list
                if(mathItemAdapter.itemCount < viewModel.itemsToFadeAt){
                    mathItemAdapter.stopFadingList()
                }

                //we remove animations being played by given math item
                mathItemAdapter.removeAnimation(viewHolder.layoutPosition)
                mathItemAdapter.notifyDataSetChanged()

                //if the maximum amount of wrong answers in a row has been reached the game ends
                if(viewModel.maxWrongStreakReached()){
                    endGame()
                }
            }

        }

        return ItemTouchHelper(callback)
    }

    private fun playFeedbackVFXOneShot(isPositiveFeedback: Boolean){
        //get one of the 3 lottie feedback animation views at random
        val lottieAnimator = when(Random.nextInt(3)) {
            0 -> activity!!.lottieAVFeedbackBottom
            1 -> activity!!.lottieAVFeedbackMiddleRight
            2 -> activity!!.lottieAVFeedbackTopLeft
            else -> return
        }

        //get a random LottieVFX object with id and scale based on if answer is correct or not
        val vfx = if(isPositiveFeedback) viewModel.getRandomPositiveLottieVFX() else viewModel.getRandomNegativeLottieVFX()

        //if the given lottie animator is not already animating and the vfx object is not null we start the animation
        if(!lottieAnimator.isAnimating && vfx != null){
            //make animator view visible
            if(lottieAnimator.visibility == View.GONE){
                lottieAnimator.visibility = View.VISIBLE
            }
            //scale it based on vfx object
            lottieAnimator.scaleX = vfx.scale
            lottieAnimator.scaleY = vfx.scale

            //set random rotation to view
            lottieAnimator.rotation = Random.nextLong(-35, 35).toFloat()

            //set and play animation
            lottieAnimator.setAnimation(vfx.id)
            lottieAnimator.playAnimation()

            /*check in the animator update listener if the animation has finished
            by looking at the current frame and comparing it to the max frame.
            Make the animation view invisible when the conditions asserts true*/
            lottieAnimator.addAnimatorUpdateListener {
                if(lottieAnimator.frame == lottieAnimator.maxFrame.toInt()){
                    lottieAnimator.visibility = View.GONE
                }
            }

            //play sound based on if answer was correct or not
            val mediaId = if(isPositiveFeedback) R.raw.correct_tone else R.raw.error_tone
            MediaPlayer.create(context, mediaId).start()
        }
    }

    private fun endGame(){
        //we stop the running timer and handler adding math items
        viewModel.stopTimer()
        itemInsertHandler.removeCallbacks(runnable)

        //we show a the popupService with given played game information
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        popupService!!.showPopupWindow(PlayedGame(
            viewModel.gameOperator,
            viewModel.gameDifficulty,
            viewModel.answersWrong,
            viewModel.answersCorrect,
            viewModel.highestCorrectStreak,
            viewModel.getTimerTime(),
            currentDate))
    }
}
