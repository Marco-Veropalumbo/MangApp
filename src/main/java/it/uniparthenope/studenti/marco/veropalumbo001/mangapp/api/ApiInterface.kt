package it.uniparthenope.studenti.marco.veropalumbo001.mangapp.api

import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {

    @POST("ntcheck")
    suspend fun checkID(
            @Body checkNT: NTCheck
    ): Response<NTCheck>

    @POST("signupform")
    suspend fun signup(
            @Body signupForm: SignupForm
    ): Response<SignupForm>

    @POST("login")
    suspend fun login(
            @Body loginData: Login
    ): Response<Login>

    @POST("home")
    suspend fun checkSession(
            @Header("Cookie") sessionId: String?,
            @Body checkLogin: CheckLogin
    ): Response<CheckLogin>

    @POST("logout")
    suspend fun myLogout(
            @Header("Cookie") sessionId: String?,
            @Body checkLogin: CheckLogin
    ): Response<CheckLogin>

    @POST("newvolumes")
    suspend fun checkNewVolumes(
            @Header("Cookie") sessionId: String?,
            @Header("quartiere") quartiere: String?
    ): Response<List<ReceivedVolumes>>

    @POST("personalvolumes")
    suspend fun checkMyVolumes(
            @Header("Cookie") sessionId: String?,
            @Header("user") user: String?,
            @Header("quartiere") quartiere: String?
    ): Response<List<ReceivedVolumes>>

    @POST("quartieri")
    suspend fun getQuartieri(
    ): Response<List<Quartiere>>

    @POST("allvolumes")
    suspend fun getAllVolumes(
            @Header("quartiere") quartiere: String?
    ): Response<List<ReceivedVolumes>>

    @POST("acquista")
    suspend fun acquista(
        @Header ("Cookie") sessionId: String?,
        @Header ("quartiere") quartiere: String?,
        @Header ("user") user: String?,
        @Body volumi: List<CartVolume>?
    ): Response<List<CartVolume>>
}