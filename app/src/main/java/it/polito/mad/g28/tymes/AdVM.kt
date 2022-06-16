package it.polito.mad.g28.tymes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdVM: ViewModel() {
    val adInfo = MutableLiveData<HashMap<String,String>>()

    init {
        adInfo.value = HashMap()
        adInfo.value?.put("Author ID", "")
        adInfo.value?.put("Author", "")
        adInfo.value?.put("Skill", "")
        adInfo.value?.put("Availability", "Available")
        adInfo.value?.put("Description", "")
        adInfo.value?.put("Price", "0.0")
        adInfo.value?.put("Location", "")
        adInfo.value?.put("Date", "")
    }

    fun updateAd(Author: String = "",
                    Skill: String = "",
                    Availability: String = "",
                    Description: String = "",
                    Price: String = "",
                    Location: String = "",
                    Date: String = "") {
        adInfo.value?.put("Author", Author)
        adInfo.value?.put("Skill", Skill)
        adInfo.value?.put("Availability", Availability)
        adInfo.value?.put("Description", Description)
        adInfo.value?.put("Price", Price)
        adInfo.value?.put("Location", Location)
        adInfo.value?.put("Date", Date)
    }

    fun updateAvailability(Availability: String = "Unavailable"){
        adInfo.value?.put("Availability", Availability)
    }


}