package it.polito.mad.g28.tymes

import android.app.Application
import androidx.lifecycle.LiveData

class AdvertRepository(application: Application) {
    private val advertDao = AdvertDatabase.getDatabase(application).advertDao()

    fun add(title:String="", description:String, datetime:String, location:String, duration:String, regularity:String){
        val i = Advert(0,"", "", "", "", "","").also {
            it.title = title
            it.description = description
            it.datetime = datetime
            it.duration = duration
            it.location = location
            it.regularity = regularity
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