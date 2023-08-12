package com.ssidglobal.qrreader.domain.model

import com.google.gson.annotations.SerializedName

data class Response<T>(

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data : T

)
