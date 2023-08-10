package com.example.epsandroidlibrary

interface SDKInterface {
    fun initSDK(args: Map<String, Any?>?): Any

    suspend fun createPaymentSession(args: Map<String, Any?>?): String

    suspend fun tokenize(args: Map<String, Any?>?): Map<String, Any>

    suspend fun getCardInfo(args: Map<String, Any?>?): Map<String, Any>
}
