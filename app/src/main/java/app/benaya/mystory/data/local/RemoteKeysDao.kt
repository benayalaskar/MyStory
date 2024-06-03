package app.benaya.mystory.data.local

import app.benaya.mystory.data.local.database.KeysRemote
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<KeysRemote>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): KeysRemote?

    @Query("DELETE FROM remote_keys")
    suspend fun deleteRemoteKeys()
}