package it.uniparthenope.studenti.marco.veropalumbo001.mangapp.repository

import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.api.RetrofitInstance
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.*
import retrofit2.Response

class Repository {

    suspend fun checkID(checkNT: NTCheck): Response<NTCheck>{
        return RetrofitInstance.api.checkID(checkNT)
    }

    suspend fun signup(signupForm: SignupForm): Response<SignupForm>{
        return RetrofitInstance.api.signup(signupForm)
    }

    suspend fun login(loginData: Login): Response<Login>{
        return RetrofitInstance.api.login(loginData)
    }

    suspend fun checkSession(sessionId: String?, checkLogin: CheckLogin): Response<CheckLogin>{
        return RetrofitInstance.api.checkSession(sessionId, checkLogin)
    }

    suspend fun myLogout(sessionId: String?, checkLogin: CheckLogin): Response<CheckLogin>{
        return RetrofitInstance.api.myLogout(sessionId, checkLogin)
    }

    suspend fun checkNewVolumes(sessionId: String?, quartiere: String?): Response<List<ReceivedVolumes>>{
        return RetrofitInstance.api.checkNewVolumes(sessionId, quartiere)
    }

    suspend fun checkMyVolumes(sessionId: String?, user: String?, quartiere: String?): Response<List<ReceivedVolumes>>{
        return RetrofitInstance.api.checkMyVolumes(sessionId, user, quartiere)
    }

    suspend fun getQuartieri(): Response<List<Quartiere>>{
        return  RetrofitInstance.api.getQuartieri()
    }

    suspend fun getAllVolumes(quartiere: String): Response<List<ReceivedVolumes>>{
        return RetrofitInstance.api.getAllVolumes(quartiere)
    }

    suspend fun acquista(sessionId: String?, quartiere: String?, user: String?, volumi: List<CartVolume>?): Response<List<CartVolume>>{
        return RetrofitInstance.api.acquista(sessionId, quartiere, user, volumi)
    }
}