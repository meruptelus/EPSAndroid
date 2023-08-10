package com.example.epsandroidlibrary

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import okhttp3.*
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class APIResponseErrorApi(name: String, message: String) : BaseError(name, message)

class APIResponseError(name: String, message: String) : BaseError(name, message)

class ApiResponse(private val rawResponse: Map<String, Any>) {
    fun getRawData(): Map<String, Any> {
        return rawResponse
    }


    private fun unsafeGet(path: String): Any? {
        return getNestedValue(rawResponse, path)
    }

    @Throws(APIResponseErrorApi::class)
    fun get(dataPath: String, errorPath: String? = null): Any {
        val data = unsafeGet(dataPath)
        if (data != null) {
            return data
        } else if (errorPath != null) {
            val error = unsafeGet(errorPath) as? String
            if (error != null) {
                throw APIResponseErrorApi(ErrorName.FIELD_NOT_FOUND.rawValue, error)
            }
        }
        throw APIResponseErrorApi(
            ErrorName.FIELD_NOT_FOUND.rawValue,
            "Requested field $dataPath is not present in the response"
        )
    }
}

fun getNestedValue(dictionary: Map<String, Any>, keyString: String): Any? {
    val keys = keyString.split(".")
    var currentData: Any? = dictionary
    for (key in keys) {
        val currentDict = currentData as? Map<String, Any>
        val value = currentDict?.get(key)
        if (value != null) {
            currentData = value
        } else {
            return null
        }
    }
    return currentData
}

open class GraphQLClientError(name: String, message: String) : BaseError(name, message)

class GraphQLClient(private val apiInstance: OkHttpClient, private val url: String) {

    private fun parseError(error: Throwable): Throwable {
        // Implement the error parsing logic if needed
        // ...
        return error
    }


    @Throws(GraphQLClientError::class)
    suspend fun mutation(mutation: String, variables: Map<String, Any>? = null): ApiResponse {

        return withContext(Dispatchers.IO) {
            val request = URL(url).openConnection() as HttpURLConnection
            request.requestMethod = "POST"
            request.setRequestProperty("Content-Type", "application/json")

            val requestBody: Map<String, Any> =
                mapOf("query" to mutation, "variables" to (variables ?: emptyMap()))
            val requestBodyJson = Gson().toJson(requestBody)
            request.doOutput = true
            request.outputStream.write(requestBodyJson.toByteArray())

            try {
                val responseCode = request.responseCode
                if (responseCode in 200..299) {
                    val inputStream = request.inputStream
                    val responseData = inputStream.bufferedReader().use(BufferedReader::readText)

                    // Use TypeToken to specify the map's generic types explicitly
                    val mapType = object : TypeToken<Map<String, Any>>() {}.type
                    val jsonResponse: Map<String, Any> = Gson().fromJson(responseData, mapType)
                    return@withContext ApiResponse(jsonResponse)
                } else {
                    throw GraphQLClientError(
                        ErrorName.REQUEST_FAILED.rawValue, "Invalid or no response data"
                    )
                }
            } catch (e: Exception) {
                throw ApiClientError(
                    ErrorName.UNKNOWN_ERROR.rawValue, "Invalid or no response data", null, null
                )
            } finally {
                request.disconnect()
            }
        }
    }

    suspend fun query(query: String, variables: Map<String, Any>? = null): ApiResponse {
        val request = withContext(Dispatchers.IO) {
            URL(url).openConnection()
        } as HttpURLConnection
        request.requestMethod = "POST"
        request.setRequestProperty("Content-Type", "application/json")

        val requestBody: Map<String, Any> =
            mapOf("query" to query, "variables" to (variables ?: emptyMap()))
        val requestBodyJson = Gson().toJson(requestBody)
        request.doOutput = true
        withContext(Dispatchers.IO) {
            request.outputStream.write(requestBodyJson.toByteArray())
        }

        try {
            val responseCode = request.responseCode
            if (responseCode in 200..299) {
                val inputStream = request.inputStream
                val responseData = inputStream.bufferedReader().use(BufferedReader::readText)

                // Use TypeToken to specify the map's generic types explicitly
                val mapType = object : TypeToken<Map<String, Any>>() {}.type
                val jsonResponse: Map<String, Any> = Gson().fromJson(responseData, mapType)

                return ApiResponse(jsonResponse)
            } else {

                throw GraphQLClientError(
                    ErrorName.REQUEST_FAILED.rawValue, "Invalid or no response data"
                )
            }
        } catch (e: Exception) {
            throw ApiClientError(
                ErrorName.UNKNOWN_ERROR.rawValue, "Invalid or no response data", null, null
            )
        } finally {
            request.disconnect()
        }

    }
}
