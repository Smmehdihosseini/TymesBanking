package it.polito.mad.g28.tymes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Advert::class], version = 1)
abstract class AdvertDatabase: RoomDatabase() {
    abstract fun advertDao(): AdvertDao

    companion object {
        @Volatile
        private var INSTANCE: AdvertDatabase? = null

        fun getDatabase(context: Context): AdvertDatabase =
            (
                    INSTANCE ?: synchronized(this) {
                        val i = INSTANCE ?: Room.databaseBuilder(
                            context.applicationContext,
                            AdvertDatabase::class.java,
                            "adverts"
                        ).build()
                        INSTANCE = i
                        INSTANCE
                    }
                    )!!
    }

}