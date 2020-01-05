package com.example.mathrecycler

import android.content.Context
import androidx.lifecycle.LiveData

class ProfilesRepository(context: Context) {

    private var profilesDao: ProfilesDao

    init {
        val appRoomDatabase = AppRoomDatabase.getDatabase(context)
        profilesDao = appRoomDatabase!!.profilesDao()
    }

    fun getProfiles(): LiveData<List<PlayerProfile>>{
        return profilesDao.getProfiles()
    }

    suspend fun updateProfile(playerProfile: PlayerProfile){
        profilesDao.updateProfile(playerProfile)
    }

    suspend fun insertProfile(playerProfile: PlayerProfile){
        profilesDao.insertProfile(playerProfile)
    }
}