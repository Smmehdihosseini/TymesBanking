package it.polito.mad.g28.tymes

import android.app.Application
import androidx.lifecycle.LiveData

class AdvertRepository(application: Application) {
    private val advertDao = AdvertDatabase.getDatabase(application).advertDao()


    fun add(title:String="", author:String, location:String, datetime:String, description:String,price:String,service:String, time:String){
        val i = Advert(0,"", "", "", "", "","", "", "").also {
            it.title = title
            it.author = author
            it.location = location
            it.datetime = datetime
            it.description = description
            it.datetime = datetime
            it.price = price
            it.service = service
            it.time = time
        }
        advertDao.addAdvert(i)
    }

    fun sub(title:String){
        advertDao.removeAdvertsWithTitle(title)
    }

    fun clear() {
        advertDao.removeAll()
    }

    fun adverts():LiveData<List<Advert>> = advertDao.findAll()
}