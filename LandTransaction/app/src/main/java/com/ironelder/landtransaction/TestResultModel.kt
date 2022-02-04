package com.ironelder.landtransaction

data class TestResultModel(
    val response: Response
)

data class Response(
    val header: Header
)

data class Header(
    val resultCode: String,
    val resultMsg: String
)