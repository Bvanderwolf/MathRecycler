package com.example.mathrecycler

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlayedGame::class, PlayerProfile::class], version = 1, exportSchema = false)
abstract class AppRoomDatabase : RoomDatabase(){

    abstract fun backlogDao(): BacklogDao

    abstract fun profilesDao(): ProfilesDao

    companion object {
        private const val DATABASE_NAME = "REMINDER_DATABASE"

        @Volatile
        private var appRoomDatabaseInstance: AppRoomDatabase? = null

        fun getDatabase(context: Context): AppRoomDatabase? {
            if (appRoomDatabaseInstance == null) {
                synchronized(AppRoomDatabase::class.java) {
                    if (appRoomDatabaseInstance == null) {
                        appRoomDatabaseInstance = Room.databaseBuilder(
                            context.applicationContext,
                            AppRoomDatabase::class.java, DATABASE_NAME
                        )
                            .build()
                    }
                }
            }
            return appRoomDatabaseInstance
        }
    }
}