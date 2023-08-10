package com.example.epsandroidlibrary

const val MOCK_TOKEN = "."

val MOCK_VALID_CARD_INFO = mapOf(
    "cardNumber" to "5477160000007836",
    "cvv" to "422",
    "expiryMonth" to "04",
    "expiryYear" to "24"
)

val MOCK_VALID_CARD_INFO_V3 = mapOf(
    "cardBrand" to "mastercard",
    "bin" to "547716",
    "countryOfIssue" to "124",
    "productId" to "",
    "fundingType" to "credit",
    "lastFourDigits" to "7836",
    "expiryMonth" to "04",
    "expiryYear" to "24",
    "fingerprint" to "01H2ZY3TZY47RY7K031NVXDQAB"
)

val MOCK_NOT_VALID_CARD_INFO_V3 = mapOf(
    "cardBrand" to "mastercard",
    "bin" to "547716",
    "countryOfIssue" to "124",
    "productId" to "",
    "fundingType" to "",
    "lastFourDigits" to "7836",
    "expiryMonth" to "04",
    "expiryYear" to "24",
    "fingerprint" to "01H2ZY3TZY47RY7K031NVXDQAB"
)

val MOCK_VALID_CARD_INFO_V2 = mapOf(
    "lastFourDigits" to "3434",
    "expiryMonth" to "12",
    "expiryYear" to "25",
    "fingerprint" to "mockFingerprint",
    "address" to mapOf(
        "line1" to "randomLine1",
        "line2" to "randomLine2",
        "country" to "randomCountry",
        "province" to "randomProvince",
        "city" to "randomCity",
        "postalCode" to "randomPostalCode"
    )
)

val MOCK_NOT_VALID_CARD_INFO_V2 = mapOf(
    "lastFourDigits" to "",
    "expiryMonth" to "12",
    "expiryYear" to "25",
    "fingerprint" to "mockFingerprint",
    "address" to mapOf(
        "line1" to "randomLine1",
        "line2" to "randomLine2",
        "country" to "randomCountry",
        "province" to "randomProvince",
        "city" to "randomCity",
        "postalCode" to "randomPostalCode"
    )
)

val MOCK_VALID_CARD_INFO_V1 = mapOf(
    "brand" to "randomBrand",
    "bin" to "randomBin",
    "countryOfIssue" to "randomCountry",
    "cardIssuer" to "randomIssuer",
    "productId" to "randomProductId",
    "fundingType" to "credit"
)

val MOCK_NOT_VALID_CARD_INFO_V1 = mapOf(
    "brand" to "randomBrand",
    "bin" to "randomBin",
    "countryOfIssue" to "randomCountry",
    "cardIssuer" to "randomIssuer",
    "productId" to "",
    "fundingType" to "credit"
)

val MOCK_NOT_VALID_CARD_INFO = mapOf(
    "cardNumber" to "346923005050501",
    "cvv" to "1234",
    "expiryMonth" to "12",
    "expiryYear" to "25"
)

val MOCK_VALID_TOKENIZE_ARGS = mapOf(
    "paymentSessionToken" to MOCK_TOKEN
)

val MOCK_NOT_VALID_TOKENIZE_ARGS = mapOf(
    "paymentSessionToken" to MOCK_TOKEN
)

val MOCK_VALID_PROCESS_PAYMENT_ARGS = mapOf(
    "sessionToken" to "randomToken",
    "accountNumber" to "10000002079476",
    "totalAmount" to "40",
    "subTotalAmount" to "39",
    "taxes" to "1"
)

val MOCK_NOT_VALID_PROCESS_PAYMENT_ARGS = mapOf(
    "sessionToken" to "randomToken",
    "accountNumber" to "",
    "totalAmount" to "40",
    "subTotalAmount" to "39",
    "taxes" to "1"
)

val MOCK_VALID_RETRIEVE_TOKEN_RESPONSE = mapOf(
    "data" to mapOf(
        "account" to mapOf(
            "retrieveSession" to mapOf(
                "data" to MOCK_TOKEN
            )
        )
    )
)

val MOCK_REST_API_URL = "https://www.testurl_rest_random_mock.com"
val MOCK_GRAPHQL_API_URL = "https://www.testurl_grapqhl_random_mock.com"

val MOCK_VALID_TOKENIZE_RESPONSE = mapOf(
    "id" to "01H5NASJY99Y2K9BN8SGS0W360",
    "avsAddressResponse" to "notChecked",
    "avsPostalCodeResponse" to "notChecked",
    "cvvResponse" to "notChecked",
    "clientId" to "optiva-public",
    "singleUse" to true,
    "cardInfo" to mapOf(
        "cardBrand" to "AMEX",
        "bin" to "547716",
        "countryOfIssue" to "124",
        "productId" to "",
        "fundingType" to "credit",
        "lastFourDigits" to "3434",
        "expiryMonth" to "01",
        "expiryYear" to "23",
        "fingerprint" to "01H5JBSJC9REDTK3DA0M1WSKWB"
    )
)

val MOCK_PROCESS_PAYMENT_DATA = mapOf(
    "id" to "01H5QQ24TM3NP5A72D7XQDSCY3",
    "paymentDate" to "2023-07-19T18:37:08.820Z",
    "status" to "succeeded",
    "totalAmount" to mapOf("units" to "CAD", "value" to 40.5)
)

val MOCK_PROCESS_PAYMENT_RESPONSE = mapOf(
    "data" to mapOf(
        "account" to mapOf(
            "processPayment" to mapOf(
                "data" to mapOf(
                    "id" to "01H5T0FF3S82MY1A03RKFC7462",
                    "paymentDate" to "2023-07-20T16:00:11.385Z",
                    "status" to "succeeded",
                    "totalAmount" to mapOf("units" to "CAD", "value" to 1000),
                    "statusReason" to "",
                    "errorCode" to ""
                ),
                "error" to null
            )
        )
    )
)

val MOCK_PROCESS_PAYMENT_WRONG_SCHEMA_RESPONSE = mapOf(
    "data" to mapOf(
        "account" to mapOf(
            "processPayment" to mapOf(
                "data" to mapOf(
                    "paymentDate" to "2023-07-20T16:00:11.385Z",
                    "status" to "succeeded",
                    "totalAmount" to mapOf("units" to "CAD", "value" to 1000),
                    "statusReason" to "",
                    "errorCode" to ""
                ),
                "error" to null
            )
        )
    )
)

val MOCK_NOT_VALID_PROCESS_PAYMENT_RESPONSE = mapOf(
    "data" to mapOf(
        "account" to mapOf(
            "processPayment" to mapOf(
                "data" to mapOf(
                    "id" to "01H5T0FF3S82MY1A03RKFC7462",
                    "paymentDate" to "2023-07-20T16:00:11.385Z",
                    "status" to "declined",
                    "totalAmount" to mapOf("units" to "CAD", "value" to 1000),
                    "errorCode" to "40026",
                    "statusReason" to "Unable to find specified resource / specified payment method token is expired"
                ),
                "error" to null
            )
        )
    )
)

val MOCK_NOT_VALID_REST_API_URL = "random_string"

val MOCK_VALID_GET_CARD_INFO_RESPONSE = mapOf(
    "cardBrand" to "visa",
    "bin" to "424242",
    "countryOfIssue" to "826",
    "cardIssuer" to "424242",
    "fundingType" to "credit",
)

val MOCK_VALID_GET_CARD_INFO_ARGS = mapOf(
    "paymentSessionToken" to MOCK_TOKEN,
    "cardDigits" to "424242"
)
