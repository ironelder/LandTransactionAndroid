package com.ironelder.landtransaction.model

import com.google.gson.annotations.SerializedName

data class ApartResultModel(
    val response: Response
)

data class Response(
    val body: Body,
    val header: Header
)

data class Header(
    val resultCode: String,
    val resultMsg: String
)

data class Body(
    val items: ApartModels,
    val numOfRows: String,
    val pageNo: String,
    val totalCount: String
)

data class ApartModels(
    @SerializedName("item")
    val ApartModelList: List<ApartModel>
)

data class ApartModel(
    @SerializedName("거래금액")
    val dealPrice: String,

    @SerializedName("거래유형")
    val dealType: String,

    @SerializedName("건축년도")
    val buildYear: String,

    @SerializedName("년")
    val dealYear: String,

    @SerializedName("법정동")
    val apartLocation: String,

    @SerializedName("아파트")
    val apartName: String,

    @SerializedName("월")
    val dealMonth: String,

    @SerializedName("일")
    val dealDay: String,

    @SerializedName("전용면적")
    val Exclusive: String,

    @SerializedName("중개사소재지")
    val dealerLocation: String,

    @SerializedName("지번")
    val postNumber: String,

    @SerializedName("지역코드")
    val locationCode: String,

    @SerializedName("층")
    val floor: String,

    @SerializedName("해제사유발생일")
    val cancelDate: String,

    @SerializedName("해제여부")
    val cancelType: String
)