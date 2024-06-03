package app.benaya.mystory.data.local

import androidx.room.*
import androidx.paging.PagingSource

@Dao
interface StoryEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(story: List<StoryEntity>)

    @Query("SELECT * FROM StoryEntity")
    fun getAllPagingStory(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM StoryEntity")
    fun getAllStory(): List<StoryEntity>

    @Query("DELETE FROM StoryEntity")
    fun delete()
}