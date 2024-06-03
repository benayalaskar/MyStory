package app.benaya.mystory.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class KeysRemote(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
