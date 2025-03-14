package com.engineerfred.nassa

import com.google.gson.annotations.SerializedName

data class FAQ(
    @SerializedName("Disease") val disease: String,
    @SerializedName("Question Number") val questionNumber: Int,
    @SerializedName("Question") val question: String,
    @SerializedName("Answer") val answer: String
)
