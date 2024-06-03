package app.benaya.mystory.data.responses

import app.benaya.mystory.data.local.StoryEntity
import com.google.gson.annotations.SerializedName

data class ResponsePagingStories(
    @field:SerializedName("error")
    var error: String,

    @field:SerializedName("message")
    var message: String,

    @field:SerializedName("listStory")
    var listStory: List<StoryEntity>
)