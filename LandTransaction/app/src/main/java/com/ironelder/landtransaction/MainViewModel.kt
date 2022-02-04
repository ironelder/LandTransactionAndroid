package com.ironelder.landtransaction

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ironelder.landtransaction.model.ApartModel
import com.ironelder.landtransaction.model.ApartResultModel
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private val apartRepository = ApartRepositoryImpl()

    private val _apartRealDealModel : MutableLiveData<List<ApartModel>> = MutableLiveData()
    val apartRealDealModel : LiveData<List<ApartModel>> = _apartRealDealModel

    val onItemClickEvent: MutableLiveData<ApartModel> = MutableLiveData()

    fun loadRealApartDealData() {
        viewModelScope.launch(Dispatchers.IO) {
            apartRepository.getApartRealDealData().let { result ->
                val xmlToJson = XmlToJson.Builder(result).build()
                val apartResultModel = Gson().fromJson(xmlToJson.toJson().toString(), ApartResultModel::class.java)
                val resultCode = apartResultModel.response.header.resultCode
                val resultMessage = apartResultModel.response.header.resultMsg
                Log.d("ironelderLandTest" , "return xmlToJson = ${xmlToJson.toString()}")
                Log.d("ironelderLandTest" , "return resultCode = ${resultCode.toString()}")
                Log.d("ironelderLandTest" , "return resultMessage = ${resultMessage.toString()}")
                Log.d("ironelderLandTest" , "return count = ${apartResultModel.response.body.items.ApartModelList.size}")
                when(resultCode) {
                    "00" -> {
                        _apartRealDealModel.postValue(apartResultModel.response.body.items.ApartModelList)
                    }
                    else -> {

                    }
                }
                /*
                val apartResultModel =
                    Gson().fromJson(xmlToJson.toJson().toString(), VolunteerListModel::class.java)
                if (apartResultModel.response.body.items.item.isNotEmpty()) {
                    val list = apartResultModel.response.body.items.item
                    if (list.size < 20) {
                        isEnd = true
                    }
                    _volunteerDataList.postValue(list)
                }
                 */
            }
        }
    }

    fun onItemClick(position: Int) {
        _apartRealDealModel.value?.getOrNull(position)?.let { onItemClickEvent.postValue(it) }
    }


}

interface ApartRepository {
    suspend fun getApartRealDealData(): String
}

class ApartRepositoryImpl:ApartRepository {
    private val apiService = NetworkService().getService()
    override suspend fun getApartRealDealData(): String {
        return apiService.getApartRealDealData()
    }
}