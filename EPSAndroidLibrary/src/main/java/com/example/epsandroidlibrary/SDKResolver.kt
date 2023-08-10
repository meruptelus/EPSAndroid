package com.example.epsandroidlibrary

import kotlin.properties.Delegates

object SDKResolver {
    private val SDKs: MutableMap<String, SDKInterface> = mutableMapOf()
    var defaultSDK: SDKInterface = SDKMock()

    init {
        addSdk("mock", SDKMock())
    }

    var cardInfo: TCardInfo? by Delegates.observable(null) { _, _, newValue ->
        // Perform any necessary actions when cardInfo is updated
        // For example, you can notify observers or perform validation
    }

    fun updateCardInfo(card: TCardInfo) {
        cardInfo = card
    }

    fun getCardInfoValue():  TCardInfo? {
        return cardInfo
    }

    fun addSdk(key: String, sdk: SDKInterface) {
        SDKs[key] = sdk
    }

    fun removeSdk(key: String) {
        SDKs.remove(key)
    }

    fun setDefault(key: String) {
        defaultSDK = SDKs[key] ?: SDKMock()
    }

    inline fun <reified SDK : SDKInterface> getDefault(): SDK {
        return defaultSDK as? SDK ?: throw IllegalStateException("Default SDK is not of type ${SDK::class.simpleName}")
    }

    fun reset() {
        SDKs.clear()
        defaultSDK = SDKMock()
    }
}
