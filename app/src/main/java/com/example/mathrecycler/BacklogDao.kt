package com.example.mathrecycler

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BacklogDao {

    @Query("SELECT * FROM backlogTable")
    fun getBacklog() : LiveData<List<PlayedGame>>

    @Query("SELECT * FROM backlogTable")
    fun getBacklogUnWrapped(): List<PlayedGame>

    @Query("DELETE FROM backlogTable")
    suspend fun clearBacklog()

    @Query("SELECT * FROM backlogTable ORDER BY correctAnswers DESC LIMIT 1")
    fun getBestPlayedGame(): PlayedGame

    @Insert
    suspend fun insertPlayedGame(game: PlayedGame)

    @Delete
    suspend fun deletePlayedGame(game: PlayedGame)

}