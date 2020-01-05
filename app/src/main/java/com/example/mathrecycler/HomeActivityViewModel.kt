package com.example.mathrecycler

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val backlogRepository = BacklogRepository(application.applicationContext)
    private val profilesRepository = ProfilesRepository(application.applicationContext)
    private val numbersApi: NumbersApiService = NumbersApi.createApi()

    val backlog: LiveData<List<PlayedGame>> = backlogRepository.getBacklog()
    val profiles: LiveData<List<PlayerProfile>> = profilesRepository.getProfiles()

    var currentPlayer: PlayerProfile? = null

    val triviaError = MutableLiveData<String>()

    //gets best played game from backlog repository and applies given action on it
    fun initBestPlayedGame(action: (PlayedGame?) -> Unit) {
        ioScope.launch {
            val bestPlayedGame = backlogRepository.getBestPlayedGame()
            action(bestPlayedGame)
        }
    }

    //gets last played game from backlog repository and applies given action on it
    fun initLastPlayedGame(action: (PlayedGame?) -> Unit) {
        ioScope.launch {
            val lastPlayedGame = backlogRepository.getLastPlayedGame()
            action(lastPlayedGame)
            if(lastPlayedGame == null){
                Log.v("room Error", "no last played Game")
            }
        }
    }

    //gets random number trivia from numbers api and applies given onResponse action on it if successful
    fun getRandomNumberTrivia(onResponseAction: (TriviaObject?) -> Unit){
        numbersApi.getRandomNumberTrivia().enqueue(object : Callback<TriviaObject> {
            override fun onResponse(call: Call<TriviaObject>, response: Response<TriviaObject>) {
                if (response.isSuccessful) {
                    val trivia = response.body()
                    onResponseAction(trivia)
                }
                else {
                    triviaError.value = "An triviaError occurred: ${response.errorBody().toString()}"
                }
            }

            override fun onFailure(call: Call<TriviaObject>, t: Throwable) {
                triviaError.value = t.message
            }
        })
    }

    fun clearBacklog(){
        ioScope.launch {
            backlogRepository.clearBacklog()
        }
    }

    //inserts played game into database with currentplayer its name and executes given onInserted function afterward
    fun insertPlayedGame(game: PlayedGame, onInserted: () -> Unit){
        ioScope.launch {
            game.playerName = currentPlayer!!.playerName
            backlogRepository.insertPlayedGame(game)
            onInserted()
        }
    }

    fun deletePlayedGame(game: PlayedGame){
        ioScope.launch {
            backlogRepository.deletePlayedGame(game)
        }
    }

    fun updatePlayerProfile(playerProfile: PlayerProfile?){
        if(playerProfile != null){
            ioScope.launch {
                profilesRepository.updateProfile(playerProfile)
            }
        }
    }

    //updates current Player profile with playedGame values important for profile stats
    fun updateCurrentPlayerProfile(playedGame: PlayedGame){
        ioScope.launch {
            if (currentPlayer != null) {
                when (playedGame.difficulty) {
                    "Easy" -> currentPlayer!!.totalEasy++
                    "Moderate" -> currentPlayer!!.totalModerate++
                    "Hard" -> currentPlayer!!.totalHard++
                }
                when (playedGame.usedOperator) {
                    "Addition" -> currentPlayer!!.totalAddition++
                    "Subtraction" -> currentPlayer!!.totalSubtraction++
                    "Multiplication" -> currentPlayer!!.totalMultiplication++
                    "Division" -> currentPlayer!!.totalDivision++
                    "Modulo" -> currentPlayer!!.totalModulo++
                }
                currentPlayer!!.totalAnswered += (playedGame.correctAnswers + playedGame.wrongAnswers)
                currentPlayer!!.totalCorrect += playedGame.correctAnswers
                currentPlayer!!.totalWrong += playedGame.wrongAnswers
                currentPlayer!!.totalGameTime += playedGame.gameTime.toInt()
                if (currentPlayer!!.highestStreak < playedGame.highestStreak) {
                    currentPlayer!!.highestStreak = playedGame.highestStreak
                }
                profilesRepository.updateProfile(currentPlayer!!)

            }
        }
    }

    fun insertPlayerProfile(playerProfile: PlayerProfile){
        ioScope.launch {
            profilesRepository.insertProfile(playerProfile)
        }
    }

    //plays buttons sound in given context
    fun playButtonSound(context: Context?){
        val mediaPlayer = MediaPlayer.create(context,R.raw.button_click_tone)
        mediaPlayer.setVolume(BUTTON_CLICK_SOUND_VOLUME, BUTTON_CLICK_SOUND_VOLUME)
        mediaPlayer.start()
    }
}