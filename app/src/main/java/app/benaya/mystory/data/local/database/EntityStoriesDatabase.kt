package app.benaya.mystory.data.local.database

import app.benaya.mystory.data.local.RemoteKeysDao
import app.benaya.mystory.data.local.StoryEntity
import app.benaya.mystory.data.local.StoryEntityDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [StoryEntity::class, KeysRemote::class], version = 2, exportSchema = false)
abstract class EntityStoriesDatabase : RoomDatabase() {
    abstract fun storyentityDao(): StoryEntityDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: EntityStoriesDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): EntityStoriesDatabase {
            if (INSTANCE == null) {
                synchronized(EntityStoriesDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        EntityStoriesDatabase::class.java,
                        "storyy.db"
                    ).build()
                }
            }
            return INSTANCE as EntityStoriesDatabase
        }
    }
}