package it.polito.mad.g28.tymes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.collections.HashMap

class AdVM: ViewModel() {
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
}