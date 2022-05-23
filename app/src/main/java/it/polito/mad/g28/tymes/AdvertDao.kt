
package it.polito.mad.g28.tymes

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface AdvertDao {
    @Query("SELECT * FROM advert")
    fun findAll() : LiveData<List<Advert>>

    @Query("SELECT id, title, author, location, datetime, description, price, service, time FROM advert WHERE title= :title")
    fun find(title: String) : LiveData<Advert>

    @Query("UPDATE advert SET title= :title, author= :author, location=:location, datetime=:datetime, description=:description, price=:price, service=:service,time=:time WHERE id=:id")
    fun update(id: Int, title: String, author:String, location:String, datetime: String, description: String, price:String, service:String, time:String)

    @Query("SELECT count() FROM advert")
    fun count() : LiveData<Int>

    @Insert
    fun addAdvert(advert: Advert)

    @Query("DELETE FROM advert WHERE title= :title")
    fun removeAdvertsWithTitle(title:String)

    @Query("DELETE FROM advert")
    fun removeAll()
}