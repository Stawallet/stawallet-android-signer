package io.stawallet.signer.data

import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.math.BigDecimal

@Database(
    entities = [
        User::class,
        Seed::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class StawalletDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val seedDao: SeedDao

}

lateinit var stawalletDatabase: StawalletDatabase

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromDouble(value: Double?): BigDecimal? = if (value == null) null else BigDecimal(value)

    @TypeConverter
    fun toDouble(bigDecimal: BigDecimal?): Double? = bigDecimal?.toDouble()
}

fun StawalletDatabase.wipeDb() {
    GlobalScope.launch(Dispatchers.IO) {
        userDao.deleteAll()
        seedDao.deleteAll()
    }
}