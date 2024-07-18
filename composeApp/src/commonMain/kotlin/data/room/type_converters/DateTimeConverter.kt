package data.room.type_converters

import androidx.room.TypeConverter
import data.helpers.format
import data.helpers.parse
import kotlinx.datetime.LocalDateTime

class DateTimeConverter {
    @TypeConverter
    fun fromString(value: String?): LocalDateTime? {
        return value?.parse()
    }

    @TypeConverter
    fun dateTimeToString(date: LocalDateTime?): String? {
        return date?.format()
    }
}