package com.example.mathrecycler

import com.google.gson.annotations.SerializedName

data class TriviaObject(
    @SerializedName("text") var text: String,
    @SerializedName("number") var number: String,
    @SerializedName("found") var found: Boolean,
    @SerializedName("type") var type: String
)