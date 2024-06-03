package app.benaya.mystory.data.responses

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

    @field:SerializedName("loginResult")
    val loginResult: ResponseUserLogin,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)