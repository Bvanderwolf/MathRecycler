package com.example.mathrecycler

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "profileTable")
data class PlayerProfile (

    @ColumnInfo(name = "playerName")
    var playerName: String,

    @ColumnInfo(name = "difficulty")
    var difficulty: String,

    @ColumnInfo(name = "operator")
    var operator: String,

    @ColumnInfo(name = "numberTrivia")
    var numberTrivia: Boolean,

    @ColumnInfo(name = "totalAnswered")
    var totalAnswered: Int,

    @ColumnInfo(name = "totalCorrect")
    var totalCorrect: Int,

    @ColumnInfo(name = "totalWrong")
    var totalWrong: Int,

    @ColumnInfo(name = "totalGameTime")
    var totalGameTime: Int,

    @ColumnInfo(name = "highestStreak")
    var highestStreak: Int,

    @ColumnInfo(name = "totalEasy")
    var totalEasy: Int,

    @ColumnInfo(name = "totalModerate")
    var totalModerate: Int,

    @ColumnInfo(name = "totalHard")
    var totalHard: Int,

    @ColumnInfo(name = "totalAddition")
    var totalAddition: Int,

    @ColumnInfo(name = "totalSubtraction")
    var totalSubtraction: Int,

    @ColumnInfo(name = "totalDivision")
    var totalDivision: Int,

    @ColumnInfo(name = "totalMultiplication")
    var totalMultiplication: Int,

    @ColumnInfo(name = "totalModulo")
    var totalModulo: Int,

    @ColumnInfo(name = "active")
    var active: Boolean,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id : Long? = null

): Parcelable