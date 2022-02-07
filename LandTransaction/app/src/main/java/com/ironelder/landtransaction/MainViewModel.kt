package com.ironelder.landtransaction

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import com.ironelder.landtransaction.model.ApartModel
import com.ironelder.landtransaction.model.ApartResultModel
import com.ironelder.landtransaction.model.city.CityData
import com.ironelder.landtransaction.model.city.CityModel
import com.ironelder.landtransaction.model.city.SigunguLi
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel(application:Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val apartRepository = ApartRepositoryImpl()

    private val _apartRealDealModel : MutableLiveData<List<ApartModel>> = MutableLiveData()
    val apartRealDealModel : LiveData<List<ApartModel>> = _apartRealDealModel

    private val _sidoListModel : MutableLiveData<List<CityData>> = MutableLiveData()
    val sidoListModel : LiveData<List<CityData>> = _sidoListModel

    private val _sigunguListModel : MutableLiveData<List<SigunguLi>> = MutableLiveData()
    val sigunguListModel : LiveData<List<SigunguLi>> = _sigunguListModel

    private val _sigunguSelectModel : MutableLiveData<SigunguLi> = MutableLiveData()
    val sigunguSelectModel : LiveData<SigunguLi> = _sigunguSelectModel

    val onItemClickEvent: MutableLiveData<ApartModel> = MutableLiveData()

    fun loadCityData(){
        val assetManager = context.assets
        val inputStream = assetManager.open(DEFAULT_PATH_SIDO)
        val cityData = inputStream.bufferedReader().use { it.readText() }
        val cityModel = Gson().fromJson(cityData.toString(), CityModel::class.java)

        cityModel.cityDatas.cityData.let {
            _sidoListModel.postValue(it)
        }
        Log.d("ironelderLandTest" , "return cityData = ${cityModel.toString()}")
    }

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

    fun onSidoItemSelect(sigunguList:List<SigunguLi>){
        _sigunguListModel.postValue(sigunguList)
    }

    fun onSigunguItemSelect(sigunguSelectItem:SigunguLi?) {
        _sigunguSelectModel.postValue(sigunguSelectItem)
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
