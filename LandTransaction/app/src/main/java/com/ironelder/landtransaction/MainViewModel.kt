package com.ironelder.landtransaction

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.google.gson.Gson
import com.ironelder.landtransaction.model.*
import com.ironelder.landtransaction.model.city.CityData
import com.ironelder.landtransaction.model.city.CityModel
import com.ironelder.landtransaction.model.city.SigunguLi
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@SuppressLint("SimpleDateFormat")
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

    private val _selectedDate : MutableLiveData<String> = MutableLiveData()
    val selectedDate : LiveData<String> = _selectedDate

    val onItemClickEvent: MutableLiveData<ApartModel> = MutableLiveData()

    var cityCode = "11"
    var citySubCode = "680"

    init {
        val searchSelectedDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("yyyy/MM")
            LocalDate.now().format(formatter)
        } else {
            val pattern = "yyyy/MM"
            val dateFormat = SimpleDateFormat(pattern)
            dateFormat.format(Date())
        }
        val landCode = cityCode + citySubCode
        _selectedDate.postValue(searchSelectedDate)
        loadCityData()
        loadRealApartDealData(landCode = landCode.toInt(),dealDate = searchSelectedDate.replace("/","").toInt())
    }

    fun loadCityData(){
        viewModelScope.launch(Dispatchers.IO){
            val assetManager = context.assets
            val inputStream = assetManager.open(DEFAULT_PATH_SIDO)
            val cityData = inputStream.bufferedReader().use { it.readText() }
            val cityModel = Gson().fromJson(cityData.toString(), CityModel::class.java)

            cityModel.cityDatas.cityData.let {
                _sidoListModel.postValue(it)
            }
            Log.d("ironelderLandTest" , "return cityData = ${cityModel.toString()}")
        }
    }

    fun loadRealApartDealData(landCode:Int = 41173, dealDate:Int = 202201) {
        viewModelScope.launch(Dispatchers.IO) {
            apartRepository.getApartRealDealData(
                landCode = landCode,
                dealDate = dealDate
            ).let { result ->
                val xmlToJson = XmlToJson.Builder(result).build()
//                Log.d("ironelderLandTest" , "return xmlToJson = ${xmlToJson.toString()}")
                val resultCode = xmlToJson.toJson()
                    ?.getJSONObject("response")
                    ?.getJSONObject("header")
                    ?.getString("resultCode")
                val resultTotalCnt = xmlToJson.toJson()
                    ?.getJSONObject("response")
                    ?.getJSONObject("body")
                    ?.getString("totalCount")?.toInt() ?: 0

                when(resultCode) {
                    "00" -> {
                        when(resultTotalCnt) {
                            0 -> {
                                _apartRealDealModel.postValue(emptyList())
                            }
                            1 -> {
                                val apartResultModel = Gson().fromJson(xmlToJson.toJson().toString(), ApartResultSingleModel::class.java)
                                _apartRealDealModel.postValue(arrayListOf(apartResultModel.response.body.items.ApartModelSingle))
                            }
                            else -> {
                                val apartResultModel = Gson().fromJson(xmlToJson.toJson().toString(), ApartResultModel::class.java)
                                _apartRealDealModel.postValue(apartResultModel.response.body.items.ApartModelList)
                            }
                        }
                    }
                    else -> {
                        //Error
                    }
                }
            }
        }
    }

    fun onItemClick(position: Int) {
        _apartRealDealModel.value?.getOrNull(position)?.let { onItemClickEvent.postValue(it) }
    }

    fun onSidoItemSelect(cityData:CityData){
        cityCode = cityData.sido_cd
        _sigunguListModel.postValue(cityData.sigungu_li)
    }

    fun onSigunguItemSelect(sigunguSelectItem:SigunguLi?) {
        citySubCode = sigunguSelectItem?.sigungu_cd ?: "680"
        _sigunguSelectModel.postValue(sigunguSelectItem)
    }

    fun onDateSelected(date:String) {
        _selectedDate.postValue(date)
    }

    fun onSearch() {
        val landCode = cityCode + citySubCode
        loadRealApartDealData(landCode = landCode.toInt(), dealDate = _selectedDate.value?.replace("/","")?.toInt() ?: 0)
    }

}

interface ApartRepository {
    suspend fun getApartRealDealData(landCode:Int, dealDate:Int): String
}

class ApartRepositoryImpl:ApartRepository {
    private val apiService = NetworkService().getService()
    override suspend fun getApartRealDealData(landCode:Int, dealDate:Int): String {
        return apiService.getApartRealDealData(landCode = landCode, dealDate = dealDate)
    }
}
