package app.benaya.mystory.di

import android.content.Context
import app.benaya.mystory.data.local.database.EntityStoriesDatabase
import app.benaya.mystory.data.local.repository.StoryEntityRepository
import app.benaya.mystory.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryEntityRepository {
        val database = EntityStoriesDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryEntityRepository(database, apiService)
    }
}