package com.example.epsandroidlibrary

import java.io.Serializable
import java.net.URL
import kotlin.reflect.full.memberProperties

typealias THeaders = Map<String, Any>

data class TApiInitResp(val graphQLClient: GraphQLClient, val apiClient: ApiClient)


data class TApi(val headers: THeaders?, val baseUrl: String)

data class TArgs(val restApi: TApi, val graphQLApi: TApi)

data class TProcessPaymentArgs(
    val sessionToken: String,
    val accountNumber: String,
    val totalAmount: String,
    val subTotalAmount: String,
    val taxes: String
)

data class TTokenizeArgs(val paymentSessionToken: String, val address: TTokenizeAddress?)

data class TSchemaApiArgs(val baseUrl: URL, val headers: Map<String, String>?)

class TSchemaArgs(val restApi: TSchemaApiArgs, val graphQLApi: TSchemaApiArgs) {

    companion object {
        fun fromMap(args: Map<String, Any?>?): TSchemaArgs {
            requireNotNull(args)

            if (!args.containsKey("restApi")) {
                throw IllegalArgumentException("The args map does not contain required key restApi")
            }

            if (!args.containsKey("graphQLApi")) {
                throw IllegalArgumentException("The args map does not contain required key graphQLApi")
            }

            val restApi = args["restApi"]
            val graphQLApi = args["graphQLApi"]

            if (restApi !is Map<*, *>) {
                throw IllegalArgumentException("The restApi argument is not a valid Map")
            }

            if (graphQLApi !is Map<*, *>) {
                throw IllegalArgumentException("The graphQLApi argument is not a valid Map")
            }


            if (!restApi.containsKey("baseUrl") || restApi["baseUrl"] !is String) {
                throw IllegalArgumentException("restApi.baseUrl is not a valid String")
            }

            if (!graphQLApi.containsKey("baseUrl") || graphQLApi["baseUrl"] !is String) {
                throw IllegalArgumentException("graphQLApi.baseUrl is not a valid String")
            }

            if (restApi.containsKey("headers") && (restApi["headers"] != null || restApi["headers"] !is Map<*, *>)) {
                throw IllegalArgumentException("restApi.headers is not a valid Map")
            }

            if (graphQLApi.containsKey("headers") && (graphQLApi["headers"] != null || graphQLApi["headers"] !is Map<*, *>)) {
                throw IllegalArgumentException("graphQLApi.headers is not a valid Map")
            }


            val restApiBaseUrl: URL = URL(restApi["baseUrl"] as String)
            val graphQLApiBaseUrl: URL = URL(graphQLApi["baseUrl"] as String)

            val restApiHeaders =
                parseAnyMapToStringMap(if (restApi["headers"] != null) restApi["headers"] as Map<*, *> else null)

            val graphQLApiHeaders =
                parseAnyMapToStringMap(if (graphQLApi["headers"] != null) graphQLApi["headers"] as Map<*, *> else null)

            val restApiArgs = TSchemaApiArgs(restApiBaseUrl, restApiHeaders)
            val graphQLApiArgs = TSchemaApiArgs(graphQLApiBaseUrl, graphQLApiHeaders)

            return TSchemaArgs(restApiArgs, graphQLApiArgs)
        }
    }
}


data class TCardAddress(
    val line1: String,
    val line2: String,
    val country: String,
    val province: String,
    val city: String,
    val postalCode: String
)

val CARD_FUNDING_TYPES = listOf("credit", "debit", "prepaid", "unknown")
typealias TCardFundingType = String

val PAYMENT_DATA_TOTAL_AMOUNT_UNITS = listOf("CAD", "USD")
typealias TPaymentDataTotalAmountUnits = String

enum class TTokenizedDataResponse {
    matched, notMatched, notChecked
}

sealed class TCardInfoResp() : Serializable {
    data class Version1(val value: TCardInfoV1) : TCardInfoResp()
    data class Version2(val value: TCardInfoV2) : TCardInfoResp()
    data class Version3(val value: TCardInfoV3) : TCardInfoResp()
}


data class TTokenizedData(
    val id: String,
    val avsAddressResponse: TTokenizedDataResponse,
    val avsPostalCodeResponse: TTokenizedDataResponse,
    val cvvResponse: TTokenizedDataResponse,
    val clientId: String,
    val singleUse: Boolean,
    val cardInfo: TCardInfoResp
)

