package com.ucrconductors.model

interface NetworkChangeListener {
    fun onNetworkAvailable()
    fun onNetworkUnavailable()
}