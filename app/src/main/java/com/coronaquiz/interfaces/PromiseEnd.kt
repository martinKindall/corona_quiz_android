package com.coronaquiz.interfaces

interface PromiseEnd {
    fun onError(error: Exception?)
    fun finally()
}