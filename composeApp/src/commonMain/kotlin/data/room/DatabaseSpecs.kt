package data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import data.room.entities.LocalActress
import data.room.entities.LocalPrefferedContent
import data.room.entities.LocalTag
import data.room.entities.LocalVideo
import data.room.entities.LocalVideoHistory
import data.room.entities.LocalWebLocal
import data.room.type_converters.DateTimeConverter

@Database(
    entities = [LocalVideo::class, LocalTag::class, LocalActress::class, LocalPrefferedContent::class, LocalWebLocal::class, LocalVideoHistory::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    value = [DateTimeConverter::class]
)
abstract class DatabaseSpecs : RoomDatabase()