package com.spassdi.walletspfw.model.api

data class Response<T: Any>(
    val isSuccessful: Boolean,
    val msg: String,
    val stateWord: Int,
    val data: T? = null
)

data class EmptyData(
    val msg: String = "",
    val stateWord: Int = 0
)
