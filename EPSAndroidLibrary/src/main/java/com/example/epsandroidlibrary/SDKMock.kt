package com.example.epsandroidlibrary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SDKMock : SDKInterface {

    override fun initSDK(args: Map<String, Any?>?): Any {
        return emptyMap<String, Any>()
    }

    override suspend fun createPaymentSession(args: Map<String, Any?>?): String {
        return withContext(Dispatchers.Default) {
            MOCK_TOKEN
        }
    }

    override suspend fun tokenize(args: Map<String, Any?>?): Map<String, Any> {
        return withContext(Dispatchers.Default) {
            MOCK_VALID_TOKENIZE_RESPONSE
        }
    }

    override suspend fun getCardInfo(args: Map<String, Any?>?): Map<String, Any> =
        withContext(Dispatchers.Default) {
            MOCK_VALID_GET_CARD_INFO_RESPONSE
        }
}
