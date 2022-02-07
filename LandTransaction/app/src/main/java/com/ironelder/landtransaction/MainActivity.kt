package com.ironelder.landtransaction

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.ironelder.landtransaction.model.ApartModel
import com.ironelder.landtransaction.model.city.CityData
import com.ironelder.landtransaction.model.city.SigunguLi
import com.ironelder.landtransaction.ui.theme.LandTransactionTheme

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

                viewModel.onItemClickEvent.observeAsState().value?.run {
                    ComposableToast(message = apartName)
                }
                Column {
                    FilterDropBox(
                        sidoList = sidoModels,
                        onSelectSido = {
                            viewModel.onSidoItemSelect(it)
                            viewModel.onSigunguItemSelect(null)
                                       },
                        sigunguList = sigunguModels,
                        sigunguSelectModel = sigunguSelectModel,
                        onSigunguSelecter = { viewModel.onSigunguItemSelect(it) }
                    )
                    TestModelList(models = apartDealModels, onItemClick = viewModel::onItemClick)
                }


                // A surface container using the 'background' color from the theme
//                Surface(color = MaterialTheme.colors.background) {
//                    Greeting("Android")
//                }
            }
        }
        viewModel.loadCityData()
        viewModel.loadRealApartDealData()
    }
}

@Composable
fun FilterDropBox(
    sidoList: List<CityData> = emptyList(),
    onSelectSido: (sigunguList: List<SigunguLi>) -> Unit = {},
    sigunguList: List<SigunguLi> = emptyList(),
    sigunguSelectModel: SigunguLi? = null,
    onSigunguSelecter: (SigunguLi) -> Unit = {}
) {
    Row(modifier = Modifier.padding(10.dp)) {
        SidoDropDownMenu(sidoList = sidoList, onItemClick = onSelectSido)
        Spacer(modifier = Modifier.width(5.dp))
        SigunguDropDownMenu(sigunguList = sigunguList, sigunguSelectModel = sigunguSelectModel, onSigunguSelecter = onSigunguSelecter)
    }
}

@Composable
fun SidoDropDownMenu(
    sidoList: List<CityData>,
    onItemClick: (sigunguList: List<SigunguLi>) -> Unit = {}
) {
    var isSidoDropDownMenuExpanded by remember { mutableStateOf(false) }
    var sidoDropDownText by remember { mutableStateOf("시/도") }
    Button(
        onClick = { isSidoDropDownMenuExpanded = !isSidoDropDownMenuExpanded }
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
                onItemClick(cityData.sigungu_li)
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
    ) {
        Text(text = sigunguSelectModel?.sigungu_nm ?: "시/군/구" )
    }

    DropdownMenu(
        modifier = Modifier.wrapContentSize(),
        expanded = isSigunguDropDownMenuExpanded,
        onDismissRequest = { isSigunguDropDownMenuExpanded = false }) {

        sigunguList.forEach { sigunguLi ->
            DropdownMenuItem(onClick = {
                println("Test = ${sigunguLi.sigungu_cd}")
                isSigunguDropDownMenuExpanded = false
                onSigunguSelecter(sigunguLi)
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