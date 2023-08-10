package com.example.epsandroidlibrary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.json.JSONObject

import kotlin.reflect.full.memberProperties


open class ValidationError(message: String) : Exception(message) {
    class MissingRequiredField(fieldName: String) :
        ValidationError("Missing required field: $fieldName")
}

class SDKApi(args: Map<String, Any?>?) : SDKInterface {
    private val retrieveSessionQuery =
        "query(\$type: String!) { account { retrieveSession(type:\$type) { data } } }"

    private var _graphQLClient: GraphQLClient? = null
    private var _apiClient: ApiClient? = null

    private val _getCardInfoArgsKeys: Set<String> = run {
        val getCardInfoArgsKeys: MutableSet<String> = mutableSetOf()
        for (property in TGetCardInfoArgs::class.memberProperties) {
            getCardInfoArgsKeys.add(property.name)
        }
        getCardInfoArgsKeys
    }

    init {
        val resp = initSDK(args) as TApiInitResp
        _graphQLClient = resp.graphQLClient
        _apiClient = resp.apiClient
    }

    override fun initSDK(args: Map<String, Any?>?): Any {
        return try {
            val validatedArgs = TSchemaArgs.fromMap(args)
            val restApi = validatedArgs.restApi
            val graphQLApi = validatedArgs.graphQLApi
            val okHttpClient = OkHttpClient()

            val graphQLClient = GraphQLClient(okHttpClient, url = graphQLApi.baseUrl.toString())
            val apiClient = ApiClient(okHttpClient, url = restApi.baseUrl.toString())

            TApiInitResp(graphQLClient = graphQLClient, apiClient = apiClient)
        } catch (error: Error) {
            println("Validation error: $error")
            JSONObject()
        }
    }

    override suspend fun createPaymentSession(args: Map<String, Any?>?): String {
        val type: String =
            if (args != null && args.containsKey("type") && args["type"] != null) args["type"] as String else "Setup"

        val variables: Map<String, Any> = mapOf(
            "type" to type
        )

        val dataPath = "data.account.retrieveSession.data"
        val errorPath = "data.account.retrieveSession.error.message"

        return withContext(Dispatchers.IO) {
            try {
                val deferredApiResponse =
                    async { _graphQLClient?.query(retrieveSessionQuery, variables) }
                val apiResponse = deferredApiResponse.await()
                return@withContext apiResponse?.get(dataPath, errorPath) as String
            } catch (error: Throwable) {
                throw error
            }
        }
    }

    override suspend fun tokenize(args: Map<String, Any?>?): Map<String, Any> =
        withContext(Dispatchers.IO) {
            try {
                val parsedArgs = validateTokenizeArgs(args)
                val address = getAddressArguments(args)
                val tokenizeApiData: Map<String, Any?> = address?.toMap() ?: emptyMap()

                val apiResponse =
                    withContext(Dispatchers.Default) {
                        _apiClient?.postWithCardInfo(
                            path = "/tokenize",
                            data = tokenizeApiData,
                            headers = mapOf(
                                "Authorization" to "Bearer ${parsedArgs.paymentSessionToken}",
                                "Content-Type" to "application/json"
                            )
                        )
                    } ?: throw ApiClientError(
                        "Invalid Response",
                        "Got null from API call",
                        null,
                        null
                    )

                return@withContext apiResponse.getRawData()
            } catch (error: Throwable) {
                throw error
            }
        }


    private fun validateCardInfoArgs(args: Map<String, String>): Boolean {
        val argsKeys: Set<String> = args.keys.toSet()

        return argsKeys == this._getCardInfoArgsKeys
    }

    override suspend fun getCardInfo(args: Map<String, Any?>?): Map<String, Any> =
        withContext(Dispatchers.IO) {
            val parsedArgs = parseAnyMapToStringMap(args)

            if (!validateCardInfoArgs(parsedArgs)) {
                throw IllegalArgumentException("args provided are not valid, verify they contain all keys and values from TGetCardInfoArgs")
            }

            val paymentSessionToken: String = parsedArgs["paymentSessionToken"] as String
            val cardDigits: String = parsedArgs["cardDigits"] as String

            val apiResponse = withContext(Dispatchers.Default) {
                _apiClient?.post(
                    path = "/cardinfo",
                    data = mapOf("cardDigits" to cardDigits),
                    headers = mapOf(
                        "Authorization" to "Bearer $paymentSessionToken",
                        "Content-Type" to "application/json"
                    )
                )
            }

            val rawData = apiResponse?.getRawData()

            return@withContext TGetCardInfoData.parseResponse(rawData)
        }
}

private fun validateTokenizeArgs(args: Map<String, Any?>?): TTokenizeArgs {
    requireNotNull(args) { "Args are required" }
    // Validate restApi
    val addressArgs = getAddressArguments(args)

    if (!args.containsKey("paymentSessionToken") || args["paymentSessionToken"] !is String) {
        throw IllegalArgumentException("paymentSessionToken is a required param")
    }

    val paymentSessionToken: String = args["paymentSessionToken"] as String

    // Check if address information is provided
    if (args.containsKey("address") && args["address"] != null) {
        if (args["paymentSessionToken"] == null) {
            throw ValidationError("paymentSessionToken is a required param")
        }
        return if (addressArgs == null) {
            TTokenizeArgs(paymentSessionToken, null)
        } else {
            TTokenizeArgs(paymentSessionToken, addressArgs)
        }
    }

    return TTokenizeArgs(paymentSessionToken, null)
}

private fun getAddressArguments(args: Map<String, Any?>?): TTokenizeAddress? {
    if (args == null) {
        throw IllegalArgumentException("No args were provided for address validation")
    }

    if (args.containsKey("address") && args["address"] is Map<*, *>) {
        val address: Map<*, *> = args["address"] as Map<*, *>

        if (address["line1"] !is String) {
            throw ValidationError("line1 is a required param")
        }
        if (address["postalCode"] !is String) {
            throw ValidationError("postalCode is a required param")
        }

        if (address["line2"] !is String?) {
            throw ValidationError("optional argument line2 is not a valid string")
        }

        if (address["city"] !is String?) {
            throw ValidationError("optional argument city is not a valid string")
        }

        if (address["country"] !is String?) {
            throw ValidationError("optional argument country is not a valid string")
        }

        if (address["province"] !is String?) {
            throw ValidationError("optional argument province is not a valid string")
        }

        val line1 = address["line1"]
        val line2 = address["line2"]
        val city = address["city"]
        val country = address["country"]
        val province = address["province"]
        val postalCode = address["postalCode"]

        if (line1 != null && postalCode != null) {
            return TTokenizeAddress(
                line1 as String,
                line2 as String?,
                country as String?,
                province as String?,
                city as String?,
                postalCode as String
            )
        }
    } else {
        return null
    }
    return null
}


