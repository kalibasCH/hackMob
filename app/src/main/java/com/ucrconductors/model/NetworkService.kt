package com.ucrconductors.model

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume

const val URI_FOR_REQUEST = "http://158.160.2.151:8000"

class NetworkService {

    private val client = OkHttpClient.Builder().build()
    suspend fun postConductorsSession(login: String, password: String): Conductor? {
        val url = "${URI_FOR_REQUEST}/auth/"
        val json = Gson().toJson(mapOf("login" to login, "password" to password))
        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        return executeRequest(url, SomeClass::class.java, body = requestBody, method = "POST")
        
    }

    suspend fun postTransportInfo(transport: Transport): Boolean {
        val url = "${URI_FOR_REQUEST}/transport/"
        val json = Gson().toJson(mapOf("id" to transport.id, "transport_num" to transport.transport_num))
        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val result: Transport? = executeRequest(url, Transport::class.java, body = requestBody, method = "POST")
        return result != null
        return true
    }

    suspend fun fetchUserByCardNumber(cardNumber: String) {
        val url = "${URI_FOR_REQUEST}/users/$cardNumber"
        executeRequest(url, String::class.java, method = "GET")
    }

    private suspend fun <T> executeRequest(url: String, responseType: Class<T>, headers: Map<String, String> = emptyMap(), method: String = "GET", body: RequestBody? = null): T? = suspendCancellableCoroutine { continuation ->
        val requestBuilder = Request.Builder().url(url).method(method, body)

        headers.forEach { (name, value) ->
            requestBuilder.addHeader(name, value)
        }

        val request = requestBuilder.build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("STATUS onFailure", "IOException", e)
                Log.e("STATUS onFailure", "${e.message}")

                continuation.resume(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val gson = Gson()
                        val responseString = response.body?.string()
                        if (responseString != null) {
                            try {
                                // Пытаемся распарсить ответ как JSON-объект
                                val result = gson.fromJson(responseString, responseType)
                                Log.d("RESPONSE_TYPE", "JSON")
                                Log.d("RESPONSE_JSON", responseString)
                                continuation.resume(result)
                            } catch (e: JsonSyntaxException) {

                                Log.d("RESPONSE_TYPE", "String")
                                Log.d("RESPONSE_STRING", responseString)
                                continuation.resume(null) 
                        } else {
                            Log.w("STATUS onResponse", "No data in response body")
                            continuation.resume(null)
                        }
                    } else {
                        Log.i("STATUS onResponse onFailure", "Error: ${response.code}")
                        continuation.resume(null)
                    }
                }
            }
        })
        continuation.invokeOnCancellation {
            client.dispatcher.executorService.shutdown()
        }
    }
}
