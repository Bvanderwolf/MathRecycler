package com.example.mathrecycler

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProfilesDao {

    @Query("SELECT * FROM profileTable")
    fun getProfiles(): LiveData<List<PlayerProfile>>

    @Insert
    suspend fun insertProfile(playerProfile: PlayerProfile)

    @Delete
    suspend fun deleteProfile(playerProfile: PlayerProfile)

    @Update
    suspend fun updateProfile(playerProfile: PlayerProfile)
}