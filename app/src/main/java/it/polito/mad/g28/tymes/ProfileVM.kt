package it.polito.mad.g28.tymes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileVM: ViewModel() {
    val profileInfo = MutableLiveData<HashMap<String,String>>()

    init {
        profileInfo.value = HashMap()
        profileInfo.value?.put("Full Name", "Full Name")
        profileInfo.value?.put("Nickname", "Nickname")
        profileInfo.value?.put("Username", "Username")
        profileInfo.value?.put("Biography", "Biography")
        profileInfo.value?.put("Skills", "Skills")
        profileInfo.value?.put("Location", "Location")
        profileInfo.value?.put("Email", "Email")
        profileInfo.value?.put("Webpage", "Webpage")
    }

    fun updateProfile(Fullname: String = "Full Name",
                      Nickname: String = "Nickname",
                      Username: String = "Username",
                      Biography: String = "Biography",
                      Skills: String = "Skills",
                      Location: String = "Location",
                      Email: String = "Email",
                      Webpage: String = "Webpage") {
        profileInfo.value?.put("Full Name", Fullname)
        profileInfo.value?.put("Nickname", Nickname)
        profileInfo.value?.put("Username", Username)
        profileInfo.value?.put("Biography", Biography)
        profileInfo.value?.put("Skills", Skills)
        profileInfo.value?.put("Location", Location)
        profileInfo.value?.put("Email", Email)
        profileInfo.value?.put("Webpage", Webpage)
    }
}