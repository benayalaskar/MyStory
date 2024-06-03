package app.benaya.mystory.utils

import app.benaya.mystory.data.local.StoryEntity
import app.benaya.mystory.data.responses.ResponseLogin
import app.benaya.mystory.data.responses.ResponseRegister
import app.benaya.mystory.data.responses.ResponseStoriesUpload
import app.benaya.mystory.data.responses.ResponseUserLogin


object DataDummy {

    fun generateDummyNewStory(): List<StoryEntity> {
        val storyList = ArrayList<StoryEntity>()
        for (i in 0..32) {
            val stories = StoryEntity(
                "StoryId $i",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                "User",
                "Caption",
                null,
                null,
            )
            storyList.add(stories)
        }
        return storyList
    }

    fun generateDummyNewStoryFailed(): List<StoryEntity> {
        return emptyList()
    }

    fun generateDummyLoginResponse(): ResponseLogin {
        val loginResult = ResponseUserLogin("Benaya Testing", "user-awsd", "token")
        return ResponseLogin(loginResult, false, "Login successfully")
    }

    fun generateDummyLoginResponseFailed(): ResponseLogin {
        val dummyResponseUserLogin = ResponseUserLogin("", "", "")
        return ResponseLogin(dummyResponseUserLogin, true, "Login Failure")
    }

    fun generateDummyRegisterFailed(): ResponseRegister {
        return ResponseRegister(true, "Email already taken")
    }

    fun generateDummyStoryUploadFailed(): ResponseStoriesUpload {
        return ResponseStoriesUpload(true, "Upload Story Failed")
    }
}