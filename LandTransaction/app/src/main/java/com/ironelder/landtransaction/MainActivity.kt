package com.ironelder.landtransaction

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.ironelder.landtransaction.model.ApartModel
import com.ironelder.landtransaction.model.city.CityData
import com.ironelder.landtransaction.model.city.SigunguLi
import com.ironelder.landtransaction.ui.theme.LandTransactionTheme
import com.ironelder.landtransaction.ui.theme.Purple500
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
            MainViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LandTransactionTheme {
                val apartDealModels =
                    viewModel.apartRealDealModel.observeAsState().value ?: emptyList()
                val sidoModels = viewModel.sidoListModel.observeAsState().value ?: emptyList()
                val sigunguModels = viewModel.sigunguListModel.observeAsState().value ?: emptyList()
                val sigunguSelectModel = viewModel.sigunguSelectModel.observeAsState().value
                val selectedDateText = viewModel.selectedDate.observeAsState().value

                viewModel.onItemClickEvent.observeAsState().value?.run {
                    ComposableToast(message = apartName)
                }
                Column {
                    TopFilterBar(
                        sidoList = sidoModels,
                        onSelectSido = {
                            viewModel.onSidoItemSelect(it)
                            viewModel.onSigunguItemSelect(null)
                                       },
                        sigunguList = sigunguModels,
                        sigunguSelectModel = sigunguSelectModel,
                        onSigunguSelecter = { viewModel.onSigunguItemSelect(it) },
                        onSearch = { viewModel.onSearch() },
                        dateButtonText = selectedDateText,
                        onDateSelected = { viewModel.onDateSelected(it) }
                    )
                    TestModelList(models = apartDealModels, onItemClick = viewModel::onItemClick)
                }


                // A surface container using the 'background' color from the theme
//                Surface(color = MaterialTheme.colors.background) {
//                    Greeting("Android")
//                }
            }
        }
//        viewModel.loadCityData()
//        viewModel.loadRealApartDealData()
    }
}

@Composable
fun TopFilterBar(
    sidoList: List<CityData> = emptyList(),
    onSelectSido: (cityData: CityData) -> Unit = {},
    sigunguList: List<SigunguLi> = emptyList(),
    sigunguSelectModel: SigunguLi? = null,
    onSigunguSelecter: (SigunguLi) -> Unit = {},
    onSearch: () -> Unit = {},
    dateButtonText:String?,
    onDateSelected: (date:String) -> Unit = {}
) {
    var dialogShow by remember { mutableStateOf(false) }
    FlowRow(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 2.dp, end = 2.dp),
        mainAxisAlignment = FlowMainAxisAlignment.Start,
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
        crossAxisSpacing = 2.dp
    ) {
        if (dialogShow) {
            DatePicker(
                onDateSelected = { localDate ->
                    val formatter = DateTimeFormatter.ofPattern("yyyy/MM")
                    println("onDateSelected = ${localDate.format(formatter)}")
                    onDateSelected.invoke(localDate.format(formatter))
                },
                onDismissRequest = {
                    println("onDismissRequest")
                    dialogShow = !dialogShow
                }
            )
        }
        Button(
            onClick = {
                dialogShow = !dialogShow
            },
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
        ){
            Text(text = dateButtonText ?: "Date")
        }
        Spacer(modifier = Modifier.width(5.dp))
        SidoDropDownMenu(sidoList = sidoList, onItemClick = onSelectSido)
        Spacer(modifier = Modifier.width(5.dp))
        SigunguDropDownMenu(sigunguList = sigunguList, sigunguSelectModel = sigunguSelectModel, onSigunguSelecter = onSigunguSelecter)
        Spacer(modifier = Modifier.width(5.dp))
        IconButton(
            onClick = onSearch,
            modifier = Modifier
                .border(1.dp, Purple500, shape = CircleShape)
                .width(30.dp)
                .height(30.dp)
                .padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
        ) {
            Icon(
                Icons.Filled.Search,
                "",
                tint = Purple500
            )
        }
    }
}

@Composable
fun SidoDropDownMenu(
    sidoList: List<CityData>,
    onItemClick: (cityData: CityData) -> Unit = {}
) {
    var isSidoDropDownMenuExpanded by remember { mutableStateOf(false) }
    var sidoDropDownText by remember { mutableStateOf("???/???") }
    Button(
        onClick = { isSidoDropDownMenuExpanded = !isSidoDropDownMenuExpanded },
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
    ) {
        Text(text = sidoDropDownText)
    }

    DropdownMenu(
        modifier = Modifier.wrapContentSize(),
        expanded = isSidoDropDownMenuExpanded,
        onDismissRequest = { isSidoDropDownMenuExpanded = false }) {

        sidoList.forEach { cityData ->
            DropdownMenuItem(onClick = {
                println("name = ${cityData.sido_cd}")
                sidoDropDownText = cityData.sido_nm
                isSidoDropDownMenuExpanded = !isSidoDropDownMenuExpanded
                onItemClick.invoke(cityData)
            }) {
                Text(text = cityData.sido_nm)
            }
        }
    }
}

@Composable
fun SigunguDropDownMenu(sigunguList: List<SigunguLi> = emptyList(),
                        sigunguSelectModel:SigunguLi? = null,
                        onSigunguSelecter:(SigunguLi) -> Unit = {}) {
    var isSigunguDropDownMenuExpanded by remember { mutableStateOf(false) }
    Button(
        onClick = { isSigunguDropDownMenuExpanded = !isSigunguDropDownMenuExpanded },
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
    ) {
        Text(text = sigunguSelectModel?.sigungu_nm ?: "???/???/???" )
    }

    DropdownMenu(
        modifier = Modifier.wrapContentSize(),
        expanded = isSigunguDropDownMenuExpanded,
        onDismissRequest = { isSigunguDropDownMenuExpanded = false }) {

        sigunguList.forEach { sigunguLi ->
            DropdownMenuItem(onClick = {
                println("Test = ${sigunguLi.sigungu_cd}")
                isSigunguDropDownMenuExpanded = false
                onSigunguSelecter.invoke(sigunguLi)
            }) {
                Text(text = sigunguLi.sigungu_nm)
            }
        }
    }
}

@Composable
fun TestModelList(models: List<ApartModel>, onItemClick: (Int) -> Unit = {}) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(
            items = models
        ) { index, item ->
            TestModelListItem(model = item, onClick = { onItemClick.invoke(index) })
        }
    }
}

@Composable
fun TestModelListItem(model: ApartModel, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.primary),
        backgroundColor = Color.White,
        contentColor = MaterialTheme.colors.primary,
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Text(text = model.apartName, style = MaterialTheme.typography.h3)
    }
}

@Composable
fun ComposableToast(message: String) {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LandTransactionTheme {
        Greeting("Android")
    }
}