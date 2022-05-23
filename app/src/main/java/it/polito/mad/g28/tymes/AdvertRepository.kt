package it.polito.mad.g28.tymes

import android.app.Application
import androidx.lifecycle.LiveData
import kotlin.concurrent.thread

class AdvertRepository(application: Application) {
    private val advertDao = AdvertDatabase.getDatabase(application).advertDao()

    fun find(title: String){
        advertDao.find(title)
    }

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
        thread {
            advertDao.addAdvert(i)
        }
    }

    fun update(id: Int, title: String, author:String, location:String, datetime: String, description: String, price:String, service:String, time:String){
        thread {
            advertDao.update(id, title, author, location, datetime, description, price, service, time)
        }
    }

    fun sub(title:String){
        advertDao.removeAdvertsWithTitle(title)
    }

    fun clear() {
        advertDao.removeAll()
    }

    fun adverts():LiveData<List<Advert>> = advertDao.findAll()
}