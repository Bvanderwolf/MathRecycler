package com.example.mathrecycler

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "backlogTable")
data class PlayedGame(

    @ColumnInfo(name = "userOperator")
    var usedOperator: String,

    @ColumnInfo(name = "difficulty")
    var difficulty: String,

    @ColumnInfo(name = "wrongAnswers")
    var wrongAnswers: Int,

    @ColumnInfo(name = "correctAnswers")
    var correctAnswers: Int,

    @ColumnInfo(name = "highestStreak")
    var highestStreak: Int,

    @ColumnInfo(name = "gameTime")
    var gameTime: String,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "playerName")
    var playerName: String = "",

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id : Long? = null
) : Parcelable