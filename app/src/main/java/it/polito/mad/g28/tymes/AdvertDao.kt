package it.polito.mad.g28.tymes

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

class AdvertDao {

    @Dao
    interface AdvertDao {
        @Query("SELECT * FROM advert")
        fun findAll() : LiveData<List<Advert>>

        @Query("SELECT count() FROM advert")
        fun count() : LiveData<Int>

        @Insert
        fun addAdvert(advert: Advert)

        @Query("DELETE FROM advert WHERE title= :title")
        fun removeAdvertsWithTitle(title:String)

        @Query("DELETE FROM advert")
        fun removeAll()
    }
}