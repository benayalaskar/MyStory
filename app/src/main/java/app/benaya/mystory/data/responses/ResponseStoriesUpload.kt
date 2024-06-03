package app.benaya.mystory.data.responses

import com.google.gson.annotations.SerializedName

data class ResponseStoriesUpload(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)