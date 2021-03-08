package it.uniparthenope.studenti.marco.veropalumbo001.mangapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.*
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {

    var myResponse: MutableLiveData<Response<NTCheck>> = MutableLiveData()
    var myResponseSignup: MutableLiveData<Response<SignupForm>> = MutableLiveData()
    var myResponseLogin: MutableLiveData<Response<Login>> = MutableLiveData()
    var myResponseSession: MutableLiveData<Response<CheckLogin>> = MutableLiveData()
    var myResponseVolume: MutableLiveData<Response<List<ReceivedVolumes>>> = MutableLiveData()
    var myResponsePersonal: MutableLiveData<Response<List<ReceivedVolumes>>> = MutableLiveData()
    var myResponseQuartieri: MutableLiveData<Response<List<Quartiere>>> = MutableLiveData()
    var myResponseAllVolume: MutableLiveData<Response<List<ReceivedVolumes>>> = MutableLiveData()
    var myResponseAcquisto: MutableLiveData<Response<List<CartVolume>>> = MutableLiveData()

    fun checkID(checkNT: NTCheck){
        viewModelScope.launch {
            val response = repository.checkID(checkNT)
            myResponse.value = response
        }
    }

    fun signup(signupForm: SignupForm){
        viewModelScope.launch {
            val response = repository.signup(signupForm)
            myResponseSignup.value = response
        }
    }

    fun login(loginData: Login){
        viewModelScope.launch {
            val response = repository.login(loginData)
            myResponseLogin.value = response
        }
    }

    fun checkSession(sessionId: String?, checkLogin: CheckLogin){
        viewModelScope.launch {
            val response = repository.checkSession(sessionId, checkLogin)
            myResponseSession.value = response
        }
    }

    fun myLogout(sessionId: String?, checkLogin: CheckLogin){
        viewModelScope.launch {
            val response = repository.myLogout(sessionId, checkLogin)
            myResponseSession.value = response
        }
    }

    fun checkNewVolumes(sessionId: String?, quartiere: String?){
        viewModelScope.launch {
            val response = repository.checkNewVolumes(sessionId, quartiere)
            myResponseVolume.value = response
        }
    }

    fun checkMyVolumes(sessionId: String?, user: String?, quartiere: String?){
        viewModelScope.launch {
            val response = repository.checkMyVolumes(sessionId, user, quartiere)
            myResponsePersonal.value = response
        }
    }

    fun getQuartieri(){
        viewModelScope.launch {
            val response = repository.getQuartieri()
            myResponseQuartieri.value = response
        }
    }

    fun getAllVolumes(quartiere: String){
        viewModelScope.launch {
            val response = repository.getAllVolumes(quartiere)
            myResponseAllVolume.value = response
        }
    }

    fun acquista(sessionId: String?,quartiere: String?, user: String?, volumi: List<CartVolume>?){
        viewModelScope.launch {
            val response = repository.acquista(sessionId, quartiere, user, volumi)
            myResponseAcquisto.value = response
        }
    }
}