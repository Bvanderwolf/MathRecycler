package com.example.mathrecycler

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import java.util.*

class BacklogRepository(context: Context) {

    private var backlogDao: BacklogDao

    init {
        val appRoomDatabase = AppRoomDatabase.getDatabase(context)
        backlogDao = appRoomDatabase!!.backlogDao()
    }

    fun getBestPlayedGame(): PlayedGame{
        return backlogDao.getBestPlayedGame()
    }

    //return last played game which is the last item in the backlog List
    fun getLastPlayedGame(): PlayedGame?{
        val backlogUnwrapped = backlogDao.getBacklogUnWrapped()
        var lastPlayedGame: PlayedGame? = null
        if(backlogUnwrapped.count() != 0){
            lastPlayedGame = backlogUnwrapped[backlogUnwrapped.count() - 1]
        }
        return lastPlayedGame
    }

    fun getBacklog(): LiveData<List<PlayedGame>>{
        return backlogDao.getBacklog()
    }

    suspend fun clearBacklog(){
        backlogDao.clearBacklog()
    }

    suspend fun insertPlayedGame(game: PlayedGame){
        backlogDao.insertPlayedGame(game)
    }

    suspend fun deletePlayedGame(game: PlayedGame){
        backlogDao.deletePlayedGame(game)
    }
}