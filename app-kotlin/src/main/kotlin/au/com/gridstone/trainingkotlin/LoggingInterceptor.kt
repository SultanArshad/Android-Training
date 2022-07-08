package au.com.gridstone.trainingkotlin

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

object LoggingInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request: Request = chain.request()
    println(request)
    val response: Response = chain.proceed(request)
    println(response)
    return response
  }
}
