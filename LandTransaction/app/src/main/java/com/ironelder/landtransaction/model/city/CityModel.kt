package com.ironelder.landtransaction.model.city

data class CityModel(
    val cityDatas: CityDatas
)

data class CityDatas(
    val cityData: List<CityData>
)

data class CityData(
    val sido_cd: String,
    val sido_nm: String,
    val sigungu_li: List<SigunguLi>
)

data class SigunguLi(
    val sigungu_cd: String,
    val sigungu_nm: String
)