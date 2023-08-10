package com.example.epsandroidlibrary

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class ApiClientError(name: String?, message: String?, code: String?, reason: String?) :
    Exception() {
    companion object {
        fun create(name: String, message: String, code: String, reason: String): ApiClientError {
            return ApiClientError(name, message, code, reason)
        }
    }
}

sealed class CardValidationError(name: String) : Exception() {
    object InvalidCardNumber : CardValidationError("invalidCardNumber")
    object InvalidCvv : CardValidationError("invalidCvv")
    object InvalidExpiryMonth : CardValidationError("invalidExpiryMonth")
    object InvalidExpiryYear : CardValidationError("invalidExpiryYear")
}

open class BaseError(
    val name: String,
    override val message: String,
    override val cause: Throwable? = null
) : Throwable()

open class ApiClient(private val session: OkHttpClient, private val url: String) {
    sealed class Result<out T> {
        data class Success<out T>(val value: T) : Result<T>()
        data class Failure(val error: Throwable) : Result<Nothing>()
    }

    // Function for postWithCardInfo
    suspend fun postWithCardInfo(
        path: String,
        data: Map<String, Any?>?,
        headers: Map<String, String>? = null
    ): ApiResponse {
        val cardInfo = SDKResolver.getCardInfoValue()
            ?: throw NullPointerException("Attempted tokenize with empty cardInfo")

        val cardInfoMap: MutableMap<String, Any?> =
            (if (data != null) data + cardInfo.toMap() else cardInfo.toMap()).toMutableMap()

        return post(path, cardInfoMap, headers)
    }

    private fun isValidCardInfo(cardInfo: TCardInfo?): CardValidationError? {
        if (cardInfo == null) {
            return CardValidationError.InvalidCardNumber
        }

        val isCardNumberValid = cardInfo.cardNumber.toString().isNotEmpty()
        val isCvvValid = cardInfo.cvv.toString().isNotEmpty()
        val isExpiryMonthValid = cardInfo.expiryMonth.toString().isNotEmpty()
        val isExpiryYearValid = cardInfo.expiryYear.toString().isNotEmpty()

        if (!isCardNumberValid) {
            return CardValidationError.InvalidCardNumber
        }

        if (!isCvvValid) {
            return CardValidationError.InvalidCvv
        }

        if (!isExpiryMonthValid) {
            return CardValidationError.InvalidExpiryMonth
        }

        if (!isExpiryYearValid) {
            return CardValidationError.InvalidExpiryYear
        }

        return null
    }

    suspend fun post(
        path: String,
        data: Map<String, Any?>,
        headers: Map<String, String>? = null
    ): ApiResponse {

        val body = Gson().toJson(data)
        // Create the URL with the base URL and append the path
        val appendedURL = url.toHttpUrlOrNull()?.newBuilder()?.addPathSegments(path)?.build()

        val requestHeaders = headers?.mapValues { it.value } ?: emptyMap()
        val connection = withContext(Dispatchers.IO) {
            URL(appendedURL.toString()).openConnection()
        } as HttpURLConnection
        return withContext(Dispatchers.IO) {
            connection.requestMethod = "POST"
            for ((key, value) in requestHeaders) {
                connection.setRequestProperty(key, value)
            }

            connection.doOutput = true
            val outputStream = connection.outputStream
            outputStream.write(body.toByteArray(Charset.defaultCharset()))
            outputStream.close()

            try {
                val responseCode = connection.responseCode
                if (responseCode in 200..299) {
                    val inputStream = connection.inputStream
                    val responseData =
                        inputStream.bufferedReader().use(BufferedReader::readText)

                    // Use TypeToken to specify the map's generic types explicitly
                    val mapType = object : TypeToken<Map<String, Any>>() {}.type
                    val jsonResponse: Map<String, Any> = Gson().fromJson(responseData, mapType)

                    return@withContext ApiResponse(jsonResponse)
                } else {

                    val errorStream = connection.errorStream
                    if (errorStream != null) {
                        val errorData = errorStream.bufferedReader().use(BufferedReader::readText)
                        val errorResponse = Gson().fromJson(errorData, ErrorResponse::class.java)
                        throw ApiClientError(
                            ErrorName.REQUEST_FAILED.rawValue, errorResponse.message,
                            errorResponse.code,
                            errorResponse.reason
                        )
                    } else {
                        throw ApiClientError(
                            ErrorName.REQUEST_FAILED.rawValue,
                            "Invalid or no response data", null, null
                        )
                    }
                }

            } finally {
                connection.disconnect()
            }
        }
    }
}
