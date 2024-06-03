package app.benaya.mystory.data.responses

import com.google.gson.annotations.SerializedName

data class ResponseUserLogin(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("token")
    val token: String
)

