package app.benaya.mystory.data.responses

import app.benaya.mystory.data.local.StoryEntity
import com.google.gson.annotations.SerializedName

data class ResponseStories(

    @field:SerializedName("listStory")
    val listStory: List<StoryEntity>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)