package it.polito.mad.g28.tymes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileVM: ViewModel() {
    val profileInfo = MutableLiveData<HashMap<String,String>>()

    init {
        profileInfo.value = HashMap()
        profileInfo.value?.put("Full Name", "")
        profileInfo.value?.put("Biography", "")
        profileInfo.value?.put("Skills", "")
        profileInfo.value?.put("Location", "")
        profileInfo.value?.put("Email", "")
    }

    fun updateProfile(Fullname: String = "Full Name",
                      Biography: String = "Biography",
                      Skills: String = "Skills",
                      Location: String = "Location",
                      Email: String = "Email") {
        profileInfo.value?.put("Full Name", Fullname)
        profileInfo.value?.put("Biography", Biography)
        profileInfo.value?.put("Skills", Skills)
        profileInfo.value?.put("Location", Location)
        profileInfo.value?.put("Email", Email)
    }
}