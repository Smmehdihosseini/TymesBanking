package it.polito.mad.g28.tymes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class AdVM(application: Application): AndroidViewModel(application) {

    val repository = AdvertRepository(application)
    val adverts: LiveData<List<Advert>> = repository.adverts()
    val adInfo = MutableLiveData<HashMap<String,String>>()

    init {
        adInfo.value = HashMap()
        adInfo.value?.put("Title", "Title")
        adInfo.value?.put("Author", "Author")
        adInfo.value?.put("Location", "Location")
        adInfo.value?.put("Datetime", "Datetime")
        adInfo.value?.put("Description", "Description")
        adInfo.value?.put("Price", "0.0TYC")
        adInfo.value?.put("Service", "Service")
        adInfo.value?.put("Time", "")
    }

    fun updateAd(Title: String = "Title",
                 Author: String = "Author",
                 Location: String = "Location",
                 Datetime: String = "Datetime",
                 Description: String = "Description",
                 Price: String = "0.0TYC",
                 Service: String = "Service",
                 Time: String = "") {
        adInfo.value?.put("Title", Title)
        adInfo.value?.put("Author", Author)
        adInfo.value?.put("Location", Location)
        adInfo.value?.put("Datetime", Datetime)
        adInfo.value?.put("Description", Description)
        adInfo.value?.put("Price", Price)
        adInfo.value?.put("Service", Service)
        adInfo.value?.put("Time", Time)
    }

    fun add(title:String, author:String, location:String, datetime:String,description:String, price:String, service:String, time:String) {
        thread {
            repository.add(title, author, location, datetime, description, price, service, time)
        }
    }

    fun sub(title: String) {
        thread {
            repository.sub(title)
        }
    }

    fun clear() {
        thread {
            repository.clear()
        }
    }
}