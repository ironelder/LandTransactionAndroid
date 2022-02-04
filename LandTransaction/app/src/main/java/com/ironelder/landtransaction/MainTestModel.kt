package com.ironelder.landtransaction

import java.util.*

data class MainTestModel(
    val id : String = UUID.randomUUID().toString(),
    val title : String = ""
)
