package com.example.epsandroidlibrary

enum class ErrorName(val rawValue: String) {
    REQUEST_FAILED("REQUEST_FAILED"),
    UNKNOWN_ERROR("UNKNOWN_ERROR"),
    PROCESS_PAYMENT_ERROR("PROCESS_PAYMENT_ERROR"),
    FIELD_NOT_FOUND("FIELD_NOT_FOUND"),
    UNEXPECTED_RESPONSE("UNEXPECTED_RESPONSE");
    override fun toString(): String {
        return rawValue
    }
}