data class TTokenizedDataSchema(
    val id: String,
    val avsAddressResponse: TTokenizedDataResponse,
    val avsPostalCodeResponse: TTokenizedDataResponse,
    val cvvResponse: TTokenizedDataResponse,
    val clientId: String,
    val singleUse: Boolean
)

data class TSuccessfulProcessPaymentData(
    val id: String,
    val paymentDate: String,
    val status: String,
    val totalAmount: TProcessPaymentAmount
)

data class TCreatePaymentSessionArgs(val type: String?)

data class TProcessPaymentAmount(val units: TPaymentDataTotalAmountUnits, val value: Double)

interface TCardInfoProtocol : Serializable

data class TCardInfoV1(
    val brand: String,
    val bin: String,
    val countryOfIssue: String,
    val cardIssuer: String,
    val productId: String,
    val fundingType: TCardFundingType
) : TCardInfoProtocol

data class TCardInfoV2(
    val lastFourDigits: String,
    val expiryMonth: String,
    val expiryYear: String,
    val fingerprint: String,
    val address: TCardAddress
) : TCardInfoProtocol

data class TCardInfoV3(
    val cardBrand: String,
    val bin: String,
    val countryOfIssue: String,
    val productId: String?,
    val fundingType: String,
    val lastFourDigits: String,
    val expiryMonth: String,
    val expiryYear: String,
    val fingerprint: String
) : TCardInfoProtocol

data class TCardInfo(
    val cardNumber: Any?,
    val cvv: Any?,
    val expiryMonth: Any?,
    val expiryYear: Any?
)

fun TCardInfo.toMap(): Map<String, Any?> {
    return mapOf(
        "cardNumber" to cardNumber,
        "cvv" to cvv,
        "expiryMonth" to expiryMonth,
        "expiryYear" to expiryYear
    )
}

data class ErrorResponse(val code: String, val reason: String, val message: String)

fun parseAnyMapToStringMap(
    map: Map<*, *>?,
    mapName: String = "Readable map"
): Map<String, String> {
    val parsedMap: MutableMap<String, String> = mutableMapOf<String, String>()

    if (map == null) {
        return parsedMap
    }

    for ((key, value) in map) {
        if (key !is String) {
            throw IllegalArgumentException("$mapName key $key has an incorrect key type associated with it. Make sure all $mapName keys contain string keys")
        }

        if (value !is String) {
            throw IllegalArgumentException("$mapName key $key has an incorrect value type associated with it. Make sure all $mapName keys contain string values")
        }

        parsedMap[key] = map[key] as String
    }

    return parsedMap
}

data class TGetCardInfoArgs(
    val paymentSessionToken: String,
    val cardDigits: String,
)

data class TTokenizeAddress(
    val line1: String,
    val line2: String?,
    val country: String?,
    val province: String?,
    val city: String?,
    val postalCode: String
)

fun TTokenizeAddress.toMap(): Map<String, Any?> {
    return mapOf(
        "line1" to line1,
        "line2" to line2,
        "country" to country,
        "province" to province,
        "city" to city,
        "postalCode" to postalCode
    )
}

class TGetCardInfoData(
    val cardBrand: String,
    val bin: String,
    val countryOfIssue: String,
    val cardIssuer: String,
    val fundingType: TCardFundingType,
    productId: String?
) {

    companion object {
        private val properties: Set<String> = run {
            val getCardInfoArgsKeys: MutableSet<String> = mutableSetOf()
            for (property in TGetCardInfoData::class.memberProperties) {
                getCardInfoArgsKeys.add(property.name)
            }
            getCardInfoArgsKeys
        }

        fun parseResponse(response: Map<String, Any>?): Map<String, String> {
            if (response == null) {
                throw IllegalArgumentException("API response for POST /cardinfo is null")
            }
            val responseProperties = response.keys.toSet()
            val parsedResponse = mutableMapOf<String, String>()

            if (responseProperties != properties) {
                throw IllegalArgumentException("API response for POST /cardinfo do not contain all necessary fields $properties")
            }
            for ((key, value) in response) {
                if (value !is String) {
                    throw IllegalArgumentException("API response for POST /cardinfo contains invalid values for its fields")
                } else {
                    parsedResponse[key] = value
                }
            }
            return parsedResponse
        }
    }
}