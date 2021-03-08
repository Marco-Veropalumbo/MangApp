package it.uniparthenope.studenti.marco.veropalumbo001.mangapp.api

import okhttp3.Interceptor
import okhttp3.Response

class MyInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
                .newBuilder()
                .build()
        return chain.proceed(request)
    }
}